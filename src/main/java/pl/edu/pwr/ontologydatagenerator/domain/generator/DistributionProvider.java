package pl.edu.pwr.ontologydatagenerator.domain.generator;

import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

public interface DistributionProvider<T> {

    T getDistribution(Identifier concept, DataProperty dataProperty);

    T getDistribution(Identifier concept, ObjectProperty dataProperty);

}
