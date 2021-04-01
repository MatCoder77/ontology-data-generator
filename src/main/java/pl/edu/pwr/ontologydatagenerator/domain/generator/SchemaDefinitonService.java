package pl.edu.pwr.ontologydatagenerator.domain.generator;

import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;

import java.net.URI;

public interface SchemaDefinitonService<T, E> {

    T buildSchemaDefinition(OntologyContainer<E> ontologyContainer);
    void saveSchemaDefinition(T dataSchemaDefinition, URI url);

}
