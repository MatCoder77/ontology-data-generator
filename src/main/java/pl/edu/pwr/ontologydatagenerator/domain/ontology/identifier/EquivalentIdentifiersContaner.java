package pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EquivalentIdentifiersContaner {

    private final Set<Set<Identifier>> equivalentIdentifiersSets;

    public Set<Identifier> getIdentifiers() {
        return equivalentIdentifiersSets.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<Identifier> getEqivalentElements(Identifier identifier) {
        return equivalentIdentifiersSets.stream()
                .filter(equivalentSet -> equivalentSet.contains(identifier))
                .findAny()
                .orElseGet(Collections::emptySet);
    }

}
