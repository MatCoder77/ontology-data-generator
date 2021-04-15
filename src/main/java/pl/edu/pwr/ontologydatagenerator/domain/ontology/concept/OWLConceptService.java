package pl.edu.pwr.ontologydatagenerator.domain.ontology.concept;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.IdentifierMapper;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OWLConceptService {

    private final IdentifierMapper identifierMapper;

    public List<Concept> parseConcepts(Collection<DataProperty> dataProperties, Collection<ObjectProperty> objectProperties, OWLReasoner reasoner) {
        Set<OWLClass> concepts = reasoner.getRootOntology().getClassesInSignature();
        return concepts.stream()
                .map(concept -> getConcept(concept, dataProperties, objectProperties, reasoner))
                .collect(Collectors.toList());
    }

    private Concept getConcept(OWLClass concept, Collection<DataProperty> allDataProperties, Collection<ObjectProperty> allObjectProperties, OWLReasoner reasoner) {
        Identifier identifier = identifierMapper.mapToIdentifier(concept);
        Map<Identifier, DataProperty> dataProperties = getDataProperties(identifier, allDataProperties);
        Map<Identifier, ObjectProperty> objectProperties = getObjectProperties(identifier, allObjectProperties);
        Set<Identifier> equivalentConcepts = getEquivalentConcepts(concept, reasoner);
        Set<Identifier> disjointConcepts = getDisjointConcepts(concept, reasoner);
        Set<Identifier> superConcepts = getSuperConcepts(concept, reasoner);
        Set<Identifier> subConcepts = getSubConcepts(concept, reasoner);
        Set<Identifier> instances = getInstances(concept, reasoner);
        return Concept.builder()
                .withIdentifier(identifier)
                .withDataProperties(dataProperties)
                .withObjectProperties(objectProperties)
                .withEquivalentConcepts(equivalentConcepts)
                .withDisjointConcepts(disjointConcepts)
                .withSuperConcepts(superConcepts)
                .withSubConcepts(subConcepts)
                .withInstances(instances)
                .build();
    }

    private Map<Identifier, DataProperty> getDataProperties(Identifier concept, Collection<DataProperty> allDataProperties) {
        return allDataProperties.stream()
                .filter(dataProperty -> hasConceptInDomain(concept, dataProperty))
                .collect(Collectors.toMap(DataProperty::getIdentifier, Function.identity()));
    }

    private boolean hasConceptInDomain(Identifier concept, DataProperty dataProperty) {
        return dataProperty.getDomain().getAllConceptsInDomain().contains(concept);
    }

    private Map<Identifier, ObjectProperty> getObjectProperties(Identifier concept, Collection<ObjectProperty> allObjectProperties) {
        return allObjectProperties.stream()
                .filter(objectProperty -> hasConceptInDomain(concept, objectProperty))
                .collect(Collectors.toMap(ObjectProperty::getIdentifier, Function.identity()));
    }

    private boolean hasConceptInDomain(Identifier concept, ObjectProperty objectProperty) {
        return objectProperty.getDomain().getAllConceptsInDomain().contains(concept);
    }

    private Set<Identifier> getEquivalentConcepts(OWLClass concept, OWLReasoner reasoner) {
        Set<OWLClass> equivalentConcepts = reasoner.getEquivalentClasses(concept).getEntities();
        return identifierMapper.mapToIdentifiers(equivalentConcepts, LinkedHashSet::new);
    }

    private Set<Identifier> getDisjointConcepts(OWLClass concept, OWLReasoner reasoner) {
        Set<OWLClass> disjointConcepts = reasoner.getDisjointClasses(concept).getFlattened();
        return identifierMapper.mapToIdentifiers(disjointConcepts, HashSet::new);
    }

    private Set<Identifier> getSuperConcepts(OWLClass concept, OWLReasoner reasoner) {
        return reasoner.getSuperClasses(concept).getFlattened().stream()
                .map(identifierMapper::mapToIdentifier)
                .collect(Collectors.toSet());
    }

    private Set<Identifier> getSubConcepts(OWLClass concept, OWLReasoner reasoner) {
        return reasoner.getSubClasses(concept).getFlattened().stream()
                .map(identifierMapper::mapToIdentifier)
                .collect(Collectors.toSet());
    }

    private Set<Identifier> getInstances(OWLClass concept, OWLReasoner reasoner) {
        Set<OWLNamedIndividual> instances = reasoner.getInstances(concept).getFlattened();
        return identifierMapper.mapToIdentifiers(instances, HashSet::new);
    }

}
