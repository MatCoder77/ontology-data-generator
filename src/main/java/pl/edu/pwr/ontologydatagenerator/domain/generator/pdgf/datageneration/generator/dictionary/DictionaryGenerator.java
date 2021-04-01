package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenDictList;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.net.URI;
import java.util.Set;

public class DictionaryGenerator extends GenDictList implements Generator {

    public DictionaryGenerator(URI dictionaryUrl, Distribution distribution, int numberOfValuesPerRow) {
        this.file = dictionaryUrl.getPath();
        this.separator = "|";
        this.unique = true;
        this.size = numberOfValuesPerRow;
        this.distribution = distribution;
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(OWL2Datatype.values());
    }

}
