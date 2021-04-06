package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.id;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenId;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenPrePostfix;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;

public class IdentifierGenerator extends GenPrePostfix implements Generator {

    public IdentifierGenerator(HasIdentifier objectWithIdentifer) {
        this.prefix = objectWithIdentifer.getName();
        this.genId = getDefaltIdGenerator();
    }

    private GenId getDefaltIdGenerator() {
        return new GenId()
                .withMin(1L);
    }

}
