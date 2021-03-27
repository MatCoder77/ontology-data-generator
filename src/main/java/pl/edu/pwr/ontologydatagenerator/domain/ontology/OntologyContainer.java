package pl.edu.pwr.ontologydatagenerator.domain.ontology;

import lombok.Getter;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class OntologyContainer<T> {

    private final T ontology;
    private final Map<Identifier, Concept> concepts;
    private final Map<Identifier, DataProperty> dataProperties;
    private final Map<Identifier, ObjectProperty> objectProperties;

    private OntologyContainer(T ontology) {
        this.ontology = ontology;
        this.concepts = new LinkedHashMap<>();
        this.dataProperties = new LinkedHashMap<>();
        this.objectProperties = new LinkedHashMap<>();
    }

    public static <T> OntologyContainer<T> of(T ontology) {
        return new OntologyContainer<>(ontology);
    }

}
