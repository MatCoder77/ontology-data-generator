package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty;

import lombok.Getter;
import org.semanticweb.owlapi.model.OWLDataRange;

@Getter
public class DataPropertyRange {

    private final OWLDataRange range;

    private DataPropertyRange(OWLDataRange range) {
        this.range = range;
    }

    public static DataPropertyRange of(OWLDataRange range) {
        return new DataPropertyRange(range);
    }

}
