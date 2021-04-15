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
import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_HEX_BINARY;

@Service
@RequiredArgsConstructor
public class HexadecimalGeneratorProducer implements DataPropertyGeneratorProducer {

    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;
    private final StringConstraintsProvider constraintsProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_HEX_BINARY);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        long minLength = constraintsProvider.getMinLength(context);
        long maxLength = constraintsProvider.getMaxLength(context);
        Distribution distribution = distributionProvider.getDistribution(context);
        return new HexadecimalGenerator(minLength, maxLength, distribution);
    }

}
