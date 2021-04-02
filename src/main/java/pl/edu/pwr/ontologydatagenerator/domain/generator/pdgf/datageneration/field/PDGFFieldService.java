package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.field;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Field;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.binary.Base64Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.binary.HexadecimalGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.id.IdentifierGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.logical.BooleanGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric.DoubleNumberGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric.LongNumberGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.time.DateTimeGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.collection.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDGFFieldService {

    private final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    private final OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
    private final FieldTypeProvider fieldTypeProvider;

    public List<Field> getFields(Concept concept, OntologyContainer<OWLOntology> container) {
        //return getTestFileds(concept);
        return CollectionUtils.listOf(
                getIdentifierField(concept),
                getDataPropertyFields(concept, container),
                getObjectPropertyFields(concept, container));
    }

    private Field getIdentifierField(Concept concept) {
        return new Field()
                .withName("__identifier__")
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
                .withType(getFieldType(dataProperty));
                //.withSize()
                //.withGenerator();
    }

    private String getFieldType(DataProperty dataProperty) {
        return fieldTypeProvider.getFieldType(dataProperty).name();
    }

    private List<Field> getTestFileds(Concept concept) {
        return List.of(getIdentifierField(concept), getBooleanField(), getDataField(),
                getLongField(), getDoubleField(), getHexaDecField(), getBase64DecField());
    }

    private Field getBooleanField() {
        return new Field()
                .withName("__boolean_test___")
                .withType(FieldTypeProvider.FieldType.VARCHAR.name())
                .withGenerator(new BooleanGenerator());
    }

    private Field getDataField() {
        return new Field()
                .withName("__data_test___")
                .withType(FieldTypeProvider.FieldType.DATE.name())
                .withGenerator(new DateTimeGenerator(LocalDateTime.now().minus(10, ChronoUnit.YEARS), LocalDateTime.now(), "yyyy-MM-dd"));
    }

    private Field getLongField() {
        DataProperty data = DataProperty.builder()
                .withIdentifier(Identifier.of(IRI.create("http://benchmark/OWL2Bench#__long__test__")))
                .build();
        return new Field()
                .withName("__long__test__")
                .withType(FieldTypeProvider.FieldType.NUMERIC.name())
                .withGenerator(new LongNumberGenerator(-100L, 100L, null/*distributionProvider.getDistribution(Identifier.of(IRI.create("http://benchmark/OWL2Bench#__TEST__")), data)*/));
    }

    private Field getDoubleField() {
        return new Field()
                .withName("__double__test___")
                .withType(FieldTypeProvider.FieldType.DOUBLE.name())
                .withGenerator(new DoubleNumberGenerator(-10, 10, 3, null));
    }

    private Field getHexaDecField() {
        return new Field()
                .withName("__test-hexadec__")
                .withType(FieldTypeProvider.FieldType.VARCHAR.name())
                .withGenerator(new HexadecimalGenerator(1, 10, null));
    }

    private Field getBase64DecField() {
        return new Field()
                .withName("__test-base64__")
                .withType(FieldTypeProvider.FieldType.VARCHAR.name())
                .withGenerator(new Base64Generator(1, 10, null));
    }

    private List<Field> getObjectPropertyFields(Concept concept, OntologyContainer<OWLOntology> container) {
        return getObjectPropertiesToInstatiate(concept, container).stream()
                .map(objectProperty -> getField(objectProperty, concept, container))
                .collect(Collectors.toList());
    }

    private List<DataProperty> getObjectPropertiesToInstatiate(Concept concept, OntologyContainer<OWLOntology> container) {
        return concept.getDataProperties().values().stream()
                .filter(dataProperty -> shouldPropertyBeInstantiated(dataProperty, concept, container))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("SameReturnValue")
    private boolean shouldPropertyBeInstantiated(ObjectProperty objectProperty, Concept concept, OntologyContainer<OWLOntology> ontologyContainer) {
        return true; //TODO: machanism for selective property instatntiation
    }

    private Field getField(ObjectProperty objectProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        return null;
    }



}
