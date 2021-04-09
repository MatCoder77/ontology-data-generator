package pl.edu.pwr.ontologydatagenerator.domain.ontology.instance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

@Getter
@RequiredArgsConstructor
public class ObjectPropertyInstance {

    private final ObjectProperty objectProperty;
    private final Identifier value;

}
