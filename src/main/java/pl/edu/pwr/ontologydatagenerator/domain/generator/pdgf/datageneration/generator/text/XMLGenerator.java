package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;

import java.util.List;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

public class XMLGenerator extends GenSequential implements Generator {

    public XMLGenerator(int min, int max) {
        this.concatenateResults = true;
        this.generators = List.of(getStaticValueGenerator("<xs:element ref=\""), new RandomStringGenerator(min, max), getStaticValueGenerator("\"/>"));
    }

    private GenStaticValue getStaticValueGenerator(String value) {
        return new GenStaticValue()
                .withValue(value);
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, RDF_XML_LITERAL);
    }

}
