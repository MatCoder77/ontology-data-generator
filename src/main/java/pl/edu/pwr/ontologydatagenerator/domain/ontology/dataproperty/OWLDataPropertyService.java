package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.IdentifierMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OWLDataPropertyService {

    private final IdentifierMapper identifierMapper;

    public List<DataProperty> getDataProperties(OWLDataFactory dataFactory, OWLReasoner reasoner) {
        Set<OWLDataProperty> dataProperties = reasoner.getRootOntology().getDataPropertiesInSignature();
        Set<OWLNamedIndividual> individuals = reasoner.getRootOntology().getIndividualsInSignature();
        return dataProperties.stream()
                .map(dataProperty -> getDataProperty(dataProperty, individuals, dataFactory, reasoner))
                .collect(Collectors.toList());
    }

    private DataProperty getDataProperty(OWLDataProperty dataProperty, Collection<OWLNamedIndividual> individuals, OWLDataFactory dataFactory, OWLReasoner reasoner) {
        Identifier identifier = identifierMapper.mapToIdentifier(dataProperty);
        DataPropertyDomain domain = getDataPropertyDomain(dataProperty, reasoner);
        DataPropertyRange range = getDataPropertyRange(dataProperty, dataFactory, reasoner);
        Set<Identifier> equivalentProperties = getDataPropertyEquivalentProperties(dataProperty, reasoner);
        Set<Identifier> disjointProperties = getDataPropertyDisjointProperties(dataProperty, reasoner);
        Set<Identifier> superProperties = getDataPropertySuperProperties(dataProperty, reasoner);
        Set<Identifier> subProperties = getDataPropertySubProperties(dataProperty, reasoner);
        Map<Identifier, Set<OWLLiteral>> valuesByIndividualIdentifier = getValuesByIndividualIdentifier(dataProperty, individuals, reasoner);
        boolean isFunctional = isFunctionalDataProperty(dataProperty, reasoner.getRootOntology());
        return DataProperty.builder()
                .withIdentifier(identifier)
                .withDomain(domain)
                .withRange(range)
                .withEquivalentProperties(equivalentProperties)
                .withDisjointProperties(disjointProperties)
                .withSuperProperties(superProperties)
                .withSubProperties(subProperties)
                .withValuesByIndividualIdentifier(valuesByIndividualIdentifier)
                .withIsFunctional(isFunctional)
                .build();
    }

    private DataPropertyDomain getDataPropertyDomain(OWLDataProperty dataProperty, OWLReasoner reasoner) {
        Set<Identifier> allDataPropertyDomains = getAllDataPropertyDomains(dataProperty, reasoner);
        Set<Identifier> directPropertyDomains = getDirectPropertyDomains(dataProperty, reasoner);
        return DataPropertyDomain.of(allDataPropertyDomains, directPropertyDomains);
    }

    private Set<Identifier> getDirectPropertyDomains(OWLDataProperty dataProperty, OWLReasoner reasoner) {
        Set<OWLClass> directDomainClasses = reasoner.getDataPropertyDomains(dataProperty, true).getFlattened();
        return identifierMapper.mapToIdentifiers(directDomainClasses, HashSet::new);
    }

    private Set<Identifier> getAllDataPropertyDomains(OWLDataProperty dataProperty, OWLReasoner reasoner) {
        Set<OWLClass> directDomainClasses = reasoner.getDataPropertyDomains(dataProperty, true).getFlattened();
        Set<OWLClass> subclasses = directDomainClasses.stream()
                .map(reasoner::getSubClasses)
                .flatMap(NodeSet::entities)
                .filter(domain -> !domain.isBottomEntity())
                .collect(Collectors.toSet());
        Set<OWLClass> allDomainClasses = Sets.union(directDomainClasses, subclasses);
        return identifierMapper.mapToIdentifiers(allDomainClasses, HashSet::new);
    }

    private DataPropertyRange getDataPropertyRange(OWLDataProperty dataProperty, OWLDataFactory dataFactory, OWLReasoner reasoner) {
        List<OWLDataRange> dataTypeRanges = reasoner.getRootOntology().getDataPropertyRangeAxioms(dataProperty).stream()
                .map(OWLDataPropertyRangeAxiom::getRange)
                .collect(Collectors.toList());
        if (dataTypeRanges.size() > 1) {
            OWLDataIntersectionOf intersectedRange = dataFactory.getOWLDataIntersectionOf(dataTypeRanges);
            return DataPropertyRange.of(intersectedRange);
        }
        return dataTypeRanges.stream()
                .findAny()
                .map(DataPropertyRange::of)
                .orElse(null);
    }

    private Set<Identifier> getDataPropertyEquivalentProperties(OWLDataProperty dataProperty, OWLReasoner reasoner) {
        Set<OWLDataProperty> equivalentDataProperties = reasoner.getEquivalentDataProperties(dataProperty).getEntities();
        return identifierMapper.mapToIdentifiers(equivalentDataProperties, HashSet::new);
    }

    private Set<Identifier> getDataPropertyDisjointProperties(OWLDataProperty dataProperty, OWLReasoner reasoner) {
        Set<OWLDataProperty> disjointProperties = reasoner.getDisjointDataProperties(dataProperty).getFlattened();
        return identifierMapper.mapToIdentifiers(disjointProperties, HashSet::new);
    }

    private Set<Identifier> getDataPropertySuperProperties(OWLDataProperty dataProperty, OWLReasoner reasoner) {
        Set<OWLDataProperty> superDataProperties = reasoner.getSuperDataProperties(dataProperty).getFlattened();
        return identifierMapper.mapToIdentifiers(superDataProperties, HashSet::new);
    }

    private Set<Identifier> getDataPropertySubProperties(OWLDataProperty dataProperty, OWLReasoner reasoner) {
        Set<OWLDataProperty> subDataProperites = reasoner.getSubDataProperties(dataProperty).getFlattened();
        return identifierMapper.mapToIdentifiers(subDataProperites, HashSet::new);
    }

    private Map<Identifier, Set<OWLLiteral>> getValuesByIndividualIdentifier(OWLDataProperty dataProperty, Collection<OWLNamedIndividual> individuals, OWLReasoner reasoner) {
        return individuals.stream()
                .map(individual -> Map.entry(individual.getIRI(), getDataPropertyValuesForIndividual(individual, dataProperty, reasoner)))
                .filter(valuesByIri -> !valuesByIri.getValue().isEmpty())
                .collect(Collectors.toMap(valuesByIri -> Identifier.of(valuesByIri.getKey()), Map.Entry::getValue));
    }

    private Set<OWLLiteral> getDataPropertyValuesForIndividual(OWLNamedIndividual individual, OWLDataProperty dataProperty, OWLReasoner reasoner) {
        return reasoner.getDataPropertyValues(individual, dataProperty);
    }

    private boolean isFunctionalDataProperty(OWLDataProperty dataProperty, OWLOntology ontology) {
        return EntitySearcher.isFunctional(dataProperty, ontology);
    }

}
