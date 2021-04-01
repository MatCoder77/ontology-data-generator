package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.binary;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenTemplate;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text.RandomSentenceGenerator;

import java.util.List;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

public class Base64Generator extends GenTemplate implements Generator {

    public Base64Generator(long min, long max, Distribution distribution) {
        this.distribution = distribution;
        this.generators = List.of(new RandomSentenceGenerator(min, max, distribution));
        this.getValue = """
                <![CDATA[
                                    String text = generator[0].toString();
                                    String encodedText = java.util.Base64.getEncoder().encodeToString(text.getBytes());
                                    fvdto.setBothValues(encodedText);
                                ]]>""";
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_BASE_64_BINARY);
    }

}
