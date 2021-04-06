package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.id;

import lombok.Getter;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.numeric.LongNumberGenerator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReferenceGenerator extends GenTemplate implements Generator {

    public ReferenceGenerator(Collection<Concept> range, ChooseType chooseType, From from, Distribution tableSelectionDistribution, Distribution rowSelectionDistribution ) {
        this.distribution = tableSelectionDistribution;
        List<Object> generators = new ArrayList<>();
        generators.add(getLongNumberGenerator(range, distribution));
        generators.addAll(getReferenceValuesGenerators(range, chooseType, from, distribution));
        this.generators = generators;
        this.getValue = """
                <![CDATA[
                                    int number = generator[0].getIntValue();
                                    String reference = generator[number].toString();
                                    fvdto.setBothValues(reference);
                                ]]>""";
    }

    private LongNumberGenerator getLongNumberGenerator(Collection<Concept> range, Distribution distribution) {
        return new LongNumberGenerator(1, range.size(), distribution);
    }

    private List<GenReferenceValue> getReferenceValuesGenerators(Collection<Concept> range, ChooseType chooseType, From from, Distribution distribution) {
        return range.stream()
                .map(concept -> getReferenceValueGenerator(concept, chooseType, from, distribution))
                .collect(Collectors.toList());
    }

    private GenReferenceValue getReferenceValueGenerator(Concept range, ChooseType chooseType, From from, Distribution distribution){
        return new GenReferenceValue()
                .withChoose(chooseType.getValue())
                .withFrom(from.getValue())
                .withReference(getIdentifierFieldReference(range))
                .withDistribution(distribution);
    }

    private SameChoiceAs getSameChoiseAs(HasIdentifier objectWithIdentifier, String generatorId) {
        return new SameChoiceAs()
                .withField(objectWithIdentifier.getName())
                .withGeneratorByID(generatorId);
    }

    private Reference getIdentifierFieldReference(Concept concept) {
        return new Reference()
                .withField("__identifier__")
                .withTable(concept.getName());
    }

    @Getter
    enum ChooseType {

        RANDOM_SHUFFLE("randomShuffle"),
        RANDOM("random"),
        PERMUTATION_RANDOM("permutationRandom"),
        RELATIVE_ROW_MAPPING("relativeRowMapping"),
        RELATIVE_UNIQUE_WITHOUT_DELETED_IN_NEXT_UPDATE("relativeUnique_withoutDeletedInNextUpdate"),
        DIRECT_ROW_MAPPING("directRowMapping"),
        SAME_CHOICE_AS("sameChoiceAs"),
        GLOBAL_UNIQUE("globalUnique"),
        INTRA_TUPEL("intraTupel"),
        ID_FROM_SUBGENERATOR("idFromSubGenerator"),
        REALTIVE_UNQIUE("relativeUnique");

        private final String value;

        ChooseType(String value) {
            this.value = value;
        }

    }

    @Getter
    enum From {

        FIXED_TIME_FRAME("fixedTimeFrame"),
        AS_INSERT("atInsert"),
        HISTORICAL("historical"),
        SAME_TIME_FRAME("sameTimeFrame"),
        RELATIVE_TIME_FRAME("relativeTimeFrame");

        private final String value;

        From(String value) {
            this.value = value;
        }

    }

}
