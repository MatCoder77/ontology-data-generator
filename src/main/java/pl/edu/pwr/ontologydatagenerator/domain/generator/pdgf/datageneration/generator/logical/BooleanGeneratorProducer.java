package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.logical;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GeneratorProducer;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class BooleanGeneratorProducer implements GeneratorProducer {

    private final DistributionProvider<GenerationContext, Distribution> distributionProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_BOOLEAN);
    }

    @Override
    public Generator buildGenerator(GenerationContext generationContext) {
        return new BooleanGenerator(distributionProvider.getDistribution(generationContext));
    }
    
}
