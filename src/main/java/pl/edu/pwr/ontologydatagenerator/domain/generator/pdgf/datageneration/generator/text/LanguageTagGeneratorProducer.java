package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GeneratorProducer;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class LanguageTagGeneratorProducer implements GeneratorProducer {

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_LANGUAGE, RDF_LANG_STRING);
    }

    @Override
    public Generator buildGenerator(GenerationContext generationContext) {
        return new LanguageTagGenerator();
    }

}
