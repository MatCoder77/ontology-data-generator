package pl.edu.pwr.ontologydatagenerator.domain.ontology;

import lombok.Builder;
import lombok.Getter;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

import java.util.Map;
import java.util.Optional;

@Getter
@Builder(setterPrefix = "with")
public class OntologyContainer<T> {

    private final T ontology;
    private final Map<Identifier, Concept> concepts;
    private final Map<Identifier, DataProperty> dataProperties;
    private final Map<Identifier, ObjectProperty> objectProperties;

    public Optional<Concept> getConcept(Identifier identifier) {
        return Optional.ofNullable(concepts.get(identifier));
    }

}
