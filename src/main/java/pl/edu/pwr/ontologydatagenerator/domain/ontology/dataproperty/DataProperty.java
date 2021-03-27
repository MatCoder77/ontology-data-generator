package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty;

import lombok.Builder;
import org.semanticweb.owlapi.model.OWLLiteral;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.Map;
import java.util.Set;

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

}
