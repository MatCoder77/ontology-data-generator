package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.customized.name;

import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;

import java.net.URI;
import java.util.List;

public class FullNameGenerator extends GenSequential implements Generator {

    public FullNameGenerator(HasIdentifier firstName, HasIdentifier lastName) {
        this.concatenateResults = true;
        this.generators = List.of(
                getOtherFieldValueGenerator(firstName),
                getStaticValueGenerator(" "),
                getOtherFieldValueGenerator(lastName));
    }

    public FullNameGenerator(URI firstNameDictionaryUrl, URI lastNameDictionaryUrl, Distribution distribution) {
        this.concatenateResults = true;
        this.generators = List.of(
                getDicrionaryGenerator(firstNameDictionaryUrl, distribution),
                getStaticValueGenerator(" "),
                getDicrionaryGenerator(lastNameDictionaryUrl, distribution));
    }

    private GenOtherFieldValue getOtherFieldValueGenerator(HasIdentifier property) {
        return new GenOtherFieldValue()
                .withReference(new Reference()
                        .withField(property.getName()));
    }

    private GenStaticValue getStaticValueGenerator(String value) {
        return new GenStaticValue()
                .withValue(value);
    }

    private GenDictList getDicrionaryGenerator(URI dictionaryUrl, Distribution distribution) {
        return new GenDictList()
                .withFile(dictionaryUrl.getPath())
                .withDistribution(distribution)
                .withUnique(true);
    }

}
