package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenRandomString;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

public class RandomStringGenerator extends GenRandomString implements Generator {

    public RandomStringGenerator(long min, long max, String characters) {
        this.min = min;
        this.max = max;
        this.characters = characters;
    }

    public RandomStringGenerator(long min, long max) {
        this.min = min;
        this.max = max;
    }

}
