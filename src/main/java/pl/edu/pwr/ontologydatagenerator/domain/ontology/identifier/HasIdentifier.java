package pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier;

public interface HasIdentifier {

    Identifier getIdentifier();

    default String getName() {
        return getIdentifier().getIri().getShortForm();
    }

}
