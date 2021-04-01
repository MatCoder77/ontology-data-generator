package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.customized;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnumGenerator extends GenSwitch implements Generator {

    public EnumGenerator(Enum<?> enumeration, Distribution distribution) {
        this(getEnumValues(enumeration), distribution);
    }

    public EnumGenerator(Collection<String> values, Distribution distribution) {
        this.genLongNumber = getLongNumberGenerator(values.size(), distribution);
        this._case = getCases(getInputValueByOutputValue(values));
    }

    private Map<Integer, String> getInputValueByOutputValue(Collection<String> values) {
        ArrayList<String> valueList = new ArrayList<>(values);
        return IntStream.range(0, values.size())
                .boxed()
                .collect(Collectors.toMap(Function.identity(), valueList::get));
    }

    public static EnumGenerator of(Collection<String> values, Distribution distribution) {
        return new EnumGenerator(values, distribution);
    }

    public static EnumGenerator of(Collection<String> values) {
        return new EnumGenerator(values, null);
    }

    public static <E extends Enum<E>> EnumGenerator of(E enumeration, Distribution distribution) {
        return new EnumGenerator(enumeration, distribution);
    }

    public static <E extends Enum<E>> EnumGenerator of(E enumeration) {
        return new EnumGenerator(enumeration, null);
    }

    private GenLongNumber getLongNumberGenerator(long valuesCount, Distribution distribution) {
        return new GenLongNumber()
                .withMin(0)
                .withMax(valuesCount - 1)
                .withDistribution(distribution);
    }

    private List<Case> getCases(Map<Integer, String> inputValueByOuptutValue) {
        return inputValueByOuptutValue.entrySet().stream()
                .map(inputByOutput -> getCase(inputByOutput.getKey(), inputByOutput.getValue()))
                .collect(Collectors.toList());
    }

    private static Case getCase(long inputValue, String outputValue) {
        return new Case()
                .withValue(inputValue)
                .withGenStaticValue(getGenStaticValue(outputValue));
    }

    private static GenStaticValue getGenStaticValue(String value) {
        return new GenStaticValue()
                .withValue(value);
    }

    @SuppressWarnings("rawtypes")
    private static List<String> getEnumValues(Enum<?> enumeration) {
        List<? extends Enum> enumConstants = Arrays.asList(enumeration.getClass().getEnumConstants());
        return mapEnumConstantsToString(enumConstants);
    }

    @SuppressWarnings("rawtypes")
    private static List<String> mapEnumConstantsToString(Collection<? extends Enum> enumConstants) {
        return enumConstants.stream()
                .map(Enum::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Collections.emptySet();
    }

}
