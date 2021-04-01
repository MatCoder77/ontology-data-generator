package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Field;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;

@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class DataPropertyRangeVisitor implements OWLDataRangeVisitorEx<Field> {

    private static final String NOT_SUPPORTED_RANGE_TYPE_MSG ="Cannot generate data for data property {0}. {1} ranges are currently not supported!";

    private final OntologyContainer<OWLOntology> ontologyContainer;
    private final Concept concept;
    private final DataProperty dataProperty;

    @Override
    public Field visit(OWLDatatype range) {
        OWL2Datatype datatype = range.getBuiltInDatatype();
        OWL2Datatype.Category category = datatype.getCategory();

        return new Field();
    }

    @Override
    public Field visit(OWLDataOneOf range) {
        throw getUnsupportedException(DataRangeType.DATA_ONE_OF);
    }

    @Override
    public Field visit(OWLDataComplementOf range) {
        throw getUnsupportedException(DataRangeType.DATA_COMPLEMENT_OF);
    }

    @Override
    public Field visit(OWLDataIntersectionOf range) {
        throw getUnsupportedException(DataRangeType.DATA_INTERSECTION_OF);
    }

    @Override
    public Field visit(OWLDataUnionOf range) {
        throw getUnsupportedException(DataRangeType.DATA_UNION_OF);
    }

    @Override
    public Field visit(OWLDatatypeRestriction range) {
        return new Field();
    }

    private UnsupportedOperationException getUnsupportedException(DataRangeType rangeType) {
        return new UnsupportedOperationException(MessageFormat.format(NOT_SUPPORTED_RANGE_TYPE_MSG, dataProperty.getName(), rangeType.getName()));
    }

}
