package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWLFacet;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ValueRangeConstraints<T> implements DataPropertyRangeConstraints {

    private final RangeValue<T> min;
    private final RangeValue<T> max;

    public static <T> ValueRangeConstraints<T> of(Collection<OWLFacetRestriction> restrictions, Function<String, T> parser) {
        Map<OWLFacet, OWLLiteral> valuesByRestrictions = DataPropertyRangeConstraints.getValuesByRestrictions(restrictions);
        RangeValue<T> min = getMin(valuesByRestrictions, parser).orElse(null);
        RangeValue<T> max = getMax(valuesByRestrictions, parser).orElse(null);
        return new ValueRangeConstraints<>(min, max);
    }

    private static <T> Optional<RangeValue<T>> getMin(Map<OWLFacet, OWLLiteral> restrictions, Function<String, T> parser) {
        return getMinInclusive(restrictions, parser)
                .or(() -> getMinExclusive(restrictions, parser));
    }

    private static <T> Optional<RangeValue<T>> getMinInclusive(Map<OWLFacet, OWLLiteral> restrictions, Function<String, T> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MIN_INCLUSIVE, parser)
                .map(value -> RangeValue.of(value, true));
    }

    private static <T> Optional<RangeValue<T>> getMinExclusive(Map<OWLFacet, OWLLiteral> restrictions, Function<String, T> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MIN_EXCLUSIVE, parser)
                .map(value -> RangeValue.of(value, false));
    }

    private static <T> Optional<RangeValue<T>> getMax(Map<OWLFacet, OWLLiteral> restrictions, Function<String, T> parser) {
        return getMaxInclusive(restrictions, parser)
                .or(() -> getMaxExclusive(restrictions, parser));
    }

    private static <T> Optional<RangeValue<T>> getMaxInclusive(Map<OWLFacet, OWLLiteral> restrictions, Function<String, T> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MAX_INCLUSIVE, parser)
                .map(value -> RangeValue.of(value, true));
    }

    private static <T> Optional<RangeValue<T>> getMaxExclusive(Map<OWLFacet, OWLLiteral> restrictions, Function<String, T> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MAX_EXCLUSIVE, parser)
                .map(value -> RangeValue.of(value, false));
    }

    public Optional<RangeValue<T>> getMin() {
        return Optional.ofNullable(min);
    }

    public Optional<RangeValue<T>> getMax() {
        return Optional.ofNullable(max);
    }

}
