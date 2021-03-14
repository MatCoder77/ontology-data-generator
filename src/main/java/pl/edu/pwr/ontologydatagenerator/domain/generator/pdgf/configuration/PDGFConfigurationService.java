package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngineConfigurationService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PDGFConfigurationService implements GenerationEngineConfigurationService<PDGFConfiguration> {

    public static final String DEFAULT_SCHEDULER = "DefaultScheduler";
    public static final String CSV_ROW_OUTPUT = "CSVRowOutput";

    private final StorageService storageService;
    @Qualifier("PDGFConfiguration") private final Marshaller xmlMarshaller;

    @Override
    public void saveConfiguration(PDGFConfiguration configuration, URI url) {
        storageService.saveResource(getConfigurationSaver(configuration), url);
    }

    private Consumer<OutputStream> getConfigurationSaver(PDGFConfiguration configuration) {
        return ThrowingConsumer.wrapper(outputStream -> xmlMarshaller.marshal(configuration, outputStream));
    }

    @Override
    public PDGFConfiguration getDefaultConfiguration() {
        return new PDGFConfiguration()
                .withScheduler(getDefaultScheduler())
                .withOutput(getDefaultOutput())
                .withSchema(getDefaultSchema());
    }

    private static Scheduler getDefaultScheduler() {
        return new Scheduler()
                .withName(DEFAULT_SCHEDULER);
    }

    private static Output getDefaultOutput() {
        return new Output()
                .withName(CSV_ROW_OUTPUT)
                .withFileTemplate("outputDir + table.getName() + fileEnding")
                .withOutputDir("output")
                .withFileEnding(".csv")
                .withDelimiter(",")
                .withQuoteStrings(false)
                .withQuotationCharacter("\"")
                .withCharset(StandardCharsets.UTF_8.name())
                .withSortByRowID(true);
    }

    private static Schema getDefaultSchema() {
        return new Schema()
                .withName("default")
                .withTables(getEmptyTables());
    }

    private static Tables getEmptyTables() {
        return new Tables()
                .withTable(Collections.emptyList());
    }

}
