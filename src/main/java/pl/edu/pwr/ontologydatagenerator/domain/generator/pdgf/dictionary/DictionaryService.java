package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.dictionary;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.SemanticSimilarityService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Dictionary;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final PDGFDictionaryDataProvider dictionaryDataProvider;
    private final SemanticSimilarityService semanticSimilarityService;

    public Set<OWL2Datatype> getAllSupportedDatatypes() {
        return dictionaryDataProvider.getAllSupportedDatatypes();
    }

    public Optional<Map.Entry<Dictionary, Double>> findBestDictionary(DataProperty dataProperty, Concept concept, Set<OWL2Datatype> supportedDataTypes) {
        return getDictionariesSortedDescendingByScore(dataProperty, concept, dictionaryDataProvider.getDictionariesForDataTypes(supportedDataTypes))
                .entrySet().stream()
                .findFirst();
    }

    @SafeVarargs
    public final Optional<Map.Entry<Dictionary, Double>> findBestDictionary(DataProperty dataProperty, Concept concept, Set<OWL2Datatype> supportedDataTypes, Predicate<Dictionary>... predicates) {
        return getDictionariesSortedDescendingByScore(dataProperty, concept, dictionaryDataProvider.getDictionariesForDataTypes(supportedDataTypes))
                .entrySet().stream()
                .filter(scoreByDictionary -> combineFilters(predicates).test(scoreByDictionary.getKey()))
                .findFirst();
    }

    private Map<Dictionary, Double> getDictionariesSortedDescendingByScore(DataProperty dataProperty, Concept concept, Collection<Dictionary> dictionaries) {
        List<Dictionary> dictionariesForConcept = getDictionariesForConcept(concept, dictionaries);
        return caclulatePropertyScoreForDictionaries(dataProperty, dictionariesForConcept).entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(Map.Entry::getValue)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private List<Dictionary> getDictionariesForConcept(Concept concept, Collection<Dictionary> dictionaries) {
        return calculateConceptScoresForDictionaries(concept, dictionaries).entrySet().stream()
                .filter(dictionaryAndScore -> isSuitableForConcept(dictionaryAndScore.getKey(), dictionaryAndScore.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean isSuitableForConcept(Dictionary dictionary, double score) {
        return dictionary.getKeywords().getConceptKeywords().isEmpty() || score >= 0.75;
    }

    private Map<Dictionary, Double> calculateConceptScoresForDictionaries(Concept concept, Collection<Dictionary> dictionaries) {
        return dictionaries.stream()
                .collect(Collectors.toMap(Function.identity(), dictionary -> calculateConceptScoreForDictionary(concept, dictionary)));
    }

    private double calculateConceptScoreForDictionary(Concept concept, Dictionary dictionary) {
        Set<String> conceptNames = Sets.union(getAllConceptAliases(concept), getSuperConcepts(concept));
        return semanticSimilarityService.calculateSemanticSimilarity(conceptNames, dictionary.getKeywords().getConceptKeywords());
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

    private Map<Dictionary, Double> caclulatePropertyScoreForDictionaries(DataProperty dataProperty, Collection<Dictionary> dictionaries) {
        return dictionaries.stream()
                .collect(Collectors.toMap(Function.identity(), dictionary -> calculatePropertyScoreForDictionary(dataProperty, dictionary)));
    }

    private double calculatePropertyScoreForDictionary(DataProperty dataProperty, Dictionary dictionary) {
        Set<String> propertyNames = Sets.union(getAllPropertyAliases(dataProperty), getSuperProperties(dataProperty));
        return semanticSimilarityService.calculateSemanticSimilarity(propertyNames, dictionary.getKeywords().getPropertyKeywords());
    }

    private Set<String> getAllPropertyAliases(DataProperty dataProperty) {
        return dataProperty.getEquivalentProperties().stream()
                .map(Identifier::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> getSuperProperties(DataProperty dataProperty) {
        return dataProperty.getSuperProperties().stream()
                .map(Identifier::getName)
                .collect(Collectors.toSet());
    }

    @SafeVarargs
    private static <T> Predicate<T> combineFilters(Predicate<T>... predicates) {
        return Stream.of(predicates).reduce(x -> true, Predicate::and);
    }

}
