package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.dictionary;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Dictionary;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGenerationContext;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.DataPropertyGeneratorProducer;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.dictionary.DictionaryService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.StringConstraints;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints.ValueRangeConstraints;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalArgumentAppException;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
public class DictionaryGeneratorProducer implements DataPropertyGeneratorProducer {

    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss[XXX]")
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter();
    private static final Function<String, Temporal> PARSER = string -> formatter.parse(string, OffsetDateTime::from);

    private final DictionaryService dictionaryService;

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return dictionaryService.getAllSupportedDatatypes();
    }

    @Override
    public Generator buildGenerator(DataPropertyGenerationContext context) {
        Dictionary dictionary = dictionaryService.findBestDictionary(context.getDataProperty(), context.getConcept(), getSupportedDataTypes())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentAppException("Dictionary shold be present when method is invoked!"));
        return new DictionaryGenerator(dictionary.getUrl(), null, 1);
    }

    @Override
    public double getScore(DataPropertyGenerationContext context) {
        return findBestApllicableDictionary(context)
                .map(Map.Entry::getValue)
                .orElse(0.0);
    }

    private Optional<Map.Entry<Dictionary, Double>> findBestApllicableDictionary(DataPropertyGenerationContext context) {
        Predicate<Dictionary> fulfillsRestrictionsPredicate = dictionary -> restrictionsAreFulfilledByDictionary(dictionary, context);
        return dictionaryService.findBestDictionary(context.getDataProperty(), context.getConcept(), getSupportedDataTypes(), fulfillsRestrictionsPredicate)
                .filter(scoreByDictionary -> scoreByDictionary.getValue() >= 0.75);
    }

    @Override
    public boolean isApplicable(DataPropertyGenerationContext context) {
        return findBestApllicableDictionary(context)
                .isPresent();
    }

    private boolean restrictionsAreFulfilledByDictionary(Dictionary dictionary, DataPropertyGenerationContext context) {
        return switch (context.getDatatype().getCategory()) {
            case CAT_NUMBER -> fulfillsNumericConstraints(dictionary, context);
            case CAT_BINARY, CAT_STRING_WITHOUT_LANGUAGE_TAG, CAT_STRING_WITH_LANGUAGE_TAG, CAT_URI -> fulfillsLengthConstraints(dictionary, context);
            case CAT_TIME -> fulfillsTimeConstraints(dictionary, context);
            default -> true;
        };
    }

    private boolean fulfillsNumericConstraints(Dictionary dictionary, DataPropertyGenerationContext context) {
        if (Set.of(XSD_DECIMAL, XSD_DOUBLE, XSD_FLOAT, OWL_RATIONAL, OWL_REAL).contains(context.getDatatype())) {
            return fulfillsRealNumericConstraints(dictionary, context);
        }
        return fulfillsDiscreteNumericConstraints(dictionary, context);
    }

    private boolean fulfillsRealNumericConstraints(Dictionary dictionary, DataPropertyGenerationContext context) {
        ValueRangeConstraints<Double> constraints = ValueRangeConstraints.of(context.getRestrictions(), Double::parseDouble);
        return dictionary.fulfillsConstraints(constraints, Double::parseDouble, Double::compare, x -> x + Double.MIN_VALUE, x -> x + Double.MIN_VALUE);
    }

    private boolean fulfillsDiscreteNumericConstraints(Dictionary dictionary, DataPropertyGenerationContext context) {
        ValueRangeConstraints<Long> constraints = ValueRangeConstraints.of(context.getRestrictions(), Long::parseLong);
        return dictionary.fulfillsConstraints(constraints, Long::parseLong, Long::compare, x -> x - 1, x -> x + 1);
    }

    private boolean fulfillsLengthConstraints(Dictionary dictionary, DataPropertyGenerationContext context) {
        StringConstraints constraints = StringConstraints.of(context.getRestrictions());
        return dictionary.fulfillsConstraints(constraints);
    }

    private boolean fulfillsTimeConstraints(Dictionary dictionary, DataPropertyGenerationContext context) {
        ValueRangeConstraints<Temporal> constraints = ValueRangeConstraints.of(context.getRestrictions(), PARSER);
        UnaryOperator<Temporal> identity = temporal -> temporal;
        return dictionary.fulfillsConstraints(constraints, PARSER, this::compareTemporals, identity, identity);
    }

    @SuppressWarnings("unchecked")
    private <T extends Temporal> int compareTemporals(T t1, T t2) {
        Comparable<T> c1 = (Comparable<T>) t1;
        return c1.compareTo(t2);
    }

}
