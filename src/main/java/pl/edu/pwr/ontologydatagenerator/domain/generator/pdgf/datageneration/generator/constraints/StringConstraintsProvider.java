package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.constraints;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.StringConstraints;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StringConstraintsProvider {

    private static final long DEFAULT_MIN_LENGTH = 5L;
    private static final long DEFAULT_MAX_LENGTH = 15L;

    public Long getMinLength(DataPropertyGenerationContext context) {
        return getMinLengthFromRestritions(context)
                .or(() -> getMinLengthBasedOnInferenceRules(context))
                .or(() -> getMinLengthBasedOnConfiguration(context))
                .orElse(DEFAULT_MIN_LENGTH);
    }

    private StringConstraints getRangeConstraints(DataPropertyGenerationContext context) {
        return StringConstraints.of(context.getRestrictions());
    }

    private Optional<Long> getMinLengthFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMinLength();
    }

    private Optional<Long> getMinLengthBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Long> getMinLengthBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    public Long getMaxLength(DataPropertyGenerationContext context) {
        return getMaxLengthFromRestritions(context)
                .or(() -> getMaxLengthBasedOnInferenceRules(context))
                .or(() -> getMaxBasedOnConfiguration(context))
                .orElse(DEFAULT_MAX_LENGTH);
    }

    private Optional<Long> getMaxLengthFromRestritions(DataPropertyGenerationContext context) {
        return getRangeConstraints(context).getMaxLength();
    }

    private Optional<Long> getMaxLengthBasedOnInferenceRules(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

    private Optional<Long> getMaxBasedOnConfiguration(DataPropertyGenerationContext context) {
        return Optional.empty();
    }

}
