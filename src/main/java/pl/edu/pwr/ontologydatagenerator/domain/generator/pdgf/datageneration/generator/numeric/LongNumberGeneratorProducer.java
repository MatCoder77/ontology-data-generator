package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.datacharcteristics.constraints.PDGFDataPropertyConstraintsProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.RangeValue;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.ValueRangeConstraints;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalArgumentAppException;

import java.util.Optional;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class LongNumberGeneratorProducer implements DataPropertyGeneratorProducer {

    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;
    private final PDGFDataPropertyConstraintsProvider constraintsProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_INTEGER, XSD_NON_NEGATIVE_INTEGER, XSD_NON_POSITIVE_INTEGER, XSD_POSITIVE_INTEGER,
                XSD_NEGATIVE_INTEGER, XSD_LONG, XSD_INT, XSD_SHORT, XSD_BYTE, XSD_UNSIGNED_LONG, XSD_UNSIGNED_INT,
                XSD_UNSIGNED_SHORT, XSD_UNSIGNED_BYTE);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        Long min = getMin(context);
        Long max = getMax(context);
        Distribution distribution = distributionProvider.getDistribution(context);
        return new LongNumberGenerator(min, max, distribution);
    }

    private Long getMin(DataPropertyGenerationContext context) {
        return getMinFromRestritions(context)
                .or(() -> getMinBasedOnConfiguration(context))
                .or(() -> getMinBasedOnInferenceRules(context))
                .orElseGet(() -> getMinForDatatype(context));
    }

    private Optional<Long> getMinFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMin()
                .map(this::getIncrementedMinIfNotInclusive);
    }

    private ValueRangeConstraints<Long> getRangeConstraints(DataPropertyGenerationContext context) {
        return ValueRangeConstraints.of(context.getRestrictions(), Long::parseLong);
    }

    private Long getIncrementedMinIfNotInclusive(RangeValue<Long> min) {
        if (min.isInclusive()) {
            return min.getValue();
        }
        return min.getValue() + 1L;
    }

    private Optional<Long> getMinBasedOnConfiguration(DataPropertyGenerationContext context) {
        return getRangeConstraintsFromConfiguration(context).getMin()
                .map(this::getIncrementedMinIfNotInclusive);
    }

    private ValueRangeConstraints<Long> getRangeConstraintsFromConfiguration(DataPropertyGenerationContext context) {
        return ValueRangeConstraints.of(constraintsProvider.getDataPropertyConstraints(context.getDataProperty()), Long::parseLong);
    }

    private Optional<Long> getMinBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private long getMinForDatatype(DataPropertyGenerationContext context) {
        return switch (context.getDatatype()) {
            case RDFS_LITERAL, XSD_INTEGER, XSD_LONG, XSD_NEGATIVE_INTEGER, XSD_NON_POSITIVE_INTEGER -> Long.MIN_VALUE;
            case XSD_INT -> Integer.MIN_VALUE;
            case XSD_SHORT -> Short.MIN_VALUE;
            case XSD_BYTE -> Byte.MIN_VALUE;
            case XSD_NON_NEGATIVE_INTEGER, XSD_UNSIGNED_BYTE, XSD_UNSIGNED_SHORT, XSD_UNSIGNED_INT, XSD_UNSIGNED_LONG -> 0L;
            case XSD_POSITIVE_INTEGER -> 1L;
            default -> throw new IllegalArgumentAppException("Called procuder for not supported datatype");
        };
    }

    private Long getMax(DataPropertyGenerationContext context) {
        return getMaxFromRestritions(context)
                .or(() -> getMaxBasedOnConfiguration(context))
                .or(() -> getMaxBasedOnInferenceRules(context))
                .orElseGet(() -> getMaxForDatatype(context));
    }

    private Optional<Long> getMaxFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMax()
                .map(this::getDecrementedMaxIfNotInclusive);
    }

    private Long getDecrementedMaxIfNotInclusive(RangeValue<Long> max) {
        if (max.isInclusive()) {
            return max.getValue();
        }
        return max.getValue() - 1L;
    }

    private Optional<Long> getMaxBasedOnConfiguration(DataPropertyGenerationContext context) {
        return getRangeConstraintsFromConfiguration(context).getMax()
                .map(this::getDecrementedMaxIfNotInclusive);
    }

    private Optional<Long> getMaxBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private long getMaxForDatatype(DataPropertyGenerationContext context) {
        return switch (context.getDatatype()) {
            // PDGF not suport values larger than Long.MAX_VALUE - 1, so max value is limited to that value
            case RDFS_LITERAL, XSD_INTEGER, XSD_LONG, XSD_NON_NEGATIVE_INTEGER, XSD_POSITIVE_INTEGER, XSD_UNSIGNED_LONG -> Long.MAX_VALUE - 1;
            case XSD_NEGATIVE_INTEGER -> -1L;
            case XSD_NON_POSITIVE_INTEGER -> 0L;
            case XSD_INT -> Integer.MAX_VALUE;
            case XSD_SHORT -> Short.MAX_VALUE;
            case XSD_BYTE -> Byte.MAX_VALUE;
            case XSD_UNSIGNED_BYTE -> 255L;
            case XSD_UNSIGNED_SHORT -> 65535L;
            case XSD_UNSIGNED_INT -> 4294967295L;
            default -> throw new IllegalArgumentAppException("Called procuder for not supported datatype");
        };
    }

}
