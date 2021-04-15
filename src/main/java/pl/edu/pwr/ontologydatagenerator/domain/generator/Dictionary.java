package pl.edu.pwr.ontologydatagenerator.domain.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.RangeValue;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.StringConstraints;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.ValueRangeConstraints;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Getter
@Setter
@RequiredArgsConstructor
public class Dictionary {

    private URI url;
    private Set<OWL2Datatype> supportedDatatypes;
    private Keywords keywords;
    private Map<OWLFacet, String> constraints = Collections.emptyMap();

    public boolean isDatatypeSupported(OWL2Datatype datatype) {
        return supportedDatatypes.contains(datatype);
    }

    public <T> boolean fulfillsConstraints(ValueRangeConstraints<T> generationConstraints, Function<String, T> parser, Comparator<T> comparator, UnaryOperator<T> unitSubtractor, UnaryOperator<T> unitAdder) {
        ValueRangeConstraints<T> dictionaryConstraints = ValueRangeConstraints.of(constraints, parser);
        return fulfillsMinConstraint(generationConstraints, dictionaryConstraints, comparator, unitSubtractor) &&
                fulfillsMaxConstraint(generationConstraints, dictionaryConstraints, comparator, unitAdder);
    }

    private <T> boolean fulfillsMinConstraint(ValueRangeConstraints<T> generationConstraints, ValueRangeConstraints<T> dictionaryConstrinats, Comparator<T> comparator, UnaryOperator<T> unitSubtractor) {
        return generationConstraints.getMin()
                .map(generationMin -> fulfillsMinConstraint(generationMin, dictionaryConstrinats, comparator, unitSubtractor))
                .orElse(true);
    }

    private <T> boolean fulfillsMinConstraint(RangeValue<T> generationMin, ValueRangeConstraints<T> dictionaryConstraints, Comparator<T> comparator, UnaryOperator<T> unitSubtractor) {
        return dictionaryConstraints.getMin()
                .map(dictionaryMin -> fulfillsMinConstraint(generationMin, dictionaryMin, comparator, unitSubtractor))
                .orElse(false);
    }

    private <T> boolean fulfillsMinConstraint(RangeValue<T> gerationMin, RangeValue<T> dictionaryMin, Comparator<T> comparator, UnaryOperator<T> unitSubtractor) {
        if (areBothInclusive(gerationMin, dictionaryMin) || areBothExclusive(gerationMin, dictionaryMin)) {
            return comparator.compare(dictionaryMin.getValue(), gerationMin.getValue()) >= 0;
        }
        if (!gerationMin.isInclusive() && dictionaryMin.isInclusive()) {
            return comparator.compare(dictionaryMin.getValue(), gerationMin.getValue()) > 0;
        }
        return comparator.compare(dictionaryMin.getValue(), unitSubtractor.apply(gerationMin.getValue())) > 0;
    }

    private <T> boolean areBothInclusive(RangeValue<T> generationValue, RangeValue<T> dictionaryValue) {
        return generationValue.isInclusive() && dictionaryValue.isInclusive();
    }

    private <T> boolean areBothExclusive(RangeValue<T> gerationValue, RangeValue<T> dictionaryValue) {
        return !gerationValue.isInclusive() && !dictionaryValue.isInclusive();
    }

    private <T> boolean fulfillsMaxConstraint(ValueRangeConstraints<T> generationConstraints, ValueRangeConstraints<T> dictionaryConstrinats, Comparator<T> comparator, UnaryOperator<T> unitAdder) {
        return generationConstraints.getMax()
                .map(generationMax -> fulfillsMaxConstraint(generationMax, dictionaryConstrinats, comparator, unitAdder))
                .orElse(true);
    }

    private <T> boolean fulfillsMaxConstraint(RangeValue<T> generationMax, ValueRangeConstraints<T> dictionaryConstraints, Comparator<T> comparator, UnaryOperator<T> unitAdder) {
        return dictionaryConstraints.getMax()
                .map(dictionaryMax -> fulfillsMaxConstraint(generationMax, dictionaryMax, comparator, unitAdder))
                .orElse(false);
    }

    private <T> boolean fulfillsMaxConstraint(RangeValue<T> gerationMax, RangeValue<T> dictionaryMax, Comparator<T> comparator, UnaryOperator<T> unitAdder) {
        if (areBothInclusive(gerationMax, dictionaryMax) || areBothExclusive(gerationMax, dictionaryMax)) {
            return comparator.compare(dictionaryMax.getValue(), gerationMax.getValue()) <= 0;
        }
        if (!gerationMax.isInclusive() && dictionaryMax.isInclusive()) {
            return comparator.compare(dictionaryMax.getValue(), gerationMax.getValue()) < 0;
        }
        return comparator.compare(dictionaryMax.getValue(), unitAdder.apply(gerationMax.getValue())) < 0;
    }

    public boolean fulfillsConstraints(StringConstraints generationConstraints) {
        StringConstraints dictionaryConstrinats = StringConstraints.of(constraints);
        return fulfillsMinLengthConstraint(generationConstraints, dictionaryConstrinats) &&
                fulfillsMaxLengthConstraint(generationConstraints, dictionaryConstrinats) &&
                fulfillsPatternConstraint(generationConstraints, dictionaryConstrinats);
    }

    private boolean fulfillsMinLengthConstraint(StringConstraints generationConstraints, StringConstraints dictionaryConstrinats) {
        return generationConstraints.getMinLength()
                .map(generationMinLength -> fulfillsMinLengthConstraint(generationMinLength, dictionaryConstrinats))
                .orElse(true);
    }

    private boolean fulfillsMinLengthConstraint(long generationMinLength, StringConstraints dictionaryConstrinats) {
        return dictionaryConstrinats.getMinLength()
                .map(dictionaryMinLength -> dictionaryMinLength >= generationMinLength)
                .orElse(false);
    }

    private boolean fulfillsMaxLengthConstraint(StringConstraints generationConstraints, StringConstraints dictionaryConstrinats) {
        return generationConstraints.getMaxLength()
                .map(generationMaxLength -> fulfillsMaxLengthConstraint(generationMaxLength, dictionaryConstrinats))
                .orElse(true);
    }

    private boolean fulfillsMaxLengthConstraint(long generationMaxLength, StringConstraints dictionaryConstrinats) {
        return dictionaryConstrinats.getMaxLength()
                .map(dictionaryMaxLength -> dictionaryMaxLength <= generationMaxLength)
                .orElse(false);
    }

    private boolean fulfillsPatternConstraint(StringConstraints generationConstraints, StringConstraints dictionaryConstrinats) {
        return generationConstraints.getPattern()
                .map(generationPattern -> fulfillsPatternConstraint(generationPattern, dictionaryConstrinats))
                .orElse(true);
    }

    private boolean fulfillsPatternConstraint(String generationPattern, StringConstraints dictionaryConstrinats) {
        return dictionaryConstrinats.getPattern()
                .map(dictionaryPattern -> dictionaryPattern.equals(generationPattern))
                .orElse(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dictionary)) return false;
        Dictionary that = (Dictionary) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }

}
