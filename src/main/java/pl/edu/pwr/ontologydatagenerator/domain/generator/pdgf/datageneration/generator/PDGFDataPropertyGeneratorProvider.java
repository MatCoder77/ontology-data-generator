package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GeneratorSelector;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PDGFDataPropertyGeneratorProvider implements GeneratorSelector {

    private final GenerationContext generationContext;
    private final List<GeneratorProducer> generatorProducers;

    @Override
    public Generator selectGenerator() {
        return getGenerator();
    }

    private Generator getGenerator() {
        List<GeneratorProducer> generatorProducersForDatatype = getGeneratorProducersForDatatype(generationContext.getDatatype());
        GeneratorProducer bestGeneratorProducer = getBestRatedGeneratorProducer(generatorProducersForDatatype);
        return bestGeneratorProducer.buildGenerator(generationContext);
    }

    private List<GeneratorProducer> getGeneratorProducersForDatatype(OWL2Datatype datatype) {
        return generatorProducers.stream()
                .filter(producer -> producer.isDataTypeSupported(datatype))
                .collect(Collectors.toList());
    }

    private GeneratorProducer getBestRatedGeneratorProducer(Collection<GeneratorProducer> generatorProducers) {
        return generatorProducers.stream()
                .findAny().get();
    }

}