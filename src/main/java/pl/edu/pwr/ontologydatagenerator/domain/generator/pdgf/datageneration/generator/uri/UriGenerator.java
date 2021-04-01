package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.uri;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.*;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_ANY_URI;

public class UriGenerator extends GenSequential implements Generator {

    public UriGenerator(String scheme, URI hostsDictionary, HasIdentifier... pathFragments) {
        this.concatenateResults = true;
        List<Object> generators = new ArrayList<>();
        generators.add(getStaticValueGenerator(scheme + "://"));
        generators.add(getDictListGenerator(hostsDictionary));
        Arrays.asList(pathFragments).forEach(f -> {
            generators.add(getStaticValueGenerator("/"));
            generators.add(getGenOtherFieldValue(f));
        });
        this.generators = generators;
    }

    public UriGenerator(String scheme, URI hostsDictionary, URI... fragments) {
        this.concatenateResults = true;
        List<Object> generators = new ArrayList<>();
        generators.add(getStaticValueGenerator(scheme + "://"));
        generators.add(getDictListGenerator(hostsDictionary));
        Arrays.asList(fragments).forEach(f -> {
            generators.add(getStaticValueGenerator("/"));
            generators.add(getDictListGenerator(f));
        });
        this.generators = generators;
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

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_ANY_URI);
    }

}
