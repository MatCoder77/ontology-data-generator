package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Schema;
import pl.edu.pwr.ontologydatagenerator.domain.generator.SchemaDefinitonService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.table.PDGFTableService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PDGFSchemaDefinitionService implements SchemaDefinitonService<PDGFSchemaDefinition, OWLOntology> {

    public static final String SCHEMA_NAME_SUFFIX = "-schema";

    private final StorageService storageService;
    private final UrlProvider urlProvider;
    @Qualifier("PDGFDatageneration") private final Marshaller xmlMarshaller;
    @Value("${app.generator.pdgf.datageneration.seed}") private final String seed;
    @Value("${app.generator.pdgf.datageneration.rng}") private final String randomNumberGenerator;
    @Value("${app.generator.pdgf.datageneration.scalefactor}") private final String scaleFactor;
    @Value("${app.datastore.schema}") private final String schemaDirectory;
    private final PDGFTableService tableService;

    @Override
    public Schema<PDGFSchemaDefinition> createSchemaDefinition(OntologyContainer<OWLOntology> ontologyContainer) {
        PDGFSchemaDefinition schemaDefinition = buildSchemaDefinition(ontologyContainer);
        URI schmaDefinitionUrl = saveSchemaDefinition(schemaDefinition);
        return new Schema<>(schmaDefinitionUrl, schemaDefinition);
    }

    public PDGFSchemaDefinition buildSchemaDefinition(OntologyContainer<OWLOntology> ontologyContainer) {
        return new PDGFSchemaDefinition()
                .withName(getSchemaName(ontologyContainer))
                .withSeed(seed)
                .withProperty(getScaleFactorProperty())
                .withRng(getRng(randomNumberGenerator))
                .withTable(tableService.getTables(ontologyContainer));
    }

    private String getSchemaName(OntologyContainer<OWLOntology> ontologyContainer) {
        return ontologyContainer.getOntologyName() + SCHEMA_NAME_SUFFIX;
    }

    private Property getScaleFactorProperty() {
        return new Property()
                .withName("SF")
                .withType("double")
                .withContent(scaleFactor);
    }

    private Rng getRng(String randomNumberGenerator) {
        return new Rng()
                .withName(randomNumberGenerator);
    }

    public URI saveSchemaDefinition(PDGFSchemaDefinition schemaDefinition) {
        URI url = getSchemaDefinitionUrl(schemaDefinition);
        storageService.saveResource(getDataSchemaDefinitionSaver(schemaDefinition), url);
        return url;
    }

    private URI getSchemaDefinitionUrl(PDGFSchemaDefinition schemaDefinition) {
        return urlProvider.getUrlForResource(schemaDirectory, schemaDefinition.getName() + ".xml");
    }

    private Consumer<OutputStream> getDataSchemaDefinitionSaver(PDGFSchemaDefinition schemaDefinition) {
        return ThrowingConsumer.wrapper(outputStream -> xmlMarshaller.marshal(schemaDefinition, outputStream));
    }

}
