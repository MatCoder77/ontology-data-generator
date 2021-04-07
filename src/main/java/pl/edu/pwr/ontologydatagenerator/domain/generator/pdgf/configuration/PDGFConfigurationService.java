package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngineConfiguraiton;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngineConfigurationService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.PDGFSchemaDefinition;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.PDGFSchemaDefinitionService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@Service
@RequiredArgsConstructor
public class PDGFConfigurationService implements GenerationEngineConfigurationService<PDGFConfiguration, PDGFSchemaDefinition> {

    public static final String DEFAULT_SCHEDULER = "DefaultScheduler";
    public static final String CSV_ROW_OUTPUT = "CSVRowOutput";
    private static final String CONFIGURATION_NAME_SUFFIX = "-configuration";

    private final StorageService storageService;
    private final UrlProvider urlProvider;
    @Qualifier("PDGFConfiguration") private final Marshaller xmlMarshaller;
    @Value("${app.datastore.configuration}") private final String configurationDirectory;
    @Value("${app.datastore.output}") private final String outputDirectory;

    @Override
    public GenerationEngineConfiguraiton<PDGFConfiguration> createGenerationEngineConfiguration(PDGFSchemaDefinition schemaDefinition) {
        PDGFConfiguration configuration = buildConfiguration(schemaDefinition);
        URI url = saveConfiguration(configuration);
        return new GenerationEngineConfiguraiton<>(url, configuration);
    }

    public PDGFConfiguration buildConfiguration(PDGFSchemaDefinition schemaDefinition) {
        return new PDGFConfiguration()
                .withScheduler(getDefaultScheduler())
                .withOutput(getDefaultOutput(getNameWithoutSchemaSuffix(schemaDefinition)))
                .withSchema(getDefaultSchema(schemaDefinition));
    }

    private Scheduler getDefaultScheduler() {
        return new Scheduler()
                .withName(DEFAULT_SCHEDULER);
    }

    private String getNameWithoutSchemaSuffix(PDGFSchemaDefinition schemaDefinition) {
        return StringUtils.removeEnd(schemaDefinition.getName(), PDGFSchemaDefinitionService.SCHEMA_NAME_SUFFIX);
    }

    private Output getDefaultOutput(String ontologyName) {
        return new Output()
                .withName(CSV_ROW_OUTPUT)
                .withFileTemplate("outputDir + table.getName() + fileEnding")
                .withOutputDir(getOutputDirectoryUrl(ontologyName).getPath())
                .withFileEnding(".csv")
                .withDelimiter(",")
                .withQuoteStrings(false)
                .withQuotationCharacter("\"")
                .withCharset(StandardCharsets.UTF_8.name())
                .withSortByRowID(true);
    }

    private URI getOutputDirectoryUrl(String ontologyName) {
        return urlProvider.getUrlForResource(outputDirectory, ontologyName + "/");
    }

    private Schema getDefaultSchema(PDGFSchemaDefinition schemaDefinition) {
        return new Schema()
                .withName(schemaDefinition.getName())
                .withTables(getTables(schemaDefinition));
    }

    private Tables getTables(PDGFSchemaDefinition schemaDefinition) {
        return new Tables()
                .withTable(getTablesList(schemaDefinition));
    }

    private List<Table> getTablesList(PDGFSchemaDefinition schemaDefinition) {
        return schemaDefinition.getTable().stream()
                .map(pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Table::getName)
                .map(this::getTable)
                .collect(Collectors.toList());
    }

    private Table getTable(String name) {
        return new Table()
                .withName(name);
    }

    public URI saveConfiguration(PDGFConfiguration configuration) {
        URI configurationUrl = getConfigurationUrl(getConfigurationName(configuration.getSchema()));
        storageService.saveResource(getConfigurationSaver(configuration), configurationUrl);
        return configurationUrl;
    }

    private String getConfigurationName(Schema schema) {
        return getNameWithoutSchemaSuffix(schema) + CONFIGURATION_NAME_SUFFIX;
    }

    private String getNameWithoutSchemaSuffix(Schema schema) {
        return StringUtils.removeEnd(schema.getName(), PDGFSchemaDefinitionService.SCHEMA_NAME_SUFFIX);
    }

    private URI getConfigurationUrl(String configurationName) {
        return urlProvider.getUrlForResource(configurationDirectory, configurationName + ".xml");
    }

    private Consumer<OutputStream> getConfigurationSaver(PDGFConfiguration configuration) {
        return ThrowingConsumer.wrapper(outputStream -> xmlMarshaller.marshal(configuration, outputStream));
    }

}
