package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf;

import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.configuration.PDGFConfiguration;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Field;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.PDGFSchemaDefinition;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Table;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.instance.Instance;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;
import pl.edu.pwr.ontologydatagenerator.infrastructure.transform.TransformUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PDGFGenerationEngine implements GenerationEngine<OntologyContainer<OWLOntology>, Instance> {

    private final GenerationEngineConfigurationService<PDGFConfiguration, PDGFSchemaDefinition> configurationService;
    private final SchemaDefinitonService<PDGFSchemaDefinition, OWLOntology> schemaDefinitonService;
    private final StorageService storageService;
    private final InstanceMapper instanceMapper;
    @Value("${app.generator.pdgf.home.url}") private final URI pdgfHomeDirecoryUrl;
    @Value("${app.generator.pdgf.datageneration.identifier-field-name}") private final String identifierFieldName;

    @Override
    public Stream<Instance> generateData(OntologyContainer<OWLOntology> ontologyContainer) {
        Schema<PDGFSchemaDefinition> schemaDefinition = schemaDefinitonService.createSchemaDefinition(ontologyContainer);
        GenerationEngineConfiguraiton<PDGFConfiguration> configuration = configurationService.createGenerationEngineConfiguration(schemaDefinition.getDefiniton());
        int exitCode = executeGenerationProcess(schemaDefinition.getUrl(), configuration.getUrl());
        validateProcessExitCode(exitCode);
        return parseGenerationResults(ontologyContainer, schemaDefinition.getDefiniton(), configuration.getConfiguration());
    }

    private int executeGenerationProcess(URI schemaDefinitionUrl, URI defaultConfigurationUrl) {
        try {
            Process process = runGenerationProcess(defaultConfigurationUrl, schemaDefinitionUrl);
            return process.waitFor();
        } catch (IOException e) {
            log.error("Error occured during generation process: ", e);
            throw new IllegalStateAppException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

    private void validateProcessExitCode(int exitCode) {
        if (exitCode != 0) {
            throw new IllegalStateAppException("Generation process ended with error. Exit code: " + exitCode);
        }
    }

    private Stream<Instance> parseGenerationResults(OntologyContainer<OWLOntology> container, PDGFSchemaDefinition schemaDefinition, PDGFConfiguration configuration) {
        Map<Concept, Resource> conceptsByResource = getConceptsByResource(container, schemaDefinition, configuration);
        Map<String, List<String>> fieldNamesByTableName = getFieldNamesByTableName(schemaDefinition);
        return conceptsByResource.entrySet().stream()
                .map(resourceByConcept -> parseFileForConcept(resourceByConcept.getKey(), resourceByConcept.getValue(), fieldNamesByTableName, configuration, container))
                .flatMap(Collection::stream);
    }

    private Map<Concept, Resource> getConceptsByResource(OntologyContainer<OWLOntology> container, PDGFSchemaDefinition schemaDefinition, PDGFConfiguration configuration) {
        Map<URI, String> conceptNamesByUrl = getConceptNameByUrlToFileWithGeneratedData(schemaDefinition, configuration);
        Map<URI, Resource> resourcesByUrl = storageService.getResources(conceptNamesByUrl.keySet());
        Map<String, Resource> conceptNamesByResource = TransformUtils.transformMap(resourcesByUrl, conceptNamesByUrl::get, Function.identity());
        Map<Identifier, Concept> conceptsByIdentifier = container.getConcepts();
        Map<String, Concept> conceptsByNames = TransformUtils.transformMap(conceptsByIdentifier, Identifier::getName, Function.identity());
        return TransformUtils.transformMap(conceptNamesByResource, conceptsByNames::get, Function.identity());
    }

    private Map<URI, String> getConceptNameByUrlToFileWithGeneratedData(PDGFSchemaDefinition schemaDefinition, PDGFConfiguration configuration) {
        return getTableNames(schemaDefinition).stream()
                .collect(Collectors.toMap(conceptName -> getUrlForFileWithConceptData(conceptName, configuration.getOutput().getOutputDir()), Function.identity()));
    }

    private List<String> getTableNames(PDGFSchemaDefinition schemaDefinition) {
        return schemaDefinition.getTable().stream()
                .map(Table::getName)
                .collect(Collectors.toList());
    }

    private URI getUrlForFileWithConceptData(String conceptName, String outputDirectoryPath) {
        return URI.create("file:" + outputDirectoryPath + conceptName + ".csv");
    }

    private Map<String, List<String>> getFieldNamesByTableName(PDGFSchemaDefinition schemaDefinition) {
        return schemaDefinition.getTable().stream()
                .collect(Collectors.toMap(Table::getName, this::getFieldNames));
    }

    private List<String> getFieldNames(Table table) {
        return table.getField().stream()
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    private List<Instance> parseFileForConcept(Concept concept, Resource resource, Map<String, List<String>> fieldNamesByTableName,
                                                 PDGFConfiguration configuration, OntologyContainer<OWLOntology> container) {
        try (CSVParser parser = getParser(resource, getParserFormat(concept, fieldNamesByTableName, configuration))){
            return Streams.stream(parser)
                    .map(record -> parseRecord(record, concept, container))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error occured when parsing data for concept {}", concept.getName(), e);
            throw new IllegalStateAppException(e);
        }
    }

    private CSVFormat getParserFormat(Concept concept, Map<String, List<String>> fieldNamesByTableName, PDGFConfiguration configuration) {
        return CSVFormat.DEFAULT
                .withDelimiter(configuration.getOutput().getDelimiter().charAt(0))
                .withHeader(fieldNamesByTableName.get(concept.getName()).toArray(new String[0]));
    }

    private CSVParser getParser(Resource resource, CSVFormat format) throws IOException {
        return CSVParser.parse(resource.getInputStream(), Charset.defaultCharset(), format);
    }

    private Instance parseRecord(CSVRecord record, Concept concept, OntologyContainer<OWLOntology> ontologyContainer) {
        Map<String, String> valuesByHeaderName = record.toMap();
        String instanceIdentifier = getInstanceIdentifier(valuesByHeaderName);
        Map<String, List<String>> valuesByPropertyName = getPropertyValuations(valuesByHeaderName);
        return instanceMapper.mapToInstance(instanceIdentifier, concept, valuesByPropertyName, ontologyContainer);
    }

    private String getInstanceIdentifier(Map<String, String> valuesByHeaderName) {
        return valuesByHeaderName.get(identifierFieldName);
    }

    private Map<String, List<String>> getPropertyValuations(Map<String, String> valuesByHeaderName) {
        return valuesByHeaderName.entrySet().stream()
                .filter(valueByHeader -> !valueByHeader.getKey().equals(identifierFieldName))
                .collect(Collectors.toMap(Map.Entry::getKey, valueByHeader -> getValuesList(valueByHeader.getValue())));
    }

    private List<String> getValuesList(String string) {
        return Arrays.asList(string.split("\\|"));
    }

}
