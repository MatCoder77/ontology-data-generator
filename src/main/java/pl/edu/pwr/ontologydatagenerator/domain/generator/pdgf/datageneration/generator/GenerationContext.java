package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class GenerationContext {

    private final OWL2Datatype datatype;
    private final Collection<OWLFacetRestriction> restrictions;
    private final DataProperty dataProperty;
    private final Concept concept;
    private final OntologyContainer<OWLOntology> container;

}
