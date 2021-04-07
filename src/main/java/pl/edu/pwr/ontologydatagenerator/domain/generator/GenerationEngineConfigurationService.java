package pl.edu.pwr.ontologydatagenerator.domain.generator;

public interface GenerationEngineConfigurationService<T, E> {

    GenerationEngineConfiguraiton<T> createGenerationEngineConfiguration(E schemaDefinition);

}
