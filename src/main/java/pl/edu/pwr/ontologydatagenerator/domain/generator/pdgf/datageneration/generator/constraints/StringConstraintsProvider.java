package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.constraints;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.StringConstraints;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StringConstraintsProvider {

    private static final long DEFAULT_MIN_LENGTH = 5L;
    private static final long DEFAULT_MAX_LENGTH = 15L;

    public Long getMinLength(GenerationContext context) {
        return getMinLengthFromRestritions(context)
                .or(() -> getMinLengthBasedOnInferenceRules(context))
                .or(() -> getMinLengthBasedOnConfiguration(context))
                .orElse(DEFAULT_MIN_LENGTH);
    }

    private StringConstraints getRangeConstraints(GenerationContext context) {
        return StringConstraints.of(context.getRestrictions());
    }

    private Optional<Long> getMinLengthFromRestritions(GenerationContext context) {
        return getRangeConstraints(context).getMinLength();
    }

    private Optional<Long> getMinLengthBasedOnInferenceRules(GenerationContext context) {
        return Optional.empty();
    }

    private Optional<Long> getMinLengthBasedOnConfiguration(GenerationContext context) {
        return Optional.empty();
    }

    public Long getMaxLength(GenerationContext context) {
        return getMaxLengthFromRestritions(context)
                .or(() -> getMaxLengthBasedOnInferenceRules(context))
                .or(() -> getMaxBasedOnConfiguration(context))
                .orElse(DEFAULT_MAX_LENGTH);
    }

    private Optional<Long> getMaxLengthFromRestritions(GenerationContext context) {
        return getRangeConstraints(context).getMaxLength();
    }

    private Optional<Long> getMaxLengthBasedOnInferenceRules(GenerationContext context) {
        return Optional.empty();
    }

    private Optional<Long> getMaxBasedOnConfiguration(GenerationContext context) {
        return Optional.empty();
    }

}
