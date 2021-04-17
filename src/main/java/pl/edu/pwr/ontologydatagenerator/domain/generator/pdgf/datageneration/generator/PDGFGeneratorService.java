package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalArgumentAppException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class PDGFGeneratorService {

    private final List<DataPropertyGeneratorProducer> dataPropertyGeneratorProducers;
    private final ObjectPropertyGeneratorProducer objectPropertyGeneratorProducer;
    private final GenerationContextProvider generationContextProvider;

    public Generator getGenerator(DataProperty dataProperty, Concept concept, OntologyContainer<OWLOntology> container) {
        DataPropertyGenerationContext generationContext = generationContextProvider.getGenerationContext(dataProperty, concept, container);
        return selectBestGenerator(generationContext);
    }

    private Generator selectBestGenerator(DataPropertyGenerationContext context) {
        List<DataPropertyGeneratorProducer> dataPropertyGeneratorProducersForDatatype = getApplicableGeneratorProducers(context);
        DataPropertyGeneratorProducer bestDataPropertyGeneratorProducer = getBestRatedGeneratorProducer(context, dataPropertyGeneratorProducersForDatatype);
        Generator selectedGenerator = bestDataPropertyGeneratorProducer.buildGenerator(context);
        log.info("Selected {} generator for property {} in concept {}", selectedGenerator.getClass().getSimpleName(), context.getDataProperty().getName(), context.getConcept().getName());
        return selectedGenerator;
    }

    private List<DataPropertyGeneratorProducer> getApplicableGeneratorProducers(DataPropertyGenerationContext generationContext) {
        return dataPropertyGeneratorProducers.stream()
                .filter(producer -> producer.isApplicable(generationContext))
                .collect(Collectors.toList());
    }

    private DataPropertyGeneratorProducer getBestRatedGeneratorProducer(DataPropertyGenerationContext context, Collection<DataPropertyGeneratorProducer> dataPropertyGeneratorProducers) {
        return dataPropertyGeneratorProducers.stream()
                .max(Comparator.comparingDouble(producer -> producer.getScore(context)))
                .orElseThrow(() -> new IllegalArgumentAppException("Error, at least one generator should be available!"));
    }

    public Generator getGenerator(ObjectProperty objectProperty, Concept concept, Collection<Concept> conceptsToInstatniate, OntologyContainer<OWLOntology> container) {
        ObjectPropertyGenerationContext context = generationContextProvider.getGenerationContext(objectProperty, concept, new HashSet<>(conceptsToInstatniate), container);
        return objectPropertyGeneratorProducer.buildGenerator(context);
    }

}
