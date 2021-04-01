package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenRandomSentence;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

public class RandomSentenceGenerator extends GenRandomSentence implements Generator {

    public RandomSentenceGenerator(long min, long max, Distribution distribution) {
        this.min = min;
        this.max = max;
        this.distribution = distribution;
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_STRING);
    }

}
