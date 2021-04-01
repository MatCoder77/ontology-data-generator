package pl.edu.pwr.ontologydatagenerator.domain.generator;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

public interface DistributionProvider {

    Distribution getDistribution(Identifier concept, DataProperty dataProperty);

    Distribution getDistribution(Identifier concept, ObjectProperty dataProperty);

}
