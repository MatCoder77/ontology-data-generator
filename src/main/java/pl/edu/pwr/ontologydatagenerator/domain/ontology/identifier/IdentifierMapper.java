package pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier;

import org.semanticweb.owlapi.model.HasIRI;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.infrastructure.transform.TransformUtils;

import java.util.Collection;
import java.util.function.Supplier;

@Service
public class IdentifierMapper {

    public <C extends Collection<Identifier>> C mapToIdentifiers(Collection<? extends HasIRI> objectsWithIri, Supplier<C> collectionFactory) {
        return TransformUtils.transform(objectsWithIri, this::mapToIdentifier, collectionFactory);
    }

    public Identifier mapToIdentifier(HasIRI objectWithIri) {
        return Identifier.of(objectWithIri.getIRI());
    }

}
