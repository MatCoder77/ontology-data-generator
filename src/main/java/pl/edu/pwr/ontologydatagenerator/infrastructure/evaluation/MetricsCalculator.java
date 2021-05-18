package pl.edu.pwr.ontologydatagenerator.infrastructure.evaluation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.IdentifierMapper;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsCalculator {

    private static final String TAB_SIZE_PROPERTY_PATTERN = "app.generator.pdgf.datageneration.tables.{0}";

    @Value("${app.generator.pdgf.datageneration.size}") private final Long baseSize;
    @Value("${app.generator.pdgf.datageneration.scalefactor}") private final Long scaleFactor;
    private final Environment environment;
    private final IdentifierMapper identifierMapper;
    private final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

    public void calculateMetrics(OntologyContainer<OWLOntology> container) {
        log.info("============================= METRICS ==============================");
        log.info("Number of concepts: {}", container.getConcepts().size());
        log.info("Number of data properties: {}", container.getOntology().getDataPropertiesInSignature().size());
        log.info("Number of object properties: {}", container.getOntology().getObjectPropertiesInSignature().size());
        log.info("Number of instances: {}", container.getOntology().getIndividualsInSignature().size());
        log.info("Average population: {}", calculateAveragePopulation(container));
        log.info("Class richness: {}", calculateClassRitchness(container));
        log.info("---------------------------------------------------------------------");
        log.info("Class fulness: ");
        container.getConcepts().values().forEach(concept -> {
            double fulness = calculateClassFulness(concept);
            log.info("{}: {}", concept.getName(), fulness);
        });
        log.info("---------------------------------------------------------------------");
        log.info("Relation reachness: ");
        container.getConcepts().values().forEach(concept -> {
            double relationReachness = calculateRelationReachness(concept, container);
            log.info("{}: {}", concept.getName(), relationReachness);
        });
    }

    public double calculateAveragePopulation(OntologyContainer<OWLOntology> container) {
        double conceptsCount = container.getConcepts().size();
        double instanceCount = container.getOntology().getIndividualsInSignature().size();
        return instanceCount / conceptsCount;
    }

    public double calculateClassRitchness(OntologyContainer<OWLOntology> container) {
        double conceptsCount = container.getConcepts().size();
        double conceptsWithInscancesCount = container.getConcepts().entrySet().stream()
                .filter(concept -> !concept.getValue().getInstances().isEmpty())
                .collect(Collectors.toList())
                .size();
        return conceptsWithInscancesCount / conceptsCount;
    }

    public double calculateClassFulness(Concept concept) {
        double instancesCount = concept.getInstances().size();
        double expectedInstancesCount = getTableSize(concept.getName()) + getExpectedInstancesCountForSubclasses(concept);
        return instancesCount / expectedInstancesCount;
    }

    private long getExpectedInstancesCountForSubclasses(Concept concept) {
        return concept.getSubConcepts().stream()
                .map(Identifier::getName)
                .filter(name -> !"Nothing".equals(name))
                .map(this::getTableSize)
                .reduce(0L, Long::sum);
    }

    public double calculateRelationReachness(Concept concept, OntologyContainer<OWLOntology> container) {
        double propertiesCount = concept.getDataProperties().size() + concept.getObjectProperties().size();
        Map<Identifier, OWLClassExpression> classesByIdentifier = getClassesByIdentifier(container);
        List<OWLIndividual> individuals = container.getOntology().getClassAssertionAxioms(classesByIdentifier.get(concept.getIdentifier())).stream()
                .map(OWLClassAssertionAxiom::getIndividual)
                .collect(Collectors.toList());
        double dataPropertyCount = individuals.stream()
                .map(instance -> container.getOntology().getDataPropertyAssertionAxioms(instance))
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .size();
        double objectPropertyCount = individuals.stream()
                .map(instance -> container.getOntology().getObjectPropertyAssertionAxioms(instance))
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .size();
        return (dataPropertyCount / individuals.size() + objectPropertyCount / individuals.size()) / propertiesCount;
    }

    private Map<Identifier, OWLNamedIndividual> getIndividualsByIdentifier(OntologyContainer<OWLOntology> container) {
        return container.getOntology().getIndividualsInSignature().stream()
                .collect(Collectors.toMap(identifierMapper::mapToIdentifier, Function.identity()));
    }

    private Map<Identifier, OWLClassExpression> getClassesByIdentifier(OntologyContainer<OWLOntology> container) {
        return container.getOntology().getClassesInSignature().stream()
                .collect(Collectors.toMap(identifierMapper::mapToIdentifier, Function.identity()));
    }

    private Long getTableSize(String conceptName) {
        Long tableSize = getPropertyValue(MessageFormat.format(TAB_SIZE_PROPERTY_PATTERN, conceptName), Long.class)
                .orElse(baseSize);
        return tableSize * scaleFactor;
    }

    private <T> Optional<T> getPropertyValue(String path, Class<T> targetClass) {
        return Optional.ofNullable(environment.getProperty(path, targetClass));
    }
}
