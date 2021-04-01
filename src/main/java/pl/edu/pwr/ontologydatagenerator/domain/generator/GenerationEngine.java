package pl.edu.pwr.ontologydatagenerator.domain.generator;

public interface GenerationEngine<T, R> {

    R generateData(T ontologyContainer);

}
