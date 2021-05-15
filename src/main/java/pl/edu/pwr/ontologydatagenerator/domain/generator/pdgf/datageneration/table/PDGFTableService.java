package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.table;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Table;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.field.PDGFFieldService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.infrastructure.collection.CollectionUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDGFTableService {

    private static final String TAB_SIZE_PATTERN = "{0} * $'{'SF'}'";
    private static final String TAB_SIZE_PROPERTY_PATTERN = "app.generator.pdgf.datageneration.tables.{0}";

    @Value("${app.generator.pdgf.datageneration.size}") private final String baseSize;
    private final PDGFFieldService fieldService;
    private final Environment environment;

    public List<Table> getTables(OntologyContainer<OWLOntology> ontologyContainer) {
        List<Concept> conceptsToInstantiate = getConceptsToInstantiate(ontologyContainer);
        return conceptsToInstantiate.stream()
                .map(concept -> getTable(concept, conceptsToInstantiate, ontologyContainer))
                .collect(Collectors.toList());
    }

    private List<Concept> getConceptsToInstantiate(OntologyContainer<OWLOntology> ontologyContainer) {
        return ontologyContainer.getConcepts().values().stream()
                .filter(CollectionUtils.distinctBy(Concept::getEquivalentConcepts))
                .filter(this::shouldConnceptBeInstantiated)
                .collect(Collectors.toList());
    }

    private boolean shouldConnceptBeInstantiated(Concept concept) {
        return !isThingOrNothing(concept);
    }

    private boolean isThingOrNothing(Concept concept) {
        return concept.isThing() || concept.isNothing();
    }

    private Table getTable(Concept concept, Collection<Concept> conceptsToInstatniate, OntologyContainer<OWLOntology> container) {
        return new Table()
                .withName(concept.getName())
                .withSize(getTableSize(concept))
                .withField(fieldService.getFields(concept, conceptsToInstatniate, container));
    }

    private String getTableSize(Concept concept) {
        String tableSize = getPropertyValue(MessageFormat.format(TAB_SIZE_PROPERTY_PATTERN, concept.getName()), String.class)
                .orElse(baseSize);
        return MessageFormat.format(TAB_SIZE_PATTERN, tableSize);
    }

    private <T> Optional<T> getPropertyValue(String path, Class<T> targetClass) {
        return Optional.ofNullable(environment.getProperty(path, targetClass));
    }

}
