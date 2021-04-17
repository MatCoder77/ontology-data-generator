package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenerationContextProvider {

    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG ="{0} ranges are currently not supported!";

    public DataPropertyGenerationContext getGenerationContext(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        DataRangeType rangeType = getRangeType(dataProperty);
        return switch (rangeType) {
            case DATATYPE -> getGenerationContextForDatatypeRange(dataProperty, concept, container);
            case DATATYPE_RESTRICTION -> getGenerationContextForDatatypeRestrictionRange(dataProperty, concept, container);
            default -> throw getUnsupportedException(rangeType);
        };
    }

    private DataRangeType getRangeType(DataProperty dataProperty) {
        return dataProperty.getRange().getRange().getDataRangeType();
    }

    private DataPropertyGenerationContext getGenerationContextForDatatypeRange(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        OWL2Datatype datatype = getRangeAsOWLDatatype(dataProperty).getBuiltInDatatype();
        return new DataPropertyGenerationContext(datatype, Collections.emptyList(), dataProperty, concept, container);
    }

    private OWLDatatype getRangeAsOWLDatatype(DataProperty dataProperty) {
        return dataProperty.getRange().getRange().asOWLDatatype();
    }

    private DataPropertyGenerationContext getGenerationContextForDatatypeRestrictionRange(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        OWLDatatypeRestriction range = getRangeAsOWLDatatypeRestriction(dataProperty);
        OWL2Datatype datatype = getBuildInDatatype(range);
        Set<OWLFacetRestriction> restrictions = range.getFacetRestrictions();
        return new DataPropertyGenerationContext(datatype, restrictions, dataProperty, concept, container);
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

    public ObjectPropertyGenerationContext getGenerationContext(ObjectProperty objectProperty, Concept concept, Collection<Concept> conceptsToInstatniate, OntologyContainer<OWLOntology> container) {
        return new ObjectPropertyGenerationContext(objectProperty, concept, new HashSet<>(conceptsToInstatniate), container);
    }

}
