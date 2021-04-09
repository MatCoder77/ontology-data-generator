package pl.edu.pwr.ontologydatagenerator.domain.ontology.instance;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class DataPropertyValueProvider implements OWLDataRangeVisitorEx<OWLDatatype> {

    private static final String DATA_RANGE_NOT_SUPPORTED_MSG = "Cannot provide type for field. Data range {0} is not supported.";

    public DataPropertyValue getDataPropertyValue(DataPropertyInstance dataPropertyInstance) {
        OWLDatatype datatype = dataPropertyInstance.getDataProperty().getRange().getRange().accept(this);
        return new DataPropertyValue(dataPropertyInstance.getValue(), datatype);
    }

    @Override
    public OWLDatatype visit(OWLDatatype node) {
        return node;
    }

    @Override
    public OWLDatatype visit(OWLDataOneOf node) {
        throw getUnsupportedOperationException(node);
    }

    @Override
    public OWLDatatype visit(OWLDataComplementOf node) {
        throw getUnsupportedOperationException(node);
    }

    @Override
    public OWLDatatype visit(OWLDataIntersectionOf node) {
        throw getUnsupportedOperationException(node);
    }

    @Override
    public OWLDatatype visit(OWLDataUnionOf node) {
        throw getUnsupportedOperationException(node);
    }

    @Override
    public OWLDatatype visit(OWLDatatypeRestriction node) {
        return node.getDatatype();
    }

    private UnsupportedOperationException getUnsupportedOperationException(OWLDataRange dataRange) {
        return new UnsupportedOperationException(MessageFormat.format(DATA_RANGE_NOT_SUPPORTED_MSG, dataRange.getDataRangeType()));
    }

}
