package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GeneratorSelector;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class PDGFGeneratorService {

    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG ="{0} ranges are currently not supported!";

    private final List<DataPropertyGeneratorProducer> dataPropertyGeneratorProducers;
    private final ObjectPropertyGeneratorProducer objectPropertyGeneratorProducer;

    public Generator getGenerator(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        DataRangeType rangeType = getRangeType(dataProperty);
        return switch (rangeType) {
            case DATATYPE -> getGeneratorForDatatypeRange(dataProperty, concept, container);
            case DATATYPE_RESTRICTION -> getGeneratorForDatatypeRestrictionRange(dataProperty, concept, container);
            default -> throw getUnsupportedException(rangeType);
        };
    }

    private DataRangeType getRangeType(DataProperty dataProperty) {
        return dataProperty.getRange().getRange().getDataRangeType();
    }

    private Generator getGeneratorForDatatypeRange(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        OWL2Datatype datatype = getRangeAsOWLDatatype(dataProperty).getBuiltInDatatype();
        DataPropertyGenerationContext generationContext = new DataPropertyGenerationContext(datatype, Collections.emptyList(), dataProperty, concept, container);
        GeneratorSelector generatorProvider = new PDGFDataPropertyGeneratorProvider(generationContext, dataPropertyGeneratorProducers);
        return generatorProvider.selectGenerator();
    }

    private OWLDatatype getRangeAsOWLDatatype(DataProperty dataProperty) {
        return dataProperty.getRange().getRange().asOWLDatatype();
    }

    private Generator getGeneratorForDatatypeRestrictionRange(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        OWLDatatypeRestriction range = getRangeAsOWLDatatypeRestriction(dataProperty);
        OWL2Datatype datatype = getBuildInDatatype(range);
        Set<OWLFacetRestriction> restrictions = range.getFacetRestrictions();
        DataPropertyGenerationContext generationContext = new DataPropertyGenerationContext(datatype, restrictions, dataProperty, concept, container);
        PDGFDataPropertyGeneratorProvider generatorProvider = new PDGFDataPropertyGeneratorProvider(generationContext, dataPropertyGeneratorProducers);
        return generatorProvider.selectGenerator();
    }

    private OWLDatatypeRestriction getRangeAsOWLDatatypeRestriction(DataProperty dataProperty) {
        return (OWLDatatypeRestriction) dataProperty.getRange().getRange();
    }

    private OWL2Datatype getBuildInDatatype(OWLDatatypeRestriction range) {
        if (range.getDatatype().isBuiltIn()) {
            return range.getDatatype().getBuiltInDatatype();
        }
        throw new IllegalStateAppException("Cannot select generator for not build in data type.");
    }

    private UnsupportedOperationException getUnsupportedException(DataRangeType rangeType) {
        return new UnsupportedOperationException(MessageFormat.format(NOT_SUPPORTED_RANGE_TYPE_MSG, rangeType.getName()));
    }

    public Generator getGenerator(ObjectProperty objectProperty, Concept concept, Collection<Concept> conceptsToInstatniate, OntologyContainer<OWLOntology> container) {
        ObjectPropertyGenerationContext context = new ObjectPropertyGenerationContext(objectProperty, concept, new HashSet<>(conceptsToInstatniate), container);
        return objectPropertyGeneratorProducer.buildGenerator(context);
    }

}
