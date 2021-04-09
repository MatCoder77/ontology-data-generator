package pl.edu.pwr.ontologydatagenerator.domain.ontology.instance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLDatatype;

@Getter
@RequiredArgsConstructor
public class DataPropertyValue {

    private final String lexicalValue;
    private final OWLDatatype datatype;

}
