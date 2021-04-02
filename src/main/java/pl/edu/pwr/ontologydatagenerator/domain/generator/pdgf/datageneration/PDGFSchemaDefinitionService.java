package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.SchemaDefinitonService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.table.PDGFTableService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PDGFSchemaDefinitionService implements SchemaDefinitonService<PDGFSchemaDefinition, OWLOntology> {

    private final StorageService storageService;
    @Qualifier("PDGFDatageneration") private final Marshaller xmlMarshaller;
    @Value("${app.generator.pdgf.datageneration.name}") private final String name;
    @Value("${app.generator.pdgf.datageneration.seed}") private final String seed;
    @Value("${app.generator.pdgf.datageneration.rng}") private final String randomNumberGenerator;
    @Value("${app.generator.pdgf.datageneration.scalefactor}") private final String scaleFactor;
    private final PDGFTableService tableService;

    @Override
    public PDGFSchemaDefinition buildSchemaDefinition(OntologyContainer<OWLOntology> ontologyContainer) {
        return new PDGFSchemaDefinition()
                .withName(name)
                .withSeed(seed)
                .withProperty(getScaleFactorProperty())
                .withRng(getRng(randomNumberGenerator))
                .withTable(tableService.getTables(ontologyContainer));
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

    @Override
    public void saveSchemaDefinition(PDGFSchemaDefinition schemaDefinition, URI url) {
        storageService.saveResource(getDataSchemaDefinitionSaver(schemaDefinition), url);
    }

    private Consumer<OutputStream> getDataSchemaDefinitionSaver(PDGFSchemaDefinition schemaDefinition) {
        return ThrowingConsumer.wrapper(outputStream -> xmlMarshaller.marshal(schemaDefinition, outputStream));
    }

}
