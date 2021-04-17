package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.datacharcteristics.distribution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.ObjectPropertyGenerationContext;

@Service
@RequiredArgsConstructor
public class PDFGObjectPropertyDistributionProvider implements DistributionProvider<ObjectPropertyGenerationContext, Distribution> {

    private final PDGFBaseDistributionProvider baseDistributionProvider;

    @Override
    public Distribution getDistribution(ObjectPropertyGenerationContext generationContext) {
        return baseDistributionProvider.getDistributionBasedOnConfigurationOrDefault(generationContext.getObjectProperty());
    }

}
