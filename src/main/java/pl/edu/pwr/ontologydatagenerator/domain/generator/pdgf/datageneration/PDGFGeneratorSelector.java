package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.RequiredArgsConstructor;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;

@RequiredArgsConstructor
public class PDGFGeneratorSelector {


    public Generator getGenerator(Concept concept, DataProperty dataProperty) {
        return null;
    }

}