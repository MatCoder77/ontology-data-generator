package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.DataRangeType;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataPropertyRange;

import java.util.Collection;

@RequiredArgsConstructor
public class PDGFGeneratorSelector {

    private final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    private final OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();

    public Generator getGenerator(Concept concept, DataProperty dataProperty) {
        DataPropertyRange dataPropertyRange = getDataPropertyRange(dataProperty);
        DataRangeType dataRangeType = getDataRangeType(dataPropertyRange);
        return null;
    }

    private DataPropertyRange getDataPropertyRange(DataProperty dataProperty) {
        return dataProperty.getRange()
                .orElseGet(() -> inferDataPropertyRange(dataProperty));
    }

    private DataPropertyRange inferDataPropertyRange(DataProperty dataProperty) {
        return dataProperty.getValuesByIndividualIdentifier().values().stream()
                .flatMap(Collection::stream)
                .map(this::getDataPropertyRangeFromLiteral)
                .findAny()
                .orElseGet(() -> DataPropertyRange.of(dataFactory.getStringOWLDatatype()));
    }

    private DataPropertyRange getDataPropertyRangeFromLiteral(OWLLiteral literal) {
        return DataPropertyRange.of(literal.getDatatype());
    }

    private DataRangeType getDataRangeType(DataPropertyRange dataPropertyRange) {
        return dataPropertyRange.getRange().getDataRangeType();
    }

}