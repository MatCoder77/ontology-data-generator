package pl.edu.pwr.ontologydatagenerator.domain.similarity;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.GenerationContextProvider;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertySimilarityService {

    private final SemanticSimilarityService semanticSimilarityService;
    private final GenerationContextProvider generationContextProvider;

    public Optional<DataProperty> findSimilarProperty(Concept concept, Set<String> propertyKeywords, double minSimilarity, Set<OWL2Datatype> supportedDatatypes, OntologyContainer<OWLOntology> container) {
        return findSimilarPropertyBasedOnKeyords(concept, propertyKeywords, supportedDatatypes, container).entrySet().stream()
                .filter(scoreByProperty -> scoreByProperty.getValue() >= minSimilarity)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public Map<DataProperty, Double> findSimilarPropertyBasedOnKeyords(Concept concept, Set<String> propertyKeywords, Set<OWL2Datatype> supportedDatatypes, OntologyContainer<OWLOntology> container) {
        return getScoredDataPropertiesSortedDescendingBySimilarity(concept, propertyKeywords, supportedDatatypes, container).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<DataProperty, Double> getScoredDataPropertiesSortedDescendingBySimilarity(Concept concept, Set<String> propertyKeywords, Set<OWL2Datatype> supportedDatatypes, OntologyContainer<OWLOntology> container) {
        return getSimilaritForConceptDataProperties(concept, propertyKeywords, supportedDatatypes, container).entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingDouble(Map.Entry::getValue)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<DataProperty, Double> getSimilaritForConceptDataProperties(Concept concept, Set<String> propertyKeywords, Set<OWL2Datatype> supportedDatatypes, OntologyContainer<OWLOntology> container) {
        return concept.getDataProperties().values().stream()
                .filter(dataProperty -> supportedDatatypes.contains(generationContextProvider.getGenerationContext(dataProperty, concept, container).getDatatype()))
                .collect(Collectors.toMap(Function.identity(), property -> calculatePropertySimilarityForKeywords(property, propertyKeywords)));
    }

    public double calculatePropertySimilarityForKeywords(DataProperty dataProperty, Set<String> propertyKeywords) {
        Set<String> propertyNames = Sets.union(getAllPropertyAliases(dataProperty), getSuperProperties(dataProperty));
        return propertyNames.stream()
                .flatMap(propertyName -> propertyKeywords.stream().map(keyword -> semanticSimilarityService.calculateSemanticSimilarity(propertyName, keyword)))
                .max(Comparator.naturalOrder())
                .orElse(0.0);
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

    public double calculatePropertySimilarityForPropertyName(String dataProperty, Set<String> propertyKeywords) {
        return propertyKeywords.stream()
                .map(keyword -> semanticSimilarityService.calculateSemanticSimilarity(dataProperty, keyword))
                .max(Comparator.naturalOrder())
                .orElse(0.0);
    }

}
