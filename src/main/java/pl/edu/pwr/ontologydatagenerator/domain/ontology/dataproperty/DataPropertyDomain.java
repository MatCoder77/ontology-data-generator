package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty;

import lombok.Getter;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.Set;

@Getter
public class DataPropertyDomain {

    private final Set<Identifier> allConceptsInDomain;
    private final Set<Identifier> directConceptsInDomain;

    private DataPropertyDomain(Set<Identifier> allConceptsInDomain, Set<Identifier> directConceptsInDomain) {
        this.allConceptsInDomain = allConceptsInDomain;
        this.directConceptsInDomain = directConceptsInDomain;
    }

    public static DataPropertyDomain of(Set<Identifier> allConceptsInDomain, Set<Identifier> directConceptsInDomain) {
        return new DataPropertyDomain(allConceptsInDomain, directConceptsInDomain);
    }

}
