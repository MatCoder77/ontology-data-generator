package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.customized.email;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class EmailAddressGenerator extends GenSequential implements Generator {

    private static final String GENERATOR_CODE = """
                <![CDATA[
                                    String formattedFragment = generator[0].toString().toLowerCase().trim().replaceAll(" ", ".");
                                    fvdto.setBothValues(formattedFragment);
                                ]]>""";

    public EmailAddressGenerator(URI domainsDictionary, String delimiter, Distribution distribution, List<HasIdentifier> fragments) {
        this.concatenateResults = true;
        List<Object> generators = new ArrayList<>();
        generators.add(getFragmentGenerator(requireFirstFragment(fragments)));
        getRemainingFragments(fragments).forEach(f -> {
            generators.add(getStaticValueGenerator(delimiter));
            generators.add(getFragmentGenerator(f));
        });
        generators.add(getStaticValueGenerator("@"));
        generators.add(getDictListGenerator(domainsDictionary, distribution));
        this.generators = generators;
    }

    private <T> T requireFirstFragment(List<T> fragments) {
        return fragments.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateAppException("At least one fragments have to be suppliet to EmailAddressGenerator"));
    }

    private <T> List<T> getRemainingFragments(List<T> fragments) {
        return fragments.stream()
                .skip(1)
                .collect(Collectors.toList());
    }

    public EmailAddressGenerator(URI domainsDictionary, String delimiter, List<URI> fragments, Distribution distribution) {
        this.concatenateResults = true;
        List<Object> generators = new ArrayList<>();
        generators.add(getFragmentGenerator(requireFirstFragment(fragments), distribution));
        getRemainingFragments(fragments).forEach(f -> {
            generators.add(getStaticValueGenerator(delimiter));
            generators.add(getFragmentGenerator(f, distribution));
        });
        generators.add(getStaticValueGenerator("@"));
        generators.add(getDictListGenerator(domainsDictionary, distribution));
        this.generators = generators;
    }

    private GenTemplate getFragmentGenerator(HasIdentifier fragment) {
        return new GenTemplate()
                .withGenerators(getGenOtherFieldValue(fragment))
                .withGetValue(GENERATOR_CODE);
    }

    private GenOtherFieldValue getGenOtherFieldValue(HasIdentifier fragment) {
        return new GenOtherFieldValue()
                .withReference(getReference(fragment));
    }

    private Reference getReference(HasIdentifier framgent) {
        return new Reference()
                .withField(framgent.getName());
    }

    private GenStaticValue getStaticValueGenerator(String staticValue) {
        return new GenStaticValue()
                .withValue(staticValue);
    }

    private GenTemplate getFragmentGenerator(URI fragment, Distribution distribution) {
        return new GenTemplate()
                .withGenerators(getDictListGenerator(fragment, distribution))
                .withGetValue(GENERATOR_CODE);
    }

    private GenDictList getDictListGenerator(URI dictionaryUrl, Distribution distribution) {
        return new GenDictList()
                .withDistribution(distribution)
                .withFile(dictionaryUrl.getPath());
    }

}
