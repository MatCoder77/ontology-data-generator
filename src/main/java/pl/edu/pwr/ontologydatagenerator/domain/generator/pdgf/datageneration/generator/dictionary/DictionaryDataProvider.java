package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary;

import lombok.Getter;
import lombok.Setter;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Dictionary;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.generator.pdgf.datageneration")
public class DictionaryDataProvider {

    private List<Dictionary> dictionaries;

    public List<Dictionary> getDictionariesForDatatype(OWL2Datatype datatype) {
        return dictionaries.stream()
                .filter(dictionary -> dictionary.isDatatypeSupported(datatype))
                .collect(Collectors.toList());
    }

}
