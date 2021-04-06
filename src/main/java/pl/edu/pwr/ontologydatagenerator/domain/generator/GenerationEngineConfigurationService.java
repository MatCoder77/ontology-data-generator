package pl.edu.pwr.ontologydatagenerator.domain.generator;

import java.net.URI;

public interface GenerationEngineConfigurationService<T> {

    URI getDefaultConfiguration();

    T buildDefaultConfiguration();

    void saveConfiguration(T configuration, URI url);

}
