package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.binary;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenTemplate;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text.RandomSentenceGenerator;

import java.util.List;

public class Base64Generator extends GenTemplate implements Generator {

    public Base64Generator(long min, long max, Distribution distribution) {
        this.distribution = distribution;
        this.generators = List.of(new RandomSentenceGenerator(min, max, distribution));
        this.getValue = """
                <![CDATA[
                                    String text = generator[0].toString();
                                    String encodedText = java.util.Base64.getEncoder().encodeToString(text.getBytes());
                                    fvdto.setBothValues(encodedText);
                                ]]>""";
    }

}
