package pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier;

import lombok.Getter;
import org.semanticweb.owlapi.model.IRI;

import java.util.Objects;

@Getter
public class Identifier {

    private final IRI iri;

    private Identifier(IRI iri) {
        this.iri = iri;
    }

    public static Identifier of(IRI iri) {
        return new Identifier(iri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        Identifier that = (Identifier) o;
        return iri.equals(that.iri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iri);
    }

}
