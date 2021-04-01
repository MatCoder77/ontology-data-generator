package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.id;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;

import java.util.Collections;
import java.util.Set;

public class ReferenceGenerator implements Generator {

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Collections.emptySet();
    }

}
