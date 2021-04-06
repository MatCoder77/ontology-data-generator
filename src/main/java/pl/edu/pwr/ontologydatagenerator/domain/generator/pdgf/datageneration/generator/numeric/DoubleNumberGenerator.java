package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.DecimalPlaces;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenDoubleNumber;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

public class DoubleNumberGenerator extends GenDoubleNumber implements Generator {

    public DoubleNumberGenerator(double min, double max, int decimalPlaces, Distribution distribution) {
        this.minD = min;
        this.maxD = max;
        this.decimalPlaces = getDecimalPlaces(decimalPlaces);
        this.distribution = distribution;
    }

    private DecimalPlaces getDecimalPlaces(int decimalPlaces) {
        return new DecimalPlaces()
                .withValue(decimalPlaces)
                .withRoundPlainValue(true)
                .withRoundingMode(RoundingMode.HALF_EVEN.name());
    }

    enum RoundingMode {
        UP,
        DOWN,
        CEILING,
        FLOOR,
        HALF_UP,
        HALF_DOWN,
        HALF_EVEN,
        UNNECESSARY
    }

}
