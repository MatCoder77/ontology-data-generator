package pl.edu.pwr.ontologydatagenerator.domain.generator;

import java.net.URI;

public interface GenerationEngineConfigurationService<T> {

    void saveConfiguration(T configuration, URI url);
    T getDefaultConfiguration();

}
