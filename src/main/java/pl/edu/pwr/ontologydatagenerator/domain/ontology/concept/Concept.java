package pl.edu.pwr.ontologydatagenerator.domain.ontology.concept;

import lombok.Builder;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.instance.Instance;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

import java.util.Map;

@Builder(setterPrefix = "with")
public class Concept implements HasIdentifier {

    private final Identifier identifier;
    private final Map<Identifier, DataProperty> dataProperties ;
    private final Map<Identifier, ObjectProperty> objectProperties;
    private final Map<Identifier, Concept> disjointWith;
    private final Map<Identifier, Concept> equivalentTo;
    private final Map<Identifier, Instance> instances;
    private final boolean isAnonymous = false;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

}
