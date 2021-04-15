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
        Long min = getMinLength(valuesByRestrictions, StringConstraints::mapLiteralToLong).orElse(null);
        Long max = getMaxLength(valuesByRestrictions, StringConstraints::mapLiteralToLong).orElse(null);
        String pattern = getPattern(valuesByRestrictions, OWLLiteral::getLiteral).orElse(null);
        return new StringConstraints(min, max, pattern);
    }

    private static <E> Optional<Long> getMinLength(Map<OWLFacet, E> restrictions, Function<E, Long> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MIN_LENGTH, parser)
                .or(() -> getExactLength(restrictions, parser));
    }

    private static Long mapLiteralToLong(OWLLiteral literal) {
        return Long.parseLong(literal.getLiteral());
    }

    private static <E> Optional<Long> getExactLength(Map<OWLFacet, E> restrictions, Function<E, Long> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.LENGTH, parser);
    }

    private static <E> Optional<Long> getMaxLength(Map<OWLFacet, E> restrictions, Function<E, Long> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.MAX_LENGTH, parser)
                .or(() -> getExactLength(restrictions, parser));
    }

    private static <E> Optional<String> getPattern(Map<OWLFacet, E> restrictions, Function<E, String> parser) {
        return DataPropertyRangeConstraints.getFacetValue(restrictions, OWLFacet.PATTERN, parser);
    }

    public static StringConstraints of(Map<OWLFacet, String> valuesByRestrictions) {
        Long min = getMinLength(valuesByRestrictions, Long::parseLong).orElse(null);
        Long max = getMaxLength(valuesByRestrictions, Long::parseLong).orElse(null);
        String pattern = getPattern(valuesByRestrictions, Function.identity()).orElse(null);
        return new StringConstraints(min, max, pattern);
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
