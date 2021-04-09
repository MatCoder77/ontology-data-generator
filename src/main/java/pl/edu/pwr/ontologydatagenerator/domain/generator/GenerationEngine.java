package pl.edu.pwr.ontologydatagenerator.domain.generator;

import java.util.stream.Stream;

public interface GenerationEngine<T, R> {

    Stream<R> generateData(T ontologyContainer);

}
