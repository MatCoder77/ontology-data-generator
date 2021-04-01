package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenLongNumber;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

public class LongNumberGenerator extends GenLongNumber implements Generator {

    public LongNumberGenerator(long min, long max, Distribution distribution) {
        this.min = min;
        this.max = max;
        this.distribution = distribution;
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_INTEGER, XSD_NON_NEGATIVE_INTEGER, XSD_NON_POSITIVE_INTEGER, XSD_POSITIVE_INTEGER,
                XSD_NEGATIVE_INTEGER, XSD_LONG, XSD_INT, XSD_SHORT, XSD_BYTE, XSD_UNSIGNED_LONG, XSD_UNSIGNED_INT,
                XSD_UNSIGNED_SHORT, XSD_UNSIGNED_BYTE);
    }

}
