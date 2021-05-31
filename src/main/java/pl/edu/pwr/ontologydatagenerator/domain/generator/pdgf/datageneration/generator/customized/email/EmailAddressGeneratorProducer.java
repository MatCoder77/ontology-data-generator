package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.customized.email;

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
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.PropertySimilarityService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_STRING;

@Service
@RequiredArgsConstructor
public class EmailAddressGeneratorProducer implements DataPropertyGeneratorProducer {

    private static final Set<String> EMAIL_PROPERTY_KEYWORDS = Set.of("email", "email address");
    private static final String EMAIL_PROVIDER_PROPERTY_KEYWORD = "email provider";
    private static final String FIRST_NAME_PROPERTY_KEYWORD = "first name";
    private static final String LAST_NAME_PROPERTY_KEYWORD = "last name";
    private static final Set<String> NAME_PROPERTY_KEYWORDS = Set.of("name");

    @Value("${app.T:0.75}") private final Double threeshold;
    private final PropertySimilarityService propertySimilarityService;
    private final DictionaryService dictionaryService;
    private final DistributionProvider<DataPropertyGenerationContext, Distribution> distributionProvider;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_STRING);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        Dictionary emailProviderDictionary = dictionaryService.requireBestApllicableDictionary(EMAIL_PROVIDER_PROPERTY_KEYWORD, context, Set.of(XSD_STRING));
        Distribution distribution = distributionProvider.getDistribution(context);
        List<HasIdentifier> emailFragments = getEmailFragments(context);
        if (emailFragments.isEmpty()) {
            List<URI> emailFragmentUris = getEmailFragmentUris(context);
            return new EmailAddressGenerator(emailProviderDictionary.getUrl(), ".", emailFragmentUris, distribution);
        }
        return new EmailAddressGenerator(emailProviderDictionary.getUrl(), ".", distribution, emailFragments);
    }

    private List<HasIdentifier> getEmailFragments(DataPropertyGenerationContext context) {
        List<HasIdentifier> firstLastNameFragments = getFirstLastNameFragments(context);
        if (firstLastNameFragments.isEmpty()) {
            return getNameFragment(context);
        }
        return firstLastNameFragments;
    }

    private List<HasIdentifier> getFirstLastNameFragments(DataPropertyGenerationContext context) {
        return Stream.of(getFirstNameProperty(context), getLastNameProperty(context))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private List<HasIdentifier> getNameFragment(DataPropertyGenerationContext context) {
        return Stream.of(getNameProperty(context))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private List<URI> getEmailFragmentUris(DataPropertyGenerationContext context) {
        return Stream.of(dictionaryService.findBestApllicableDictionary(FIRST_NAME_PROPERTY_KEYWORD, context, Set.of(XSD_STRING)),
                dictionaryService.findBestApllicableDictionary(LAST_NAME_PROPERTY_KEYWORD, context, Set.of(XSD_STRING)))
                .flatMap(Optional::stream)
                .map(Map.Entry::getKey)
                .map(Dictionary::getUrl)
                .collect(Collectors.toList());
    }

    @Override
    public double getScore(DataPropertyGenerationContext context) {
        return getPropertySimilarity(context);
    }

    private double getPropertySimilarity(DataPropertyGenerationContext context) {
        return propertySimilarityService.calculatePropertySimilarityForKeywords(context.getDataProperty(), EMAIL_PROPERTY_KEYWORDS);
    }

    @Override
    public boolean isApplicable(DataPropertyGenerationContext context) {
        return isDataTypeSupported(context.getDatatype()) &&
                areRestrictionsFulfilled(context) &&
                getScore(context) >= threeshold &&
                (areEmailFragmentFieldsPresents(context) || areDictionariesPresent(context)) &&
                dictionaryService.isDictionaryAvailableForProperty(EMAIL_PROVIDER_PROPERTY_KEYWORD, context, getSupportedDataTypes());
    }

    private boolean areEmailFragmentFieldsPresents(DataPropertyGenerationContext context) {
        return getLastNameProperty(context).isPresent() ||
                getNameProperty(context).isPresent();
    }

    private boolean areDictionariesPresent(DataPropertyGenerationContext context) {
        return dictionaryService.isDictionaryAvailableForProperty(LAST_NAME_PROPERTY_KEYWORD, context, Set.of(XSD_STRING));
    }

    private Optional<DataProperty> getFirstNameProperty(DataPropertyGenerationContext context) {
        return getProperty(Set.of(FIRST_NAME_PROPERTY_KEYWORD), context);
    }

    private Optional<DataProperty> getLastNameProperty(DataPropertyGenerationContext context) {
        return getProperty(Set.of(LAST_NAME_PROPERTY_KEYWORD), context);
    }

    private Optional<DataProperty> getNameProperty(DataPropertyGenerationContext context) {
        return getProperty(NAME_PROPERTY_KEYWORDS, context);
    }

    public Optional<DataProperty> getProperty(Set<String> propertyKeywords, DataPropertyGenerationContext context) {
        return propertySimilarityService.findSimilarProperty(context.getConcept(), propertyKeywords, 1, Set.of(RDFS_LITERAL, XSD_STRING), context.getContainer());
    }

    public boolean areRestrictionsFulfilled(DataPropertyGenerationContext context) {
        return context.getRestrictions().isEmpty();
    }

}
