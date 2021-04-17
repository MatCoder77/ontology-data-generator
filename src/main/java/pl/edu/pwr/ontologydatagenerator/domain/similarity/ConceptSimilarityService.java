package pl.edu.pwr.ontologydatagenerator.domain.similarity;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConceptSimilarityService {

    private final SemanticSimilarityService semanticSimilarityService;

    public double calculateConceptSimilarityForKeywrods(Concept concept, Set<String> conceptKeywords) {
        Set<String> conceptNames = Sets.union(getAllConceptAliases(concept), getSuperConcepts(concept));
        return conceptNames.stream()
                .flatMap(conceptName -> conceptKeywords.stream().map(keyword -> semanticSimilarityService.calculateSemanticSimilarity(conceptName, keyword)))
                .max(Comparator.naturalOrder())
                .orElse(0.0);
    }

    private Set<String> getAllConceptAliases(Concept concept) {
        return concept.getEquivalentConcepts().stream()
                .map(Identifier::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> getSuperConcepts(Concept concept) {
        return concept.getSuperConcepts().stream()
                .map(Identifier::getName)
                .collect(Collectors.toSet());
    }

}
