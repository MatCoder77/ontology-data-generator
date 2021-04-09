package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.instance.DataPropertyInstance;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.instance.Instance;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.instance.ObjectPropertyInstance;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.transform.TransformUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstanceMapper {

    public Instance mapToInstance(String instanceName, Concept concept, Map<String, List<String>> valuesByPropertyName, OntologyContainer<OWLOntology> container) {
        Identifier identifier = getIdentifier(instanceName, container.getOntologyIdentifier());
        Map<Identifier, List<String>> valuesByPropertyIdentifier = getValuesByPropertyIdentifier(valuesByPropertyName, container.getOntologyIdentifier());
        List<DataPropertyInstance> dataPropertyInstances = getDataPropertyInstances(valuesByPropertyIdentifier, container);
        List<ObjectPropertyInstance> objectPropertyInstances = getObjectPropertyInstances(valuesByPropertyIdentifier, container);
        return new Instance(identifier, concept, dataPropertyInstances, objectPropertyInstances);
    }

    private Identifier getIdentifier(String name, Identifier ontologyIdentifier) {
        IRI ontologyIri = ontologyIdentifier.getIri();
        IRI iri = IRI.create(ontologyIri.getIRIString() + "#" + name);
        return Identifier.of(iri);
    }

    private Map<Identifier, List<String>> getValuesByPropertyIdentifier(Map<String, List<String>> valuesByPropertyName, Identifier ontologyIdentifier) {
        return TransformUtils.transformMap(valuesByPropertyName, propertyName -> getIdentifier(propertyName, ontologyIdentifier), Function.identity());
    }

    private List<DataPropertyInstance> getDataPropertyInstances(Map<Identifier, List<String>> valuesByPropertyIdentifier, OntologyContainer<OWLOntology> container) {
        Map<DataProperty, List<String>> valuesByDataProperty = getValuesByDataProperty(valuesByPropertyIdentifier, container);
        return getDataPropertyInstances(valuesByDataProperty);
    }

    private Map<DataProperty, List<String>> getValuesByDataProperty(Map<Identifier, List<String>> valuesByPropertyIdentifier, OntologyContainer<OWLOntology> container) {
        Map<Identifier, DataProperty> dataPropertiesByIdentifier = container.getDataProperties();
        return TransformUtils.transformMap(
                valuesByPropertyIdentifier,
                (identifier, values) -> dataPropertiesByIdentifier.containsKey(identifier),
                dataPropertiesByIdentifier::get, Function.identity());
    }

    private List<DataPropertyInstance> getDataPropertyInstances(Map<DataProperty, List<String>> valuesByDataProperty) {
        return valuesByDataProperty.entrySet().stream()
                .map(valuesByProperty -> getDataPropertyInstances(valuesByProperty.getKey(), valuesByProperty.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<DataPropertyInstance> getDataPropertyInstances(DataProperty dataProperty, List<String> values) {
        return values.stream()
                .map(value -> new DataPropertyInstance(dataProperty, value))
                .collect(Collectors.toList());
    }

    private List<ObjectPropertyInstance> getObjectPropertyInstances(Map<Identifier, List<String>> valuesByPropertyIdentifier, OntologyContainer<OWLOntology> container) {
        Map<ObjectProperty, List<Identifier>> valuesByObjectProperty = getValuesByObjectProperty(valuesByPropertyIdentifier, container);
        return getObjectPropertyInstances(valuesByObjectProperty);
    }

    private Map<ObjectProperty, List<Identifier>> getValuesByObjectProperty(Map<Identifier, List<String>> valuesByPropertyIdentifier, OntologyContainer<OWLOntology> container) {
        Map<Identifier, ObjectProperty> objectPropertiesByIdentifier = container.getObjectProperties();
        return TransformUtils.transformMap(
                valuesByPropertyIdentifier,
                (identifier, values) -> objectPropertiesByIdentifier.containsKey(identifier),
                objectPropertiesByIdentifier::get, names -> getIdentifiers(names, container));
    }

    private List<Identifier> getIdentifiers(Collection<String> names, OntologyContainer<OWLOntology> container) {
        return names.stream()
                .map(name -> getIdentifier(name, container.getOntologyIdentifier()))
                .collect(Collectors.toList());
    }

    private List<ObjectPropertyInstance> getObjectPropertyInstances(Map<ObjectProperty, List<Identifier>> valuesByObjectProperty) {
        return valuesByObjectProperty.entrySet().stream()
                .map(valuesByProperty -> getObjectPropertyInstances(valuesByProperty.getKey(), valuesByProperty.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ObjectPropertyInstance> getObjectPropertyInstances(ObjectProperty objectProperty, List<Identifier> values) {
        return values.stream()
                .map(value -> new ObjectPropertyInstance(objectProperty, value))
                .collect(Collectors.toList());
    }

}
