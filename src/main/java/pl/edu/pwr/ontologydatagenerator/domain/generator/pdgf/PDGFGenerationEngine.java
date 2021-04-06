package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngineConfigurationService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Schema;
import pl.edu.pwr.ontologydatagenerator.domain.generator.SchemaDefinitonService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngine;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.configuration.PDGFConfiguration;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.PDGFSchemaDefinition;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDGFGenerationEngine implements GenerationEngine<OntologyContainer<OWLOntology>, PDGFDataGenerationResult> {

    private final GenerationEngineConfigurationService<PDGFConfiguration> configurationService;
    private final SchemaDefinitonService<PDGFSchemaDefinition, OWLOntology> schemaDefinitonService;
    @Value("${app.generator.pdgf.home.url}") URI pdgfHomeDirecoryUrl;

    @Override
    public PDGFDataGenerationResult generateData(OntologyContainer<OWLOntology> ontologyContainer) {
        URI defaultConfigurationUrl = configurationService.getDefaultConfiguration();
        Schema<PDGFSchemaDefinition> schemaDefinition = schemaDefinitonService.createSchemaDefinition(ontologyContainer);
        int exitCode = executeGenerationProcess(defaultConfigurationUrl, schemaDefinition.getUrl());
        return null;
    }

    private int executeGenerationProcess(URI defaultConfigurationUrl, URI schemaDefinitionUrl) {
        try {
            Process process = runGenerationProcess(defaultConfigurationUrl, schemaDefinitionUrl);
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            log.error("Error occured during generation process: ", e);
            throw new IllegalStateAppException(e);
        }
    }

    private Process runGenerationProcess(URI defaultConfigurationUrl, URI schemaDefinitionUrl) throws IOException {
        return new ProcessBuilder()
                .directory(new File(pdgfHomeDirecoryUrl.getPath()))
                .command("java", "-jar", "pdgf.jar", "-l", schemaDefinitionUrl.getPath(), "-l", defaultConfigurationUrl.getPath(), "-c", "-ns", "-s")
                .inheritIO()
                .start();
    }

}
