package pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty;

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
public class OWLObjectPropertyService {

    private final IdentifierMapper identifierMapper;

    public List<ObjectProperty> getObjectProperties(OWLReasoner reasoner) {
        Set<OWLObjectProperty> objectProperties = reasoner.getRootOntology().getObjectPropertiesInSignature();
        Set<OWLNamedIndividual> individuals = reasoner.getRootOntology().getIndividualsInSignature();
        return objectProperties.stream()
                .map(objectProperty -> getObjectProperty(objectProperty, individuals, reasoner))
                .collect(Collectors.toList());
    }

    private ObjectProperty getObjectProperty(OWLObjectProperty objectProperty, Collection<OWLNamedIndividual> individuals, OWLReasoner reasoner) {
        Identifier identifier = identifierMapper.mapToIdentifier(objectProperty);
        ObjectPropertyDomain domain = getObjectPropertyDomain(objectProperty, reasoner);
        ObjectPropertyRange range = getObjectPropertyRange(objectProperty, reasoner);
        Set<Identifier> equivalentProperties = getEquivalentObjectProperties(objectProperty, reasoner);
        Set<Identifier> disjointProperties = getDisjointObjectProperties(objectProperty, reasoner);
        Set<Identifier> superProperties = getSuperObjectProperties(objectProperty, reasoner);
        Set<Identifier> subProperties = getSubObjectProperties(objectProperty, reasoner);
        Map<Identifier, Set<Identifier>> valuesByIndividualIdentifier = getValuesByIndividualIdentifier(objectProperty, individuals, reasoner);
        boolean isTransitive = isTransitive(objectProperty, reasoner.getRootOntology());
        boolean isSymmetric = isSymmetric(objectProperty, reasoner.getRootOntology());
        boolean isAssymetric = isAsymmetric(objectProperty, reasoner.getRootOntology());
        boolean isReflexive = isReflexive(objectProperty, reasoner.getRootOntology());
        boolean isIrreflexive = isIrreflexive(objectProperty, reasoner.getRootOntology());
        boolean isInverseFunctional = isInverseFunctional(objectProperty, reasoner.getRootOntology());
        boolean isFunctional = isFunctional(objectProperty, reasoner.getRootOntology());
        Set<Identifier> inverseProperties = getInverseProperties(objectProperty, reasoner.getRootOntology());
        return ObjectProperty.builder()
                .withIdentifier(identifier)
                .withDomain(domain)
                .withRange(range)
                .withEquivalentProperties(equivalentProperties)
                .withDisjointProperties(disjointProperties)
                .withSuperProperties(superProperties)
                .withSubProperties(subProperties)
                .withValuesByIndividualIdentifier(valuesByIndividualIdentifier)
                .withIsTransitive(isTransitive)
                .withIsSymmetric(isSymmetric)
                .withIsAssymetric(isAssymetric)
                .withIsReflexive(isReflexive)
                .withIsIrreflexive(isIrreflexive)
                .withIsInverseFunctional(isInverseFunctional)
                .withIsFunctional(isFunctional)
                .withInverseProperties(inverseProperties)
                .build();
    }

    private ObjectPropertyDomain getObjectPropertyDomain(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<Identifier> allDataPropertyDomains = getAllObjectPropertyDomains(objectProperty, reasoner);
        Set<Identifier> directPropertyDomains = getDirectObjectPropertyDomains(objectProperty, reasoner);
        return ObjectPropertyDomain.of(allDataPropertyDomains, directPropertyDomains);
    }

    private Set<Identifier> getDirectObjectPropertyDomains(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLClass> directDomainClasses = reasoner.getObjectPropertyDomains(objectProperty, true).getFlattened();
        return identifierMapper.mapToIdentifiers(directDomainClasses, HashSet::new);
    }

    private Set<Identifier> getAllObjectPropertyDomains(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLClass> directDomainClasses = reasoner.getObjectPropertyDomains(objectProperty, true).getFlattened();
        Set<OWLClass> indirectDomainClasses = getSubclasses(directDomainClasses, reasoner);
        Set<OWLClass> allDomainClasses = Sets.union(directDomainClasses, indirectDomainClasses);
        return identifierMapper.mapToIdentifiers(allDomainClasses, HashSet::new);
    }

    private Set<OWLClass> getSubclasses(Collection<OWLClass> classes, OWLReasoner reasoner) {
        return classes.stream()
                .map(reasoner::getSubClasses)
                .filter(domain -> !domain.isBottomSingleton())
                .flatMap(NodeSet::entities)
                .collect(Collectors.toSet());
    }

    private ObjectPropertyRange getObjectPropertyRange(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<Identifier> directRangeClasses = getDirectObjectPropertyRanges(objectProperty, reasoner);
        Set<Identifier> allRangeClasses = getAllObjectPropertyRanges(objectProperty, reasoner);
        return ObjectPropertyRange.of(allRangeClasses, directRangeClasses);
    }

