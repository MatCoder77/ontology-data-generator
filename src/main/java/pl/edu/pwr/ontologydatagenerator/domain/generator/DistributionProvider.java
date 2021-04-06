package pl.edu.pwr.ontologydatagenerator.domain.generator;

public interface DistributionProvider<T, R> {
    
    R getDistribution(T generationContext);

}
