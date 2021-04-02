package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.table;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Table;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.field.PDGFFieldService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.infrastructure.collection.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDGFTableService {

    private static final String TAB_SIZE_PATTERN = "{0} * $'{'SF'}'";

    @Value("${app.generator.pdgf.datageneration.size}") private final Long baseSize;
    private final PDGFFieldService fieldService;

    public List<Table> getTables(OntologyContainer<OWLOntology> ontologyContainer) {
        return getConceptsToInstantiate(ontologyContainer).stream()
                .map(concept -> getTable(concept, ontologyContainer))
                .collect(Collectors.toList());
    }

    private List<Concept> getConceptsToInstantiate(OntologyContainer<OWLOntology> ontologyContainer) {
        return ontologyContainer.getConcepts().values().stream()
                .filter(CollectionUtils.distinctBy(Concept::getEquivalentConcepts))
                .filter(concept -> shouldConnceptBeInstantiated(concept))
                .collect(Collectors.toList());
    }

    private boolean shouldConnceptBeInstantiated(Concept concept) {
        return !isThingOrNothing(concept);
    }

    private boolean isThingOrNothing(Concept concept) {
        return concept.isThing() || concept.isNothing();
    }

    private Table getTable(Concept concept, OntologyContainer<OWLOntology> container) {
        return new Table()
                .withName(concept.getName())
                .withSize(getTableSize())
                .withField(fieldService.getFields(concept, container));
    }

    private String getTableSize() {
        return MessageFormat.format(TAB_SIZE_PATTERN, String.valueOf(baseSize));
    }

}
