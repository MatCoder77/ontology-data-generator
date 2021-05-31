package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.customized.name;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Dictionary;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.dictionary.DictionaryService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.ConceptSimilarityService;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.PropertySimilarityService;

import java.util.Optional;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_STRING;

@Service
@RequiredArgsConstructor
public class DictionaryBasedFullNameGeneratorProducer implements DataPropertyGeneratorProducer {

    private static final Set<String> NAME_PROPERTY_KEYWORDS = Set.of("name");
    private static final Set<String> PERSON_CONCEPT_KEYWORDS = Set.of("person");
    private static final String FIRST_NAME_PROPERTY_KEYWORD = "first name";
    private static final String LAST_NAME_PROPERTY_KEYWORD = "last name";

    @Value("${app.T:0.75}") private final Double threeshold;
    private final DictionaryService dictionaryService;
    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;
    private final PropertySimilarityService propertySimilarityService;
    private final ConceptSimilarityService conceptSimilarityService;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_STRING);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        Dictionary firstNameDictionary = dictionaryService.requireBestApllicableDictionary(FIRST_NAME_PROPERTY_KEYWORD, context, getSupportedDataTypes());
        Dictionary lastNameDictionry = dictionaryService.requireBestApllicableDictionary(LAST_NAME_PROPERTY_KEYWORD, context, getSupportedDataTypes());
        Distribution distribution = distributionProvider.getDistribution(context);
        return new FullNameGenerator(firstNameDictionary.getUrl(), lastNameDictionry.getUrl(), distribution);
    }

    @Override
    public double getScore(DataPropertyGenerationContext context) {
        return getPropertySimilarity(context);
    }

    private double getPropertySimilarity(DataPropertyGenerationContext context) {
        return propertySimilarityService.calculatePropertySimilarityForKeywords(context.getDataProperty(), NAME_PROPERTY_KEYWORDS);
    }

    @Override
    public boolean isApplicable(DataPropertyGenerationContext context) {
        return isDataTypeSupported(context.getDatatype()) &&
                getPropertySimilarity(context) >= threeshold &&
                dictionaryService.isDictionaryAvailableForProperty(FIRST_NAME_PROPERTY_KEYWORD, context, getSupportedDataTypes()) &&
                dictionaryService.isDictionaryAvailableForProperty(LAST_NAME_PROPERTY_KEYWORD, context, getSupportedDataTypes()) &&
                isPersonConcept(context) &&
                getFirstNameProperty(context).isEmpty() &&
                getLastNameProperty(context).isEmpty() &&
                isNotRestricted(context);
    }

    private boolean isPersonConcept(DataPropertyGenerationContext context) {
        return conceptSimilarityService.calculateConceptSimilarityForKeywrods(context.getConcept(), PERSON_CONCEPT_KEYWORDS) >= threeshold;
    }

    private Optional<DataProperty> getFirstNameProperty(DataPropertyGenerationContext context) {
        return getProperty(Set.of(FIRST_NAME_PROPERTY_KEYWORD), context);
    }

    private Optional<DataProperty> getLastNameProperty(DataPropertyGenerationContext context) {
        return getProperty(Set.of(LAST_NAME_PROPERTY_KEYWORD), context);
    }

    public Optional<DataProperty> getProperty(Set<String> propertyKeywords, DataPropertyGenerationContext context) {
        return propertySimilarityService.findSimilarProperty(context.getConcept(), propertyKeywords, 1, Set.of(XSD_STRING), context.getContainer());
    }

    public boolean isNotRestricted(DataPropertyGenerationContext context) {
        return context.getRestrictions().isEmpty();
    }

}
