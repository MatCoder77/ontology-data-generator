package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.DecimalPlaces;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenDoubleNumber;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

public class DoubleNumberGenerator extends GenDoubleNumber implements Generator {

    public DoubleNumberGenerator(double min, double max, Distribution distribution) {
        this(min, max, -1, distribution);
    }

    public DoubleNumberGenerator(double min, double max, int decimalPlaces, Distribution distribution) {
        this.minD = min;
        this.maxD = max;
        this.decimalPlaces = getDecimalPlaces(decimalPlaces);
        this.distribution = distribution;
    }

    private DecimalPlaces getDecimalPlaces(int decimalPlaces) {
        return new DecimalPlaces()
                .withValue(decimalPlaces)
                .withRoundPlainValue(true)
                .withRoundingMode(RoundingMode.HALF_EVEN.name());
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_DECIMAL, XSD_DOUBLE, XSD_FLOAT, OWL_RATIONAL, OWL_REAL);
    }

    enum RoundingMode {
        UP,
        DOWN,
        CEILING,
        FLOOR,
        HALF_UP,
        HALF_DOWN,
        HALF_EVEN,
        UNNECESSARY
    }

}
