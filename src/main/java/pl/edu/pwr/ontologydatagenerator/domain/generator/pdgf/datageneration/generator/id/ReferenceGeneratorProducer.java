package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.id;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.ObjectPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.ObjectPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferenceGeneratorProducer implements ObjectPropertyGeneratorProducer {

    private final DistributionProvider<ObjectPropertyGenerationContext, Distribution> distributionProvider;

    @Override
    public Generator buildGenerator(ObjectPropertyGenerationContext generationContext) {
        Set<Concept> instantiableConceptsInRange = getInstantablieConceptsInRange(generationContext);
        Distribution tableSelectionDistribution = null;
        Distribution rowSelectionDistribution = distributionProvider.getDistribution(generationContext);
        return new ReferenceGenerator(instantiableConceptsInRange, ReferenceGenerator.ChooseType.RANDOM, ReferenceGenerator.From.HISTORICAL, tableSelectionDistribution, rowSelectionDistribution);
    }

    private Set<Concept> getInstantablieConceptsInRange(ObjectPropertyGenerationContext generationContext) {
        return generationContext.getObjectProperty().getRange().getAllConceptsInRange().stream()
                .map(identifier -> generationContext.getContainer().getConcept(identifier))
                .flatMap(Optional::stream)
                .filter(conceptInRange -> generationContext.getConceptsToInstantiate().contains(conceptInRange))
                .collect(Collectors.toSet());
    }

}
