package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.customized;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;

import java.net.URI;
import java.util.*;

public class EmailAddressGenerator extends GenSequential implements Generator {

    private EmailAddressGenerator(URI domainsDictionary, HasIdentifier fragment, String delimiter, HasIdentifier... fragments) {
        this.concatenateResults = true;
        List<Object> generators = new ArrayList<>();
        generators.add(getGenOtherFieldValue(fragment));
        Arrays.asList(fragments).forEach(f -> {
            generators.add(getStaticValueGenerator(delimiter));
            generators.add(getGenOtherFieldValue(f));
        });
        generators.add(getDictListGenerator(domainsDictionary));
        this.generators = generators;
    }

    private EmailAddressGenerator(URI domainsDictionary, URI fragment, String delimiter, URI... fragments) {
        this.concatenateResults = true;
        List<Object> generators = new ArrayList<>();
        generators.add(getDictListGenerator(fragment));
        Arrays.asList(fragments).forEach(f -> {
            generators.add(getStaticValueGenerator(delimiter));
            generators.add(getDictListGenerator(f));
        });
        generators.add(getDictListGenerator(domainsDictionary));
        this.generators = generators;
    }

    public EmailAddressGenerator of(URI domainsDictionary, HasIdentifier fragment, String delimiter, HasIdentifier... fragments) {
        return new EmailAddressGenerator(domainsDictionary, fragment, delimiter, fragments);
    }

    public EmailAddressGenerator of(URI domainsDictionary, HasIdentifier fragment) {
        return new EmailAddressGenerator(domainsDictionary, fragment, null);
    }

    public EmailAddressGenerator of(URI domainsDictionary, URI fragment, String delimiter, URI... fragments) {
        return new EmailAddressGenerator(domainsDictionary, fragment, delimiter, fragments);
    }

    public EmailAddressGenerator of(URI domainsDictionary, URI fragment) {
        return new EmailAddressGenerator(domainsDictionary, fragment, null);
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

    private GenDictList getDictListGenerator(URI dictionaryUrl) {
        return new GenDictList()
                .withFile(dictionaryUrl.getPath());
    }

}
