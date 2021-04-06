package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
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
public class DoubleGeneratorProducer implements DataPropertyGeneratorProducer {

    private static final int DEFAULT_PRECISION = 5;
    private static final String NOT_SUPPORTED_DATATYPE = "Called procuder for not supported datatype";

    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_DECIMAL, XSD_DOUBLE, XSD_FLOAT, OWL_RATIONAL, OWL_REAL);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext generationContext) {
        double min = getMin(generationContext);
        double max = getMax(generationContext);
        int precision = getPrecision(generationContext);
        Distribution distribution = distributionProvider.getDistribution(generationContext);
        return new DoubleNumberGenerator(min, max, precision, distribution);
    }

    private Double getMin(DataPropertyGenerationContext context) {
        return getMinFromRestritions(context)
                .or(() -> getMinBasedOnInferenceRules(context))
                .or(() -> getMinBasedOnConfiguration(context))
                .orElseGet(() -> getMinForDatatype(context));
    }

    private ValueRangeConstraints<Double> getRangeConstraints(DataPropertyGenerationContext context) {
        return ValueRangeConstraints.of(context.getRestrictions(), Double::parseDouble);
    }

    private Optional<Double> getMinFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMin()
                .map(min -> getIncrementedMinIfNotInclusive(min, context));
    }

    private Double getIncrementedMinIfNotInclusive(RangeValue<Double> min, DataPropertyGenerationContext context) {
        if (min.isInclusive()) {
            return min.getValue();
        }
        return min.getValue() + getPrecision(context);
    }

    private Optional<Double> getMinBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Double> getMinBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private double getMinForDatatype(DataPropertyGenerationContext context) {
        return switch (context.getDatatype()) {
            case RDFS_LITERAL, XSD_DECIMAL, XSD_DOUBLE, OWL_RATIONAL, OWL_REAL -> -Double.MAX_VALUE;
            case XSD_FLOAT -> -Float.MAX_VALUE;
            default -> throw new IllegalArgumentAppException(NOT_SUPPORTED_DATATYPE);
        };
    }

    private double getMax(DataPropertyGenerationContext context) {
        return getMaxFromRestritions(context)
                .or(() -> getMaxBasedOnInferenceRules(context))
                .or(() -> getMaxBasedOnConfiguration(context))
                .orElseGet(() -> getMaxForDatatype(context));
    }

    private Optional<Double> getMaxFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMax()
                .map(max -> getIncrementedMaxIfInclusive(max, context));
    }

    private double getIncrementedMaxIfInclusive(RangeValue<Double> max, DataPropertyGenerationContext context) {
        if (!max.isInclusive()) {
            return max.getValue();
        }
        return max.getValue() + getPrecision(context);
    }

    private Optional<Double> getMaxBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Double> getMaxBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private double getMaxForDatatype(DataPropertyGenerationContext context) {
        return switch (context.getDatatype()) {
            case RDFS_LITERAL, XSD_DECIMAL, XSD_DOUBLE, OWL_RATIONAL, OWL_REAL -> Double.MAX_VALUE;
            case XSD_FLOAT -> Float.MAX_VALUE;
            default -> throw new IllegalArgumentAppException(NOT_SUPPORTED_DATATYPE);
        };
    }

    private int getPrecision(DataPropertyGenerationContext context) {
        return getPrecisionBasedOnInferenceRules(context)
                .or(() -> getPrecisionBasedOnConfiguration(context))
                .orElseGet(() -> getPrecisionForDatatype(context));
    }

    private Optional<Integer> getPrecisionBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Integer> getPrecisionBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.of(DEFAULT_PRECISION);
    }

    private int getPrecisionForDatatype(DataPropertyGenerationContext context) {
        return switch (context.getDatatype()) {
            case RDFS_LITERAL, XSD_DECIMAL, XSD_DOUBLE, OWL_RATIONAL, OWL_REAL -> 36;
            case XSD_FLOAT -> 23;
            default -> throw new IllegalArgumentAppException(NOT_SUPPORTED_DATATYPE);
        };
    }

}