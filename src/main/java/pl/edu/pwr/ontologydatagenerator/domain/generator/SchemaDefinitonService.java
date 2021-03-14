package pl.edu.pwr.ontologydatagenerator.domain.generator;

import java.net.URI;

public interface SchemaDefinitonService<T> {

    void saveSchemaDefinition(T dataSchemaDefinition, URI url);

}
