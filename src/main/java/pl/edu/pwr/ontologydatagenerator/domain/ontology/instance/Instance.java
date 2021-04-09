package pl.edu.pwr.ontologydatagenerator.domain.ontology.instance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Instance {

    private final Identifier identifier;
    private final Concept concept;
    private final List<DataPropertyInstance> dataPropertyInstances;
    private final List<ObjectPropertyInstance> objectPropertyInstances;

}
