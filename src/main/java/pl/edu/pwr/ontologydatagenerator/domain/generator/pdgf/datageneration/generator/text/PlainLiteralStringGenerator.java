package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary.DictionaryGenerator;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDF_PLAIN_LITERAL;

public class PlainLiteralStringGenerator extends GenSequential implements Generator {

    public PlainLiteralStringGenerator(long min, long max, String languageTag) {
        this.concatenateResults = true;
        this.generators = List.of(new RandomStringGenerator(min, max), getLanguageTagSeparatorGenerator(), languageTag);
    }

    public PlainLiteralStringGenerator(long min, long max) {
        this.concatenateResults = true;
        this.generators = List.of(new RandomStringGenerator(min, max), getLanguageTagSeparatorGenerator(), new LanguageTagGenerator());
    }

    public PlainLiteralStringGenerator(long min, long max, URI languageTagDictionaryUrl) {
        this.concatenateResults = true;
        this.generators = List.of(new RandomStringGenerator(min, max), getLanguageTagSeparatorGenerator(), new DictionaryGenerator(languageTagDictionaryUrl, null, 1));
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
