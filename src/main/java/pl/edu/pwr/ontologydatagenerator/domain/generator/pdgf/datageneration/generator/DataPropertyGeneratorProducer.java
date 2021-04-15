package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.Set;
import java.util.stream.Collectors;

public interface DataPropertyGeneratorProducer {

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

    default boolean isApplicable(DataPropertyGenerationContext context) {
        return isDataTypeSupported(context.getDatatype());
    }

    default double getScore(DataPropertyGenerationContext context) {
        return 0;
    }

    Generator buildGenerator(DataPropertyGenerationContext context);

}
