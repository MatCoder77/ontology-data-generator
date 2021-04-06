package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.binary;

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
import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_BASE_64_BINARY;

@Service
@RequiredArgsConstructor
public class Base64GeneratorProducer implements DataPropertyGeneratorProducer {

    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;
    private final StringConstraintsProvider constraintsProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_BASE_64_BINARY);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext generationContext) {
        long minLength = constraintsProvider.getMinLength(generationContext);
        long maxLength = constraintsProvider.getMaxLength(generationContext);
        Distribution distribution = distributionProvider.getDistribution(generationContext);
        return new Base64Generator(minLength, maxLength, distribution);
    }

}