    private Set<Identifier> getDirectObjectPropertyRanges(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLClass> directRangeClasses = reasoner.getObjectPropertyRanges(objectProperty, true).getFlattened();
        return identifierMapper.mapToIdentifiers(directRangeClasses, HashSet::new);
    }

    private Set<Identifier> getAllObjectPropertyRanges(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLClass> directRangeClasses = reasoner.getObjectPropertyRanges(objectProperty, true).getFlattened();
        Set<OWLClass> indirectRangeClasses = getSubclasses(directRangeClasses, reasoner);
        Set<OWLClass> allRangeClasses = Sets.union(directRangeClasses, indirectRangeClasses);
        return identifierMapper.mapToIdentifiers(allRangeClasses, HashSet::new);
    }

    private Set<Identifier> getEquivalentObjectProperties(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLObjectPropertyExpression> equivalentObjectPropertyExpressions = reasoner.getEquivalentObjectProperties(objectProperty).getEntities();
        Set<OWLObjectProperty> equivalentObjectProperties = getNamedObjectProperties(equivalentObjectPropertyExpressions);
        return identifierMapper.mapToIdentifiers(equivalentObjectProperties, HashSet::new);
    }

    private Set<OWLObjectProperty> getNamedObjectProperties(Collection<OWLObjectPropertyExpression> objectPropertyExpressions) {
        return objectPropertyExpressions.stream()
                .filter(OWLObjectPropertyExpression::isOWLObjectProperty)
                .map(OWLObjectPropertyExpression::asOWLObjectProperty)
                .collect(Collectors.toSet());
    }

    private Set<Identifier> getDisjointObjectProperties(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLObjectPropertyExpression> disjointPropertyExpressions = reasoner.getDisjointObjectProperties(objectProperty).getFlattened();
        Set<OWLObjectProperty> disjointProperties = getNamedObjectProperties(disjointPropertyExpressions);
        return identifierMapper.mapToIdentifiers(disjointProperties, HashSet::new);
    }

    private Set<Identifier> getSuperObjectProperties(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLObjectPropertyExpression> superDataPropertyExpressions = reasoner.getSuperObjectProperties(objectProperty).getFlattened();
        Set<OWLObjectProperty> superObjectProperties = getNamedObjectProperties(superDataPropertyExpressions);
        return identifierMapper.mapToIdentifiers(superObjectProperties, HashSet::new);
    }

    private Set<Identifier> getSubObjectProperties(OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLObjectPropertyExpression> subDataProperityExpressions = reasoner.getSubObjectProperties(objectProperty).getFlattened();
        Set<OWLObjectProperty> subObjectProperties = getNamedObjectProperties(subDataProperityExpressions);
        return identifierMapper.mapToIdentifiers(subObjectProperties, HashSet::new);
    }

    private Map<Identifier, Set<Identifier>> getValuesByIndividualIdentifier(OWLObjectProperty objectProperty, Collection<OWLNamedIndividual> individuals, OWLReasoner reasoner) {
        return individuals.stream()
                .map(individual -> Map.entry(identifierMapper.mapToIdentifier(individual), getObjectPropertyValuesForIndividual(individual, objectProperty, reasoner)))
                .filter(individualByValues -> !individualByValues.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Set<Identifier> getObjectPropertyValuesForIndividual(OWLNamedIndividual individual, OWLObjectProperty objectProperty, OWLReasoner reasoner) {
        Set<OWLNamedIndividual> objectPropertyValues = reasoner.getObjectPropertyValues(individual, objectProperty).getFlattened();
        return identifierMapper.mapToIdentifiers(objectPropertyValues, HashSet::new);
    }

    private boolean isTransitive(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.isTransitive(objectProperty, ontology);
    }

    private boolean isSymmetric(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.isSymmetric(objectProperty, ontology);
    }

    private boolean isAsymmetric(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.isAsymmetric(objectProperty, ontology);
    }

    private boolean isReflexive(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.isReflexive(objectProperty, ontology);
    }

    private boolean isIrreflexive(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.isIrreflexive(objectProperty, ontology);
    }

    private boolean isInverseFunctional(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.isInverseFunctional(objectProperty, ontology);
    }

    private boolean isFunctional(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.isFunctional(objectProperty, ontology);
    }

    private Set<Identifier> getInverseProperties(OWLObjectProperty objectProperty, OWLOntology ontology) {
        return EntitySearcher.getInverses(objectProperty, ontology)
                .filter(OWLObjectPropertyExpression::isOWLObjectProperty)
                .map(OWLObjectPropertyExpression::asOWLObjectProperty)
                .map(identifierMapper::mapToIdentifier)
                .collect(Collectors.toSet());
    }

}
