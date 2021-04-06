package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.text;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.constraints.StringConstraintsProvider;

import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDF_XML_LITERAL;

@Service
@RequiredArgsConstructor
public class XMLGeneratorProducer implements GeneratorProducer {

    private final StringConstraintsProvider constraintsProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, RDF_XML_LITERAL);
    }

    @Override
    public Generator buildGenerator(GenerationContext generationContext) {
        long min = constraintsProvider.getMinLength(generationContext);
        long max = constraintsProvider.getMaxLength(generationContext);
        return new XMLGenerator(min, max);
    }

}
