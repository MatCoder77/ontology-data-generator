package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.datacharcteristics.distribution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;

@Service
@RequiredArgsConstructor
public class PDGFDataPeropertyDistributionProvider implements DistributionProvider<DataPropertyGenerationContext, Distribution> {

    private final PDGFBaseDistributionProvider baseDistributionProvider;

    @Override
    public Distribution getDistribution(DataPropertyGenerationContext context) {
        return baseDistributionProvider.getDistributionBasedOnConfigurationOrDefault(context.getDataProperty());
    }

}
