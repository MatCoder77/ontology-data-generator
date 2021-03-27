package pl.edu.pwr.ontologydatagenerator.domain.ontology;

import java.net.URI;

public interface OntologyService<T> {

    T loadOntology(URI url);
    void saveOntology(T ontology, URI url);
    void validateOntology(T ontology);
    OntologyContainer<T> parseOntology(T ontology);

}
