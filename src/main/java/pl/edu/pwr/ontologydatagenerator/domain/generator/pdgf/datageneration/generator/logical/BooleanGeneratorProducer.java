package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.logical;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class BooleanGeneratorProducer implements DataPropertyGeneratorProducer {

    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_BOOLEAN);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        return new BooleanGenerator(distributionProvider.getDistribution(context));
    }
    
}
