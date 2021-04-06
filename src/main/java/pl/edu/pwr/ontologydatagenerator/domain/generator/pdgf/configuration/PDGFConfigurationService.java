package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngineConfigurationService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PDGFConfigurationService implements GenerationEngineConfigurationService<PDGFConfiguration> {

    public static final String DEFAULT_SCHEDULER = "DefaultScheduler";
    public static final String CSV_ROW_OUTPUT = "CSVRowOutput";

    private final StorageService storageService;
    private final UrlProvider urlProvider;
    @Qualifier("PDGFConfiguration") private final Marshaller xmlMarshaller;
    @Value("${app.datastore.configuration}") private final String configurationDirectory;
    @Value("${app.datastore.output}") private final String outputDirectory;
    @Value("${app.generator.pdgf.configuration.default.name}") private final String defaultConfigurationName;

    @Override
    public URI getDefaultConfiguration() {
        URI configurationUrl = getConfigurationUrl(defaultConfigurationName);
        Resource configurationResource = storageService.getResource(configurationUrl);
        if (!configurationResource.exists()) {
            PDGFConfiguration defaultConfiguration = buildDefaultConfiguration();
            saveConfiguration(defaultConfiguration, configurationUrl);
        }
        return configurationUrl;
    }

    private URI getConfigurationUrl(String configurationName) {
        return urlProvider.getUrlForResource(configurationDirectory, configurationName);
    }

    @Override
    public PDGFConfiguration buildDefaultConfiguration() {
        return new PDGFConfiguration()
                .withScheduler(getDefaultScheduler())
                .withOutput(getDefaultOutput())
                .withSchema(getDefaultSchema());
    }

    private Scheduler getDefaultScheduler() {
        return new Scheduler()
                .withName(DEFAULT_SCHEDULER);
    }

    private Output getDefaultOutput() {
        return new Output()
                .withName(CSV_ROW_OUTPUT)
                .withFileTemplate("outputDir + table.getName() + fileEnding")
                .withOutputDir(getOutputDirectoryUrl().getPath())
                .withFileEnding(".csv")
                .withDelimiter(",")
                .withQuoteStrings(false)
                .withQuotationCharacter("\"")
                .withCharset(StandardCharsets.UTF_8.name())
                .withSortByRowID(true);
    }

    private URI getOutputDirectoryUrl() {
        return urlProvider.getUrlForResource(outputDirectory + "/");
    }

    private Schema getDefaultSchema() {
        return new Schema()
                .withName("default")
                .withTables(getEmptyTables());
    }

    private Tables getEmptyTables() {
        return new Tables()
                .withTable(new Table().withName("__placeholder__"));
    }

    @Override
    public void saveConfiguration(PDGFConfiguration configuration, URI url) {
        storageService.saveResource(getConfigurationSaver(configuration), url);
    }

    private Consumer<OutputStream> getConfigurationSaver(PDGFConfiguration configuration) {
        return ThrowingConsumer.wrapper(outputStream -> xmlMarshaller.marshal(configuration, outputStream));
    }

}
