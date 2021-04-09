package pl.edu.pwr.ontologydatagenerator.domain.ontology.instance;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.collection.CollectionUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OWLInstanceService {

    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
    private final DataPropertyValueProvider dataPropertyValueProvider;

    public void instantiate(Instance instance, OntologyContainer<OWLOntology> container) {
        List<OWLAxiom> axioms = getAxiomsForInstance(instance);
        manager.addAxioms(container.getOntology(), axioms);
    }

    public List<OWLAxiom> getAxiomsForInstance(Instance instance) {
        return CollectionUtils.listOf(
                getClassAssertion(instance),
                getDataPropertyAssertions(instance),
                getObjectPropertyAssertions(instance));
    }

    private OWLAxiom getClassAssertion(Instance instance) {
        OWLClass concept = getClass(instance.getConcept());
        OWLNamedIndividual individual = getNamedIndividual(instance.getIdentifier());
        return dataFactory.getOWLClassAssertionAxiom(concept, individual);
    }

    private OWLClass getClass(Concept concept) {
        return dataFactory.getOWLClass(concept.getIdentifier().getIri());
    }

    private OWLNamedIndividual getNamedIndividual(Identifier instanceIdentifier) {
        return dataFactory.getOWLNamedIndividual(instanceIdentifier.getIri());
    }

    private List<OWLAxiom> getDataPropertyAssertions(Instance instance) {
        return instance.getDataPropertyInstances().stream()
                .map(dataPropertyInstance -> getDataPropertyAssertion(instance.getIdentifier(), dataPropertyInstance))
                .collect(Collectors.toList());
    }

    private OWLAxiom getDataPropertyAssertion(Identifier instanceIdentifier, DataPropertyInstance dataPropertyInstance) {
        OWLDataProperty property = getDataProperty(dataPropertyInstance.getDataProperty());
        OWLNamedIndividual subject = getNamedIndividual(instanceIdentifier);
        OWLLiteral literal = getOWLLiteral(dataPropertyInstance);
        return dataFactory.getOWLDataPropertyAssertionAxiom(property, subject, literal);
    }

    private OWLDataProperty getDataProperty(DataProperty dataProperty) {
        return dataFactory.getOWLDataProperty(dataProperty.getIdentifier().getIri());
    }

    private OWLLiteral getOWLLiteral(DataPropertyInstance dataPropertyInstance) {
        DataPropertyValue dataPropertyValue = dataPropertyValueProvider.getDataPropertyValue(dataPropertyInstance);
        return dataFactory.getOWLLiteral(dataPropertyValue.getLexicalValue(), dataPropertyInstance.getValue());
    }

    private List<OWLAxiom> getObjectPropertyAssertions(Instance instance) {
        return instance.getObjectPropertyInstances().stream()
                .map(objectPropertyInstance -> getObjectPropertyAssertion(instance.getIdentifier(), objectPropertyInstance))
                .collect(Collectors.toList());
    }

    private OWLAxiom getObjectPropertyAssertion(Identifier instanceIdentifier, ObjectPropertyInstance objectPropertyInstance) {
        OWLObjectProperty property = getObjectProperty(objectPropertyInstance.getObjectProperty());
        OWLIndividual subject = getNamedIndividual(instanceIdentifier);
        OWLIndividual object = getNamedIndividual(objectPropertyInstance.getValue());
        return dataFactory.getOWLObjectPropertyAssertionAxiom(property, subject, object);
    }

    private OWLObjectProperty getObjectProperty(ObjectProperty objectProperty) {
        return dataFactory.getOWLObjectProperty(objectProperty.getIdentifier().getIri());
    }

}
