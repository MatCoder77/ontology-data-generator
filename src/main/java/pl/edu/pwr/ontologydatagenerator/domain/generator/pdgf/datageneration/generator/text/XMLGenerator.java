package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.List;

public class XMLGenerator extends GenSequential implements Generator {

    public XMLGenerator(long min, long max) {
        this.concatenateResults = true;
        this.generators = List.of(getStaticValueGenerator("<xs:element ref=\""), new RandomStringGenerator(min, max), getStaticValueGenerator("\"/>"));
    }

    private GenStaticValue getStaticValueGenerator(String value) {
        return new GenStaticValue()
                .withValue(value);
    }

}
