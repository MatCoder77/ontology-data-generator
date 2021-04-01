package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.logical;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

public class BooleanGenerator extends GenLongNumber implements Generator {

    public BooleanGenerator(Distribution distribution) {
        this.min = 0;
        this.max = 1;
        this.distribution = distribution;
    }

    public BooleanGenerator() {
        this(null);
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_BOOLEAN);
    }

}
