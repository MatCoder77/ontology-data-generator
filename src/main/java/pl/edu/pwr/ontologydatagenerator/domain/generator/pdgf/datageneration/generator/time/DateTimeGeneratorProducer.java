package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.time;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.RangeValue;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.ValueRangeConstraints;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class DateTimeGeneratorProducer implements DataPropertyGeneratorProducer {

    public static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss[XXX]")
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter();
    private static final Function<String, Temporal> PARSER = string -> TIME_FORMATTER.parse(string, OffsetDateTime::from);
    private static final DateTimePrecisson DEFAULT_PRECISION = DateTimePrecisson.DAYS;
    private static final Temporal DEFAULT_MIN = PARSER.apply("1990-01-01T00:00:00Z");
    private static final Temporal DEFAULT_MAX = PARSER.apply("2020-05-01T00:00:00Z");

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_DATE_TIME, XSD_DATE_TIME_STAMP);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        Temporal min = getMin(context);
        Temporal max = getMax(context);
        DateTimePrecisson precission = getPrecision(context);
        return new DateTimeGenerator(min, max, precission);
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
        return min.getValue().plus(1, getPrecision(context).getUnit());
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
        return max.getValue().minus(1, getPrecision(context).getUnit());
    }

    private Optional<Temporal> getMaxBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Temporal> getMaxBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private DateTimePrecisson getPrecision(DataPropertyGenerationContext context) {
        return getPrecisionBasedOnInferenceRules(context)
                .or(() -> getPrecisionBasedOnConfiguration(context))
                .orElse(DEFAULT_PRECISION);
    }

    private Optional<DateTimePrecisson> getPrecisionBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<DateTimePrecisson> getPrecisionBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

}
