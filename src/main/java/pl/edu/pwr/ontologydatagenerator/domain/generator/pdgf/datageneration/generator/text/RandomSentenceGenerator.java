package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenRandomSentence;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

public class RandomSentenceGenerator extends GenRandomSentence implements Generator {

    public RandomSentenceGenerator(long min, long max, Distribution distribution) {
        this.min = min;
        this.max = max;
        this.distribution = distribution;
    }

}
