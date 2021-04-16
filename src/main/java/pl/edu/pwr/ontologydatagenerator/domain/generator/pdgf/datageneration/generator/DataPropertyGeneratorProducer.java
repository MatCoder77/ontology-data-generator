package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.util.Set;

public interface DataPropertyGeneratorProducer {

    Set<OWL2Datatype> getSupportedDataTypes();

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
