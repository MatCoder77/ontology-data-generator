package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.dictionary;

import lombok.Getter;
import lombok.Setter;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Dictionary;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DictionaryDataProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Keywords;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.generator.pdgf.datageneration")
public class PDGFDictionaryDataProvider implements DictionaryDataProvider {

    private List<Dictionary> dictionaries;

    public List<Dictionary> getDictionariesForDatatype(OWL2Datatype datatype) {
        return dictionaries.stream()
                .filter(dictionary -> dictionary.isDatatypeSupported(datatype))
                .collect(Collectors.toList());
    }

    public Set<OWL2Datatype> getAllSupportedDatatypes() {
        return dictionaries.stream()
                .map(Dictionary::getSupportedDatatypes)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public List<Keywords> getAllSupportedKeywords() {
        return dictionaries.stream()
                .map(Dictionary::getKeywords)
                .collect(Collectors.toList());
    }

    public Set<Dictionary> getDictionariesForDataTypes(Set<OWL2Datatype> datatypes) {
        return datatypes.stream()
                .map(this::getDictionariesForDatatype)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
