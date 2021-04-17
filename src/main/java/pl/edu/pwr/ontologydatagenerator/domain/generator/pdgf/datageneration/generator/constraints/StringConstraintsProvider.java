package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.constraints;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.datacharcteristics.constraints.PDGFDataPropertyConstraintsProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.StringConstraints;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StringConstraintsProvider {

    private static final long DEFAULT_MIN_LENGTH = 5L;
    private static final long DEFAULT_MAX_LENGTH = 15L;

    private final PDGFDataPropertyConstraintsProvider constraintsProvider;

    public Long getMinLength(DataPropertyGenerationContext context) {
        return getMinLengthFromRestritions(context)
                .or(() -> getMinLengthBasedOnConfiguration(context))
                .or(() -> getMinLengthBasedOnInferenceRules(context))
                .orElse(DEFAULT_MIN_LENGTH);
    }

    private Optional<Long> getMinLengthFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMinLength();
    }

    private StringConstraints getRangeConstraints(DataPropertyGenerationContext context) {
        return StringConstraints.of(context.getRestrictions());
    }

    private Optional<Long> getMinLengthBasedOnConfiguration(DataPropertyGenerationContext context) {
        return getRangeConstraintsFromConfiguration(context).getMinLength();
    }

    private StringConstraints getRangeConstraintsFromConfiguration(DataPropertyGenerationContext context) {
        return StringConstraints.of(constraintsProvider.getDataPropertyConstraints(context.getDataProperty()));
    }

    private Optional<Long> getMinLengthBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    public Long getMaxLength(DataPropertyGenerationContext context) {
        return getMaxLengthFromRestritions(context)
                .or(() -> getMaxBasedOnConfiguration(context))
                .or(() -> getMaxLengthBasedOnInferenceRules(context))
                .orElse(DEFAULT_MAX_LENGTH);
    }

    private Optional<Long> getMaxLengthFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMaxLength();
    }

    private Optional<Long> getMaxBasedOnConfiguration(DataPropertyGenerationContext context) {
        return getRangeConstraintsFromConfiguration(context).getMaxLength();
    }

    private Optional<Long> getMaxLengthBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

}
