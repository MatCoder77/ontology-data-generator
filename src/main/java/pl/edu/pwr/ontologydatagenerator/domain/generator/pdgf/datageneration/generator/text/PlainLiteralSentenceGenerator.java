package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary.DictionaryGenerator;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDF_PLAIN_LITERAL;

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

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, RDF_PLAIN_LITERAL);
    }

}
