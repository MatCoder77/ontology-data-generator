package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.field;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.MessageFormat;
import java.util.Map;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class FieldTypeProvider implements OWLDataRangeVisitorEx<FieldTypeProvider.FieldType> {

    private static final String DATA_RANGE_NOT_SUPPORTED_MSG = "Cannot provide type for field. Data range {0} is not supported.";
    private static final Map<OWL2Datatype, FieldType> fieldTypeByDatatype = Map.ofEntries(
            Map.entry(RDF_XML_LITERAL, FieldType.VARCHAR),
            Map.entry(RDFS_LITERAL, FieldType.VARCHAR), 
            Map.entry(RDF_PLAIN_LITERAL, FieldType.VARCHAR),
            Map.entry(RDF_LANG_STRING, FieldType.VARCHAR),
            Map.entry(OWL_REAL, FieldType.DOUBLE),
            Map.entry(OWL_RATIONAL, FieldType.DOUBLE),
            Map.entry(XSD_STRING, FieldType.VARCHAR),
            Map.entry(XSD_NORMALIZED_STRING, FieldType.VARCHAR),
            Map.entry(XSD_TOKEN, FieldType.VARCHAR),
            Map.entry(XSD_LANGUAGE, FieldType.VARCHAR),
            Map.entry(XSD_NAME, FieldType.VARCHAR),
            Map.entry(XSD_NCNAME, FieldType.VARCHAR),
            Map.entry(XSD_NMTOKEN, FieldType.VARCHAR),
            Map.entry(XSD_DECIMAL, FieldType.DOUBLE),
            Map.entry(XSD_INTEGER, FieldType.NUMERIC),
            Map.entry(XSD_NON_NEGATIVE_INTEGER, FieldType.NUMERIC),
            Map.entry(XSD_NON_POSITIVE_INTEGER, FieldType.NUMERIC),
            Map.entry(XSD_POSITIVE_INTEGER, FieldType.NUMERIC),
            Map.entry(XSD_NEGATIVE_INTEGER, FieldType.NUMERIC),
            Map.entry(XSD_LONG, FieldType.NUMERIC),
            Map.entry(XSD_INT, FieldType.NUMERIC),
            Map.entry(XSD_SHORT, FieldType.NUMERIC),
            Map.entry(XSD_BYTE, FieldType.NUMERIC),
            Map.entry(XSD_UNSIGNED_LONG, FieldType.NUMERIC),
            Map.entry(XSD_UNSIGNED_INT, FieldType.NUMERIC),
            Map.entry(XSD_UNSIGNED_SHORT, FieldType.NUMERIC),
            Map.entry(XSD_UNSIGNED_BYTE, FieldType.NUMERIC),
            Map.entry(XSD_DOUBLE, FieldType.DOUBLE),
            Map.entry(XSD_FLOAT, FieldType.DOUBLE),
            Map.entry(XSD_BOOLEAN, FieldType.BOOLEAN),
            Map.entry(XSD_HEX_BINARY, FieldType.VARCHAR),
            Map.entry(XSD_BASE_64_BINARY, FieldType.VARCHAR),
            Map.entry(XSD_ANY_URI, FieldType.VARCHAR),
            Map.entry(XSD_DATE_TIME, FieldType.DATE),
            Map.entry(XSD_DATE_TIME_STAMP, FieldType.DATE));

    public FieldType getFieldType(DataProperty dataProperty) {
        return dataProperty.getRange().getRange().accept(this);
    }

    public FieldType getFieldType(ObjectProperty objectProperty) {
        return FieldType.VARCHAR;
    }

    @Override
    public FieldType visit(OWLDatatype range) {
        return fieldTypeByDatatype.get(range.getBuiltInDatatype());
    }

    @Override
    public FieldType visit(OWLDataOneOf range) {
        throw getUnsupportedOperationException(range);
    }

    @Override
    public FieldType visit(OWLDataComplementOf range) {
        throw getUnsupportedOperationException(range);
    }

    @Override
    public FieldType visit(OWLDataIntersectionOf range) {
        throw getUnsupportedOperationException(range);
    }

    @Override
    public FieldType visit(OWLDataUnionOf range) {
        throw getUnsupportedOperationException(range);
    }

    @Override
    public FieldType visit(OWLDatatypeRestriction range) {
        return fieldTypeByDatatype.get(range.getDatatype().getBuiltInDatatype());
    }

    private UnsupportedOperationException getUnsupportedOperationException(OWLDataRange dataRange) {
        return new UnsupportedOperationException(MessageFormat.format(DATA_RANGE_NOT_SUPPORTED_MSG, dataRange.getDataRangeType()));
    }

    public enum FieldType {

        VARCHAR,
        BIT,
        TINYINT,
        SMALLINT,
        INTEGER,
        BIGINT,
        FLOAT,
        REAL,
        DOUBLE,
        NUMERIC,
        DECIMAL,
        CHAR,
        LONGVARCHAR,
        DATE,
        TIME,
        TIMESTAMP,
        BINARY,
        VARBINARY,
        LONGVARBINARY,
        NULL,
        OTHER,
        JAVA_OBJECT,
        DISTINCT,
        STRUCT,
        ARRAY,
        BLOB,
        CLOB,
        REF,
        DATALINK,
        BOOLEAN,
        ROWID,
        NCHAR,
        NVARCHAR,
        LONGNVARCHAR,
        NCLOB,
        SQLXML,
        REF_CURSOR,
        TIME_WITH_TIMEZONE,
        TIMESTAMP_WITH_TIMEZONE

    }

}
