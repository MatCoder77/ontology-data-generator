package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty;

import org.semanticweb.owlapi.model.OWLDataRange;

import java.util.Optional;

public class DataPropertyRange {

    private static final DataPropertyRange UNKNOWN_RANGE = new DataPropertyRange(null);
    private final OWLDataRange range;

    private DataPropertyRange(OWLDataRange range) {
        this.range = range;
    }

    public static DataPropertyRange of(OWLDataRange range) {
        return new DataPropertyRange(range);
    }

    public static DataPropertyRange unknown() {
        return UNKNOWN_RANGE;
    }

    public Optional<OWLDataRange> getRange() {
        return Optional.ofNullable(range);
    }

}
