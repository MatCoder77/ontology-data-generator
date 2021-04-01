package pl.edu.pwr.ontologydatagenerator.domain.generator;

import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.List;

public interface DictionaryDataProvider {

    List<Dictionary> getDictionariesForDatatype(OWL2Datatype datatype);

}
