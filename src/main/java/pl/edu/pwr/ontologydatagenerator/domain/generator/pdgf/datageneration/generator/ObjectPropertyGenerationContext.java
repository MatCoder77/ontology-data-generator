package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ObjectPropertyGenerationContext {

    private final ObjectProperty objectProperty;
    private final Concept concept;
    private final Set<Concept> conceptsToInstantiate;
    private final OntologyContainer<OWLOntology> container;

}
