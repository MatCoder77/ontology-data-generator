package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.constraints.StringConstraintsProvider;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDF_PLAIN_LITERAL;

@Service
@RequiredArgsConstructor
public class PlainLiteralGeneratorProducer implements DataPropertyGeneratorProducer {

    private final StringConstraintsProvider constraintsProvider;
    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, RDF_PLAIN_LITERAL);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        return getGeneratorBasedOnContext(context);
    }

    private Generator getGeneratorBasedOnContext(DataPropertyGenerationContext context) {
        long min = constraintsProvider.getMinLength(context);
        if (min > 25) {
            return getPlainLiteralSentenceGenerator(context);
        }
        return getPlainLiteralStringGenerator(context);
    }

    private Generator getPlainLiteralSentenceGenerator(DataPropertyGenerationContext context) {
        long min = constraintsProvider.getMinLength(context);
        long max = constraintsProvider.getMaxLength(context);
        Distribution distribution = distributionProvider.getDistribution(context);
        return new PlainLiteralSentenceGenerator(min, max, "en-US", distribution);
    }

    private Generator getPlainLiteralStringGenerator(DataPropertyGenerationContext context) {
        long min = constraintsProvider.getMinLength(context);
        long max = constraintsProvider.getMaxLength(context);
        return new PlainLiteralStringGenerator(min, max, "en-US");
    }

}
