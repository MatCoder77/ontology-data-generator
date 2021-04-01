package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.binary;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenRandomString;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenTemplate;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.List;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

/**
 * Generates randeom sequences of binary data encoded as hexadecimal touples representing octets.
 * It is worth to note that legnth constrints are specified in value length, i.e. number of octets, not the number
 * of charcters in hexadecimal representation.
 */
public class HexadecimalGenerator extends GenTemplate implements Generator {

    public HexadecimalGenerator(long min, long max, Distribution distribution) {
        this.distribution = distribution;
        this.generators = List.of(getRandomStringGenerator(2 * min, 2 * max), getRandomStringGenerator(1, 1));
        this.getValue = """
                <![CDATA[
                                    String val = generator[0].toString();
                                    String ch = generator[1].toString();
                                    if (val.length() % 2 != 0) {
                                        fvdto.setBothValues(ch + val);
                                    } else {
                                        fvdto.setBothValues(val);
                                    }
                                ]]>""";
    }

    private GenRandomString getRandomStringGenerator(long min, long max) {
        return new GenRandomString()
                .withMin(min)
                .withMax(max)
                .withCharacters("0123456789abcdef");
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_HEX_BINARY);
    }

}
