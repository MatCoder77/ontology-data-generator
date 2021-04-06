package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenLongNumber;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

public class LongNumberGenerator extends GenLongNumber implements Generator {

    public LongNumberGenerator(long min, long max, Distribution distribution) {
        this.min = min;
        this.max = max;
        this.distribution = distribution;
    }

}
