package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class PDGFGeneratorService implements OWLDataRangeVisitorEx<Generator> {

    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG ="{0} ranges are currently not supported!";

    public Generator getGenerator(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        dataProperty.getRange().getRange().accept(this);
        return null;
    }

    @Override
    public Generator visit(OWLDatatype range) {
        OWL2Datatype datatype = range.getBuiltInDatatype();
        OWL2Datatype.Category category = datatype.getCategory();
        return null;
    }

    @Override
    public Generator visit(OWLDataOneOf range) {
        throw getUnsupportedException(DataRangeType.DATA_ONE_OF);
    }

    @Override
    public Generator visit(OWLDataComplementOf range) {
        throw getUnsupportedException(DataRangeType.DATA_COMPLEMENT_OF);
    }

    @Override
    public Generator visit(OWLDataIntersectionOf range) {
        throw getUnsupportedException(DataRangeType.DATA_INTERSECTION_OF);
    }

    @Override
    public Generator visit(OWLDataUnionOf range) {
        throw getUnsupportedException(DataRangeType.DATA_UNION_OF);
    }

    @Override
    public Generator visit(OWLDatatypeRestriction range) {
        return null;
    }

    private UnsupportedOperationException getUnsupportedException(DataRangeType rangeType) {
        return new UnsupportedOperationException(MessageFormat.format(NOT_SUPPORTED_RANGE_TYPE_MSG, rangeType.getName()));
    }

}
