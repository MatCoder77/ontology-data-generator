package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.constraints.StringConstraintsProvider;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class RandomStringGeneratorProducer implements GeneratorProducer {

    private final StringConstraintsProvider constraintsProvider;
    private final DistributionProvider<GenerationContext, Distribution> distributionProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_STRING, XSD_NORMALIZED_STRING, XSD_TOKEN, XSD_NAME, XSD_NCNAME, XSD_NMTOKEN);
    }

    @Override
    public Generator buildGenerator(GenerationContext generationContext) {
        return getGeneratorBasedOnContext(generationContext);
    }

    private Generator getGeneratorBasedOnContext(GenerationContext context) {
        if (isLongString(context)) {
            return getRandomSentenceGenerator(context);
        }
        return getRandomStringGenerator(context);
    }

    private boolean isLongString(GenerationContext context) {
        return context.getDatatype() == XSD_STRING && constraintsProvider.getMinLength(context) > 25;
    }

    private Generator getRandomSentenceGenerator(GenerationContext context) {
        long min = constraintsProvider.getMinLength(context);
        long max = constraintsProvider.getMaxLength(context);
        Distribution distribution = distributionProvider.getDistribution(context);
        return new RandomSentenceGenerator(min, max, distribution);
    }

    private Generator getRandomStringGenerator(GenerationContext context) {
        long min = constraintsProvider.getMinLength(context);
        long max = constraintsProvider.getMaxLength(context);
        return new RandomStringGenerator(min, max);
    }

}
