package pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty;

import lombok.Getter;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import java.util.Set;

@Getter
public class ObjectPropertyDomain {

    private final Set<Identifier> allConceptsInDomain;
    private final Set<Identifier> directConceptsInDomain;

    private ObjectPropertyDomain(Set<Identifier> allConceptsInDomain, Set<Identifier> directConceptsInDomain) {
        this.allConceptsInDomain = allConceptsInDomain;
        this.directConceptsInDomain = directConceptsInDomain;
    }

    public static ObjectPropertyDomain of(Set<Identifier> allConceptsInDomain, Set<Identifier> directConceptsInDomain) {
        return new ObjectPropertyDomain(allConceptsInDomain, directConceptsInDomain);
    }
    
}
