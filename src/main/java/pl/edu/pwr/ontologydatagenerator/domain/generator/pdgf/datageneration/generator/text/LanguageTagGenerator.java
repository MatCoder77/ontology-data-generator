package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.List;

public class LanguageTagGenerator extends GenSequential implements Generator {

    public LanguageTagGenerator() {
        this.concatenateResults = true;
        this.generators = List.of(
                new RandomStringGenerator(2, 2, "abcdefghijklmnopqrstuvwxyz"),
                getSeparatorGenerator(),
                new RandomStringGenerator(2, 2, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    private GenStaticValue getSeparatorGenerator() {
        return new GenStaticValue()
                .withValue("-");
    }

}
