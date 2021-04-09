package pl.edu.pwr.ontologydatagenerator.domain.ontology.instance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;

@Getter
@RequiredArgsConstructor
public class DataPropertyInstance {

    private final DataProperty dataProperty;
    private final String value;

}
