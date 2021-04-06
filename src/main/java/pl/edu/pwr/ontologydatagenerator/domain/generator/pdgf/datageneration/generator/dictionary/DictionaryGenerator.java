package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenDictList;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.net.URI;

public class DictionaryGenerator extends GenDictList implements Generator {

    public DictionaryGenerator(URI dictionaryUrl, Distribution distribution, int numberOfValuesPerRow) {
        this.file = dictionaryUrl.getPath();
        this.separator = "|";
        this.unique = true;
        this.size = String.valueOf(numberOfValuesPerRow);
        this.distribution = distribution;
    }

}
