package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Dictionary;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalArgumentAppException;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DictionaryGeneratorProducer implements DataPropertyGeneratorProducer {

    private final DictionaryService dictionaryService;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return dictionaryService.getAllSupportedDatatypes();
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        Dictionary dictionary = dictionaryService.findBestDictionary(context.getDataProperty(), context.getConcept(), getSupportedDataTypes())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentAppException("Dictionary shold be present when method is invoked!"));
        return new DictionaryGenerator(dictionary.getUrl(), null, 1);
    }

    @Override
    public double getScore(DataPropertyGenerationContext context) {
        return dictionaryService.findBestDictionary(context.getDataProperty(), context.getConcept(), getSupportedDataTypes())
                .map(Map.Entry::getValue)
                .orElse(0.0);
    }

    @Override
    public boolean isApplicable(DataPropertyGenerationContext context) {
        return isNotRestricted(context) && getScore(context) >= 0.75;
    }

    public boolean isNotRestricted(DataPropertyGenerationContext context) {
        return context.getRestrictions().isEmpty();
    }

}
