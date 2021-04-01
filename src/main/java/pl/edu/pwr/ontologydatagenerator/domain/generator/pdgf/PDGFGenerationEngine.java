package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.SchemaDefinitonService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngine;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.PDGFSchemaDefinition;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;

@Service
@RequiredArgsConstructor
public class PDGFGenerationEngine implements GenerationEngine<OntologyContainer<OWLOntology>, PDGFDataGenerationResult> {

    private final SchemaDefinitonService<PDGFSchemaDefinition, OWLOntology> schemaDefinitonService;

    @Override
    public PDGFDataGenerationResult generateData(OntologyContainer<OWLOntology> ontologyContainer) {
        return null;
    }

}
