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
public class StringConstraints implements DataPropertyRangeConstraints {

    private final Long minLength;
    private final Long maxLength;
    private final String pattern;

    public static StringConstraints of(Collection<OWLFacetRestriction> restrictions) {
        Map<OWLFacet, OWLLiteral> valuesByRestrictions = DataPropertyRangeConstraints.getValuesByRestrictions(restrictions);
        Long min = getMinLength(valuesByRestrictions).orElse(null);
        Long max = getMaxLength(valuesByRestrictions).orElse(null);
        String pattern = getPattern(valuesByRestrictions).orElse(null);
        return new StringConstraints(min, max, pattern);
    }

    private static Optional<Long> getMinLength(Map<OWLFacet, OWLLiteral> restrictions) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MIN_LENGTH, Long::parseLong)
                .or(() -> getExactLength(restrictions));
    }

    private static Optional<Long> getExactLength(Map<OWLFacet, OWLLiteral> restrictions) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.LENGTH, Long::parseLong);
    }

    private static Optional<Long> getMaxLength(Map<OWLFacet, OWLLiteral> restrictions) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MAX_LENGTH, Long::parseLong)
                .or(() -> getExactLength(restrictions));
    }

    private static Optional<String> getPattern(Map<OWLFacet, OWLLiteral> restrictions) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.PATTERN, Function.identity());
    }

    public Optional<Long> getMinLength() {
        return Optional.ofNullable(minLength);
    }

    public Optional<Long> getMaxLength() {
        return Optional.ofNullable(maxLength);
    }

    public Optional<String> getPattern() {
        return Optional.ofNullable(pattern);
    }

}
