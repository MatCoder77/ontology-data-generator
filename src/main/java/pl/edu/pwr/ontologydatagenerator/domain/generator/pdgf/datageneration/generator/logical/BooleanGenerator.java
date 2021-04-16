package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.logical;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

public class BooleanGenerator extends GenLongNumber implements Generator {

    public BooleanGenerator(Distribution distribution) {
        this.min = 0;
        this.max = 1;
        this.distribution = distribution;
    }

}
