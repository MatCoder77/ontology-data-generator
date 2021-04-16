package pl.edu.pwr.ontologydatagenerator.domain.generator;

import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Set;

public interface DictionaryDataProvider {

    Set<OWL2Datatype> getAllSupportedDatatypes();

    Set<Dictionary> getDictionariesForDataTypes(Set<OWL2Datatype> datatypes);

}
