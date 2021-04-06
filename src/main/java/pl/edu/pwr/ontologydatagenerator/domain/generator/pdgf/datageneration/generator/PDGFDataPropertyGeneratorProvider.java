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

    private final DataPropertyGenerationContext generationContext;
    private final List<DataPropertyGeneratorProducer> dataPropertyGeneratorProducers;

    @Override
    public Generator selectGenerator() {
        return getGenerator();
    }

    private Generator getGenerator() {
        List<DataPropertyGeneratorProducer> dataPropertyGeneratorProducersForDatatype = getGeneratorProducersForDatatype(generationContext.getDatatype());
        DataPropertyGeneratorProducer bestDataPropertyGeneratorProducer = getBestRatedGeneratorProducer(dataPropertyGeneratorProducersForDatatype);
        return bestDataPropertyGeneratorProducer.buildGenerator(generationContext);
    }

    private List<DataPropertyGeneratorProducer> getGeneratorProducersForDatatype(OWL2Datatype datatype) {
        return dataPropertyGeneratorProducers.stream()
                .filter(producer -> producer.isDataTypeSupported(datatype))
                .collect(Collectors.toList());
    }

    private DataPropertyGeneratorProducer getBestRatedGeneratorProducer(Collection<DataPropertyGeneratorProducer> dataPropertyGeneratorProducers) {
        return dataPropertyGeneratorProducers.stream()
                .findAny().get();
    }

}