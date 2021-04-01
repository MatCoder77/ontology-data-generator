package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty;

import lombok.Builder;
import lombok.Getter;
import org.semanticweb.owlapi.model.OWLLiteral;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@Builder(setterPrefix = "with")
public class DataProperty implements HasIdentifier {

    private final Identifier identifier;
    private final DataPropertyDomain domain;
    private final DataPropertyRange range;
    private final Set<Identifier> equivalentProperties;
    private final Set<Identifier> disjointProperties;
    private final Set<Identifier> superProperties;
    private final Set<Identifier> subProperties;
    private final Map<Identifier, Set<OWLLiteral>> valuesByIndividualIdentifier;
    private final boolean isFunctional;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public Optional<DataPropertyRange> getRange() {
        return Optional.ofNullable(range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataProperty)) return false;
        DataProperty that = (DataProperty) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

}
