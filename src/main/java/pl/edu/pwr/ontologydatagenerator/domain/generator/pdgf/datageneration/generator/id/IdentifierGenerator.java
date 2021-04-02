package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.id;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenId;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenPrePostfix;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;

import java.util.Collections;
import java.util.Set;

public class IdentifierGenerator extends GenPrePostfix implements Generator {

    public IdentifierGenerator(HasIdentifier objectWithIdentifer) {
        this.prefix = objectWithIdentifer.getName();
        this.genId = getDefaltIdGenerator();
    }

    private GenId getDefaltIdGenerator() {
        return new GenId()
                .withMin(1L);
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Collections.emptySet();
    }

}
