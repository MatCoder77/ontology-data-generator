package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.List;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

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

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_LANGUAGE, RDF_LANG_STRING);
    }

}
