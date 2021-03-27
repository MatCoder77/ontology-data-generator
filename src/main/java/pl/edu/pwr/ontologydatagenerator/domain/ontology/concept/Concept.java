package pl.edu.pwr.ontologydatagenerator.domain.ontology.concept;

import lombok.Builder;
import lombok.Getter;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

import java.util.Map;
import java.util.Set;

@Getter
@Builder(setterPrefix = "with")
public class Concept implements HasIdentifier {

    private final Identifier identifier;
    private final Map<Identifier, DataProperty> dataProperties ;
    private final Map<Identifier, ObjectProperty> objectProperties;
    private final Set<Identifier> disjointConcepts;
    private final Set<Identifier> equivalentConcepts;
    private final Set<Identifier> instances;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

}
