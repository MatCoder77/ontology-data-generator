package pl.edu.pwr.ontologydatagenerator.domain.ontology.concept;

import com.google.common.base.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concept)) return false;
        Concept concept = (Concept) o;
        return Objects.equal(identifier, concept.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    public boolean isThing() {
        return identifier.getIri().isThing();
    }

    public boolean isNothing() {
        return identifier.getIri().isNothing();
    }

}
