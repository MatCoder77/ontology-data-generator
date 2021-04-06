package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.uri;

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

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_ANY_URI;

@Service
@RequiredArgsConstructor
public class UriGeneratorProducer implements GeneratorProducer {

    private final DistributionProvider<GenerationContext, Distribution> distributionProvider;
    private final StringConstraintsProvider constraintsProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_ANY_URI);
    }

    @Override
    public Generator buildGenerator(GenerationContext generationContext) {
        long min = constraintsProvider.getMinLength(generationContext);
        long max = constraintsProvider.getMaxLength(generationContext);
        return new UriGenerator("http", min, max);
    }

}
