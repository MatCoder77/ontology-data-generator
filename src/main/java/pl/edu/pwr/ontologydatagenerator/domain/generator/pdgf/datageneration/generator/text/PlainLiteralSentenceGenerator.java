package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary.DictionaryGenerator;

import java.net.URI;
import java.util.List;

public class PlainLiteralSentenceGenerator extends GenSequential implements Generator {

    public PlainLiteralSentenceGenerator(long min, long max, String languageTag, Distribution distribution) {
        this.concatenateResults = true;
        this.generators = List.of(new RandomSentenceGenerator(min, max, distribution), getLanguageTagSeparatorGenerator(), languageTag);
    }

    public PlainLiteralSentenceGenerator(long min, long max, Distribution distribution) {
        this.concatenateResults = true;
        this.generators = List.of(new RandomSentenceGenerator(min, max, distribution), getLanguageTagSeparatorGenerator(), new LanguageTagGenerator());
    }

    public PlainLiteralSentenceGenerator(long min, long max, Distribution distribution, URI languageTagsUrls) {
        this.concatenateResults = true;
        this.generators = List.of(new RandomSentenceGenerator(min, max, distribution), getLanguageTagSeparatorGenerator(), new DictionaryGenerator(languageTagsUrls, distribution, 1));
    }

    private GenStaticValue getLanguageTagSeparatorGenerator() {
        return new GenStaticValue()
                .withValue("@");
    }

}
