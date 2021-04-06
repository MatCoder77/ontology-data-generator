package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

public interface ObjectPropertyGeneratorProducer {

    Generator buildGenerator(ObjectPropertyGenerationContext generationContext);

}
