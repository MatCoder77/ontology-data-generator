package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.time;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.RangeValue;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.ValueRangeConstraints;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class DataTimeGeneratorProducer implements DataPropertyGeneratorProducer {

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss[XXX]")
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter();
    private static final Function<String, Temporal> PARSER = string -> formatter.parse(string, OffsetDateTime::from);
    private static final TemporalUnit DEFAULT_PRECISION = ChronoUnit.DAYS;
    private static final Temporal DEFAULT_MIN = PARSER.apply("1990-01-01T00:00:00Z");
    private static final Temporal DEFAULT_MAX = PARSER.apply("2020-03-01T00:00:00Z");

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_DATE_TIME, XSD_DATE_TIME_STAMP);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        Temporal min = getMin(context);
        Temporal max = getMax(context);
        TemporalUnit precission = getPrecision(context);
        String format = getFormatBasedOnPrecission((ChronoUnit) precission);
        return new DateTimeGenerator(min, max, format);
    }

    private Temporal getMin(DataPropertyGenerationContext context) {
        return getMinFromRestritions(context)
                .or(() -> getMinBasedOnInferenceRules(context))
                .or(() -> getMinBasedOnConfiguration(context))
                .orElse(DEFAULT_MIN);
    }

    private ValueRangeConstraints<Temporal> getRangeConstraints(DataPropertyGenerationContext context) {
        return ValueRangeConstraints.of(context.getRestrictions(), PARSER);
    }

    private Optional<Temporal> getMinFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMin()
                .map(min -> getIncrementedMinIfNotInclusive(min, context));
    }

    private Temporal getIncrementedMinIfNotInclusive(RangeValue<Temporal> min, DataPropertyGenerationContext context) {
        if (min.isInclusive()) {
            return min.getValue();
        }
        return min.getValue().plus(1, getPrecision(context));
    }

    private Optional<Temporal> getMinBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Temporal> getMinBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Temporal getMax(DataPropertyGenerationContext context) {
        return getMaxFromRestritions(context)
                .or(() -> getMaxBasedOnInferenceRules(context))
                .or(() -> getMaxBasedOnConfiguration(context))
                .orElse(DEFAULT_MAX);
    }

    private Optional<Temporal> getMaxFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMax()
                .map(max -> getDecrementedMaxIfNotInclusive(max, context));
    }

    private Temporal getDecrementedMaxIfNotInclusive(RangeValue<Temporal> max, DataPropertyGenerationContext context) {
        if (max.isInclusive()) {
            return max.getValue();
        }
        return max.getValue().minus(1, getPrecision(context));
    }

    private Optional<Temporal> getMaxBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Temporal> getMaxBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private TemporalUnit getPrecision(DataPropertyGenerationContext context) {
        return getPrecisionBasedOnInferenceRules(context)
                .or(() -> getPrecisionBasedOnConfiguration(context))
                .orElse(DEFAULT_PRECISION);
    }

    private Optional<TemporalUnit> getPrecisionBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<TemporalUnit> getPrecisionBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private String getFormatBasedOnPrecission(ChronoUnit precission) {
        return switch (precission) {
            case YEARS -> "yyyy";
            case MONTHS -> "yyyy-MM";
            case DAYS -> "yyyy-MM-dd";
            case HOURS -> "yyyy-MM-dd'T'HH";
            case MINUTES -> "yyyy-MM-dd'T'HH:mm";
            case SECONDS -> "yyyy-MM-dd'T'HH:mm:ss";
            default -> throw new IllegalStateAppException("Unsupported time prescission");
        };
    }

}
