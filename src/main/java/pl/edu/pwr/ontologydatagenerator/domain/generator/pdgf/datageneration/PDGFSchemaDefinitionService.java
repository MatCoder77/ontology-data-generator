package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.SchemaDefinitonService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;

import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class PDGFSchemaDefinitionService implements SchemaDefinitonService<PDGFSchemaDefinition> {

    private final StorageService storageService;
    @Qualifier("PDGFDatageneration") private final Marshaller xmlMarshaller;

    @Override
    public void saveSchemaDefinition(PDGFSchemaDefinition schemaDefinition, URI url) {
        storageService.saveResource(getDataSchemaDefinitionSaver(schemaDefinition), url);
    }

    private Consumer<OutputStream> getDataSchemaDefinitionSaver(PDGFSchemaDefinition schemaDefinition) {
        return ThrowingConsumer.wrapper(outputStream -> xmlMarshaller.marshal(schemaDefinition, outputStream));
    }

}
