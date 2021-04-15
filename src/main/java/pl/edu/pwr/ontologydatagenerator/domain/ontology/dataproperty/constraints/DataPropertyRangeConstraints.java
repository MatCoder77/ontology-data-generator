package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints;

import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWLFacet;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface DataPropertyRangeConstraints {

    static <R, E> Optional<R> getFacetValue(Map<OWLFacet, E> restrictions, OWLFacet facet, Function<E, R> parser) {
        return Optional.ofNullable(restrictions.get(facet))
                .map(parser);
    }

    static Map<OWLFacet, OWLLiteral> getValuesByRestrictions(Collection<OWLFacetRestriction> restrictions) {
        return restrictions.stream()
                .collect(Collectors.toMap(OWLFacetRestriction::getFacet, OWLFacetRestriction::getFacetValue));
    }

}
