package pl.edu.pwr.ontologydatagenerator.domain.generator;

import com.google.common.base.Objects;
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
    private Keywords keywords;
    
    public boolean isDatatypeSupported(OWL2Datatype datatype) {
        return supportedDatatypes.contains(datatype);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dictionary)) return false;
        Dictionary that = (Dictionary) o;
        return Objects.equal(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }

}
