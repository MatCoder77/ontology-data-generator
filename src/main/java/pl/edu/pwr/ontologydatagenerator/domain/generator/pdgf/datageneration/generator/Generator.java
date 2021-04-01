package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Set;
import java.util.stream.Collectors;

public interface Generator {

    default Set<OWL2Datatype.Category> getSupportedCategories() {
        return getSupportedDataTypes().stream()
                .map(OWL2Datatype::getCategory)
                .collect(Collectors.toSet());
    }

    Set<OWL2Datatype> getSupportedDataTypes();

    default boolean isCategorySupported(OWL2Datatype.Category category) {
        return getSupportedCategories().contains(category);
    }

    default boolean isDataTypeSupported(OWL2Datatype datatype) {
        return getSupportedDataTypes().contains(datatype);
    }

}
