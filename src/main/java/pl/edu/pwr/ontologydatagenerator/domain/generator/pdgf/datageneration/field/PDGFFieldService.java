package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.field;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Field;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.PDGFGeneratorService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.id.IdentifierGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.collection.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDGFFieldService {

    private final FieldTypeProvider fieldTypeProvider;
    private final PDGFGeneratorService generatorService;
    @Value("${app.generator.pdgf.datageneration.identifier-field-name}") private final String identifierFieldName;

    public List<Field> getFields(Concept concept, Collection<Concept> conceptsToInstatniate, OntologyContainer<OWLOntology> container) {
        //return getTestFileds(concept);
        return CollectionUtils.listOf(
                getIdentifierField(concept),
                getDataPropertyFields(concept, container),
                getObjectPropertyFields(concept, conceptsToInstatniate, container));
    }

    private Field getIdentifierField(Concept concept) {
        return new Field()
                .withName(identifierFieldName)
                .withType(FieldTypeProvider.FieldType.VARCHAR.name())
                .withGenerator(new IdentifierGenerator(concept));
    }

    private List<Field> getDataPropertyFields(Concept concept, OntologyContainer<OWLOntology> container) {
        return getDataPropertiesToInstatiate(concept, container).stream()
                .map(dataProperty -> getField(dataProperty, concept, container))
                .collect(Collectors.toList());
    }

    private List<DataProperty> getDataPropertiesToInstatiate(Concept concept, OntologyContainer<OWLOntology> container) {
        return concept.getDataProperties().values().stream()
                .filter(CollectionUtils.distinctBy(DataProperty::getEquivalentProperties))
                .filter(dataProperty -> shouldPropertyBeInstantiated(dataProperty, concept, container))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("SameReturnValue")
    private boolean shouldPropertyBeInstantiated(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> ontologyContainer) {
        return true; //TODO: machanism for selective property instatntiation
    }

    private Field getField(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        return new Field()
                .withName(dataProperty.getName())
                .withType(getFieldType(dataProperty))
                .withGenerator(getGenerator(dataProperty, concept, container));
    }

    private String getFieldType(DataProperty dataProperty) {
        return fieldTypeProvider.getFieldType(dataProperty).name();
    }

    private Generator getGenerator(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        return generatorService.getGenerator(dataProperty, concept, container);
    }

    private List<Field> getObjectPropertyFields(Concept concept, Collection<Concept> conceptsToInstatniate, OntologyContainer<OWLOntology> container) {
        return getObjectPropertiesToInstatiate(concept, container).stream()
                .map(objectProperty -> getField(objectProperty, concept, conceptsToInstatniate, container))
                .collect(Collectors.toList());
    }

    private List<ObjectProperty> getObjectPropertiesToInstatiate(Concept concept, OntologyContainer<OWLOntology> container) {
        return concept.getObjectProperties().values().stream()
                .filter(CollectionUtils.distinctBy(ObjectProperty::getEquivalentProperties))
                .filter(objectProperty -> shouldPropertyBeInstantiated(objectProperty, concept, container))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("SameReturnValue")
    private boolean shouldPropertyBeInstantiated(ObjectProperty objectProperty, Concept concept, OntologyContainer<OWLOntology> ontologyContainer) {
        return true; //TODO: machanism for selective property instatntiation
    }

    private Field getField(ObjectProperty objectProperty, Concept concept, Collection<Concept> conceptsToInstatniate, OntologyContainer<OWLOntology> container) {
        return new Field()
                .withName(objectProperty.getName())
                .withType(getFieldType(objectProperty))
                .withGenerator(generatorService.getGenerator(objectProperty, concept, conceptsToInstatniate, container));
    }

    private String getFieldType(ObjectProperty objectProperty) {
        return fieldTypeProvider.getFieldType(objectProperty).name();
    }

}
