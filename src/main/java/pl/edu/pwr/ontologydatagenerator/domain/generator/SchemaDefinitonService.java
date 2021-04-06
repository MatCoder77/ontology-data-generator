package pl.edu.pwr.ontologydatagenerator.domain.generator;

import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;

public interface SchemaDefinitonService<T, E> {

    Schema<T> createSchemaDefinition(OntologyContainer<E> ontologyContainer);

}
