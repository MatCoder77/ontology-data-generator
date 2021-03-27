package pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty;

import lombok.Getter;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import java.util.Set;

@Getter
public class ObjectPropertyRange {

    private final Set<Identifier> allConceptsInRange;
    private final Set<Identifier> directConceptsInRange;

    private ObjectPropertyRange(Set<Identifier> allConceptsInRange, Set<Identifier> directConceptsInRange) {
        this.allConceptsInRange = allConceptsInRange;
        this.directConceptsInRange = directConceptsInRange;
    }

    public static ObjectPropertyRange of(Set<Identifier> allConceptsInRange, Set<Identifier> directConceptsInRange) {
        return new ObjectPropertyRange(allConceptsInRange, directConceptsInRange);
    }
    
}
