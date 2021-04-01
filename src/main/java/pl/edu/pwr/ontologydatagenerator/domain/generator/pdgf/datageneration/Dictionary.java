package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.net.URI;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class Dictionary {

    private URI url;
    private Set<OWL2Datatype> supportedDatatypes;
    private Set<String> keywords;

    public boolean isDatatypeSupported(OWL2Datatype datatype) {
        return supportedDatatypes.contains(datatype);
    }

}
