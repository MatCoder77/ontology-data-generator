package pl.edu.pwr.ontologydatagenerator.domain.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@Getter
@RequiredArgsConstructor
public class GenerationEngineConfiguraiton<T>{

    private final URI url;
    private final T configuration;

}
