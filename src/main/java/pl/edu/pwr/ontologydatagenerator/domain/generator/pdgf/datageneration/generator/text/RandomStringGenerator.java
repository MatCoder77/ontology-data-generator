package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenRandomString;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

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

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_STRING, XSD_NORMALIZED_STRING, XSD_TOKEN, XSD_NAME, XSD_NCNAME, XSD_NMTOKEN);
    }

}
