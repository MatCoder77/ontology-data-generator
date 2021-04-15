package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GeneratorSelector;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalArgumentAppException;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PDGFDataPropertyGeneratorProvider implements GeneratorSelector {

    private final DataPropertyGenerationContext generationContext;
    private final List<DataPropertyGeneratorProducer> dataPropertyGeneratorProducers;

    @Override
    public Generator selectGenerator() {
        return getGenerator();
    }

    private Generator getGenerator() {
        List<DataPropertyGeneratorProducer> dataPropertyGeneratorProducersForDatatype = getApplicableGeneratorProducers();
        DataPropertyGeneratorProducer bestDataPropertyGeneratorProducer = getBestRatedGeneratorProducer(dataPropertyGeneratorProducersForDatatype);
        Generator selectedGenerator = bestDataPropertyGeneratorProducer.buildGenerator(generationContext);
        log.info("Selected {} generator for property {}", selectedGenerator.getClass().getSimpleName(), generationContext.getDataProperty().getName());
        return selectedGenerator;
    }

    private List<DataPropertyGeneratorProducer> getApplicableGeneratorProducers() {
        return dataPropertyGeneratorProducers.stream()
                .filter(producer -> producer.isApplicable(generationContext))
                .collect(Collectors.toList());
    }

    private DataPropertyGeneratorProducer getBestRatedGeneratorProducer(Collection<DataPropertyGeneratorProducer> dataPropertyGeneratorProducers) {
        return dataPropertyGeneratorProducers.stream()
                .max(Comparator.comparingDouble(producer -> producer.getScore(generationContext)))
                .orElseThrow(() -> new IllegalArgumentAppException("Error, at least one generator should be available!"));
    }

}