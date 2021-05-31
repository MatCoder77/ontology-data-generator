package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.dictionary;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DictionaryDataProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.ConceptSimilarityService;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.PropertySimilarityService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Dictionary;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.transform.TransformUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    @Value("${app.T:0.75}") private final Double threeshold;
    private final DictionaryDataProvider dictionaryDataProvider;
    private final ConceptSimilarityService conceptSimilarityService;
    private final PropertySimilarityService propertySimilarityService;

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
                .filter(scoreByDictionary -> TransformUtils.combineFilters(predicates).test(scoreByDictionary.getKey()))
                .findFirst();
    }

    private Map<Dictionary, Double> getDictionariesSortedDescendingByScore(DataProperty dataProperty, Concept concept, Collection<Dictionary> dictionaries) {
        Map<Dictionary, Double> dictionariesForConcept = getDictionariesForConcept(concept, dictionaries);
        return caclulatePropertyScoreForDictionaries(dataProperty, dictionariesForConcept).entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble((Map.Entry<Dictionary, Pair<Double, Double>> dictionaryAndScores) -> dictionaryAndScores.getValue().getLeft()).thenComparing(dictionaryAndScores -> dictionaryAndScores.getValue().getRight())))
                .collect(Collectors.toMap(Map.Entry::getKey, dictionaryAndScores -> dictionaryAndScores.getValue().getLeft(), (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<Dictionary, Double> getDictionariesForConcept(Concept concept, Collection<Dictionary> dictionaries) {
        return calculateConceptScoresForDictionaries(concept, dictionaries).entrySet().stream()
                .filter(dictionaryAndScore -> isSuitableForConcept(dictionaryAndScore.getKey(), dictionaryAndScore.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isSuitableForConcept(Dictionary dictionary, double score) {
        return dictionary.getKeywords().getConceptKeywords().isEmpty() || score >= threeshold;
    }

    private Map<Dictionary, Double> calculateConceptScoresForDictionaries(Concept concept, Collection<Dictionary> dictionaries) {
        return dictionaries.stream()
                .collect(Collectors.toMap(Function.identity(), dictionary -> calculateConceptScoreForDictionary(concept, dictionary)));
    }

    private double calculateConceptScoreForDictionary(Concept concept, Dictionary dictionary) {
        return conceptSimilarityService.calculateConceptSimilarityForKeywrods(concept, dictionary.getKeywords().getConceptKeywords());
    }

    private Map<Dictionary, Pair<Double, Double>> caclulatePropertyScoreForDictionaries(DataProperty dataProperty, Map<Dictionary, Double> dictionaries) {
        return dictionaries.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, dictionaryAndScore -> Pair.of(calculatePropertyScoreForDictionary(dataProperty, dictionaryAndScore.getKey()), dictionaryAndScore.getValue())));
    }

    private double calculatePropertyScoreForDictionary(DataProperty dataProperty, Dictionary dictionary) {
        return propertySimilarityService.calculatePropertySimilarityForKeywords(dataProperty, dictionary.getKeywords().getPropertyKeywords());
    }

    public boolean isDictionaryAvailableForProperty(String propertyName, DataPropertyGenerationContext context, Set<OWL2Datatype> supportedDatatypes) {
        return findBestApllicableDictionary(propertyName, context, supportedDatatypes)
                .map(scoreByDictionary -> scoreByDictionary.getValue() >= threeshold)
                .orElse(false);
    }

    public Dictionary requireBestApllicableDictionary(String propertyName, DataPropertyGenerationContext context, Set<OWL2Datatype> supportedDatatypes) {
        return findBestApllicableDictionary(propertyName, context, supportedDatatypes)
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

    public Optional<Map.Entry<Dictionary, Double>> findBestApllicableDictionary(String propertyName, DataPropertyGenerationContext context, Set<OWL2Datatype> supportedDatatypes) {
        return findBestDictionary(propertyName, context.getConcept(), supportedDatatypes)
                .filter(scoreByDictionary -> scoreByDictionary.getValue() >= threeshold);
    }

    @SafeVarargs
    private Optional<Map.Entry<Dictionary, Double>> findBestDictionary(String dataProperty, Concept concept, Set<OWL2Datatype> supportedDataTypes, Predicate<Dictionary>... predicates) {
        return getDictionariesSortedDescendingByScore(dataProperty, concept, dictionaryDataProvider.getDictionariesForDataTypes(supportedDataTypes))
                .entrySet().stream()
                .filter(scoreByDictionary -> TransformUtils.combineFilters(predicates).test(scoreByDictionary.getKey()))
                .findFirst();
    }

    private Map<Dictionary, Double> getDictionariesSortedDescendingByScore(String dataProperty, Concept concept, Collection<Dictionary> dictionaries) {
        Map<Dictionary, Double> dictionariesForConcept = getDictionariesForConcept(concept, dictionaries);
        return caclulatePropertyScoreForDictionaries(dataProperty, dictionariesForConcept).entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble((Map.Entry<Dictionary, Pair<Double, Double>> dictionaryAndScores) -> dictionaryAndScores.getValue().getLeft()).thenComparing(dictionaryAndScores -> dictionaryAndScores.getValue().getRight())))
                .collect(Collectors.toMap(Map.Entry::getKey, dictionaryAndScores -> dictionaryAndScores.getValue().getLeft(), (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<Dictionary, Pair<Double, Double>> caclulatePropertyScoreForDictionaries(String dataProperty, Map<Dictionary, Double> dictionaries) {
        return dictionaries.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, dictionaryAndScore -> Pair.of(calculatePropertyScoreForDictionary(dataProperty, dictionaryAndScore.getKey()), dictionaryAndScore.getValue())));
    }

    private double calculatePropertyScoreForDictionary(String dataProperty, Dictionary dictionary) {
        return propertySimilarityService.calculatePropertySimilarityForPropertyName(dataProperty, dictionary.getKeywords().getPropertyKeywords());
    }

}
