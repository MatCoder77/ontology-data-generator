package pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty;

import lombok.Builder;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.Map;
import java.util.Set;

@Builder(setterPrefix = "with")
public class ObjectProperty implements HasIdentifier {

    private final Identifier identifier;
    private final ObjectPropertyDomain domain;
    private final ObjectPropertyRange range;
    private final Set<Identifier> equivalentProperties;
    private final Set<Identifier> disjointProperties;
    private final Set<Identifier> subProperties;
    private final Set<Identifier> superProperties;
    private final Map<Identifier, Set<Identifier>> valuesByIndividualIdentifier;
    private final boolean isTransitive;
    private final boolean isSymmetric;
    private final boolean isAssymetric;
    private final boolean isReflexive;
    private final boolean isIrreflexive;
    private final boolean isInverseFunctional;
    private final boolean isFunctional;
    private final Set<Identifier> inverseProperties;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

}
