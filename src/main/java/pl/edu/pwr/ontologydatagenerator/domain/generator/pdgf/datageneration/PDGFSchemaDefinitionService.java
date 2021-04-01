package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.SchemaDefinitonService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.field.FieldType;
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
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDGFSchemaDefinitionService implements SchemaDefinitonService<PDGFSchemaDefinition, OWLOntology> {

    private final StorageService storageService;
    @Qualifier("PDGFDatageneration") private final Marshaller xmlMarshaller;
    @Value("${app.generator.pdgf.datageneration.name}") private final String name;
    @Value("${app.generator.pdgf.datageneration.seed}") private final String seed;
    @Value("${app.generator.pdgf.datageneration.rng}") private final String randomNumberGenerator;
    @Value("${app.generator.pdgf.datageneration.scalefactor}") private final String scaleFactor;
    private final DistributionProvider<Distribution> distributionProvider;

    @Override
    public PDGFSchemaDefinition buildSchemaDefinition(OntologyContainer<OWLOntology> ontologyContainer) {
        return new PDGFSchemaDefinition()
                .withName(name)
                .withSeed(seed)
                .withProperty(getScaleFactorProperty())
                .withRng(getRng(randomNumberGenerator))
                .withTable(getTables(ontologyContainer));
    }

    private Property getScaleFactorProperty() {
        return new Property()
                .withName("SF")
                .withType("double")
                .withContent(scaleFactor);
    }

    private Rng getRng(String randomNumberGenerator) {
        return new Rng().withName(randomNumberGenerator);
    }

    private List<Table> getTables(OntologyContainer<OWLOntology> ontologyContainer) {
        return getConceptsToInstantiate(ontologyContainer).stream()
                .map(concept -> getTable(concept, ontologyContainer))
                .collect(Collectors.toList());
    }

    private List<Concept> getConceptsToInstantiate(OntologyContainer<OWLOntology> ontologyContainer) {
        return ontologyContainer.getConcepts().values().stream()
                .filter(concept -> shouldBeInstantiated(concept, ontologyContainer))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("SameReturnValue")
    private boolean shouldBeInstantiated(Concept concept, OntologyContainer<OWLOntology> ontologyContainer) {
        return true; //TODO: implement instatntion only for non-abstract concepts
    }

    private Table getTable(Concept concept, OntologyContainer<OWLOntology> container) {
        return new Table()
                .withName(concept.getName())
                .withSize(10)
                .withField(getIdentifierField(concept), getBooleanField(), getDataField(),
                        getLongField(), getDoubleField(), getHexaDecField(), getBase64DecField());
    }

    private Field getIdentifierField(Concept concept) {
        return new Field()
                .withName("__identifier__")
                .withType(FieldType.VARCHAR.name())
                .withGenerator(IdentifierGenerator.of(concept));
    }

    private Field getBooleanField() {
        return new Field()
                .withName("__boolean_test___")
                .withType(FieldType.VARCHAR.name())
                .withGenerator(new BooleanGenerator());
    }

    private Field getDataField() {
        return new Field()
                .withName("__data_test___")
                .withType(FieldType.DATE.name())
                .withGenerator(new DateTimeGenerator(LocalDateTime.now().minus(10, ChronoUnit.YEARS), LocalDateTime.now(), "yyyy-MM-dd"));
    }

    private Field getLongField() {
        DataProperty data = DataProperty.builder()
                .withIdentifier(Identifier.of(IRI.create("http://benchmark/OWL2Bench#__long__test__")))
                .build();
        return new Field()
                .withName("__long__test__")
                .withType(FieldType.NUMERIC.name())
                .withGenerator(new LongNumberGenerator(-100L, 100L, distributionProvider.getDistribution(Identifier.of(IRI.create("http://benchmark/OWL2Bench#__TEST__")), data)));
    }

    private Field getDoubleField() {
        return new Field()
                .withName("__double__test___")
                .withType(FieldType.DOUBLE.name())
                .withGenerator(new DoubleNumberGenerator(-10, 10, 3, null));
    }

    private Field getHexaDecField() {
        return new Field()
                .withName("__test-hexadec__")
                .withType(FieldType.VARCHAR.name())
                .withGenerator(new HexadecimalGenerator(1, 10, null));
    }

    private Field getBase64DecField() {
        return new Field()
                .withName("__test-base64__")
                .withType(FieldType.VARCHAR.name())
                .withGenerator(new Base64Generator(1, 10, null));
    }

    @Override
    public void saveSchemaDefinition(PDGFSchemaDefinition schemaDefinition, URI url) {
        storageService.saveResource(getDataSchemaDefinitionSaver(schemaDefinition), url);
    }

    private Consumer<OutputStream> getDataSchemaDefinitionSaver(PDGFSchemaDefinition schemaDefinition) {
        return ThrowingConsumer.wrapper(outputStream -> xmlMarshaller.marshal(schemaDefinition, outputStream));
    }

}
