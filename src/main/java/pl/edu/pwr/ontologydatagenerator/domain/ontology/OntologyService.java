package pl.edu.pwr.ontologydatagenerator.domain.ontology;

import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngine;

import java.net.URI;

public interface OntologyService<T, R> {

    T loadOntology(URI url);

    void validateOntology(T ontology);

    void generateInstances(T ontology, GenerationEngine<OntologyContainer<T>, R> generationEngine);

    void saveOntology(T ontology, URI url);

    OntologyContainer<T> parseOntology(T ontology);

}
