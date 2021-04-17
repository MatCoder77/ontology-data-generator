package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.customized.name;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.ConceptSimilarityService;
import pl.edu.pwr.ontologydatagenerator.domain.similarity.PropertySimilarityService;

import java.util.Optional;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class OtherFieldBasedFullNameGeneratorProducer implements DataPropertyGeneratorProducer {

    private static final Set<String> NAME_PROPERTY_KEYWORDS = Set.of("name");
    private static final Set<String> PERSON_CONCEPT_KEYWORDS = Set.of("person");
    private static final String FIRST_NAME_PROPERTY_KEYWORD = "first name";
    private static final String LAST_NAME_PROPERTY_KEYWORD = "last name";

    private final PropertySimilarityService propertySimilarityService;
    private final ConceptSimilarityService conceptSimilarityService;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_STRING);
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        return new FullNameGenerator(requireFirstNameProperty(context), requireLastNameProperty(context));
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
                isPersonConcept(context) &&
                getFirstNameProperty(context).isPresent() &&
                getLastNameProperty(context).isPresent() &&
                isNotRestricted(context);
    }

    private boolean isPersonConcept(DataPropertyGenerationContext context) {
        return conceptSimilarityService.calculateConceptSimilarityForKeywrods(context.getConcept(), PERSON_CONCEPT_KEYWORDS) >= 0.75;
    }

    private DataProperty requireFirstNameProperty(DataPropertyGenerationContext context) {
        return getFirstNameProperty(context).orElseThrow();
    }

    private Optional<DataProperty> getFirstNameProperty(DataPropertyGenerationContext context) {
        return getProperty(Set.of(FIRST_NAME_PROPERTY_KEYWORD), context);
    }

    private DataProperty requireLastNameProperty(DataPropertyGenerationContext context) {
        return getLastNameProperty(context).orElseThrow();
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
