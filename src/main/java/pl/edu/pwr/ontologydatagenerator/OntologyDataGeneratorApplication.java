package pl.edu.pwr.ontologydatagenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngine;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.instance.Instance;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;
import pl.edu.pwr.ontologydatagenerator.infrastructure.evaluation.MetricsCalculator;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalArgumentAppException;

import java.net.URI;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties
public class OntologyDataGeneratorApplication implements CommandLineRunner {

	private static final String CALCULATE_METRICS_ARG = "calculate-metrics";
	@Value("${app.datastore.input}") private final String inputDirectory;
	@Value("${app.datastore.output}") private final String outputDirectory;
	private final UrlProvider localUrlProvider;
	private final OntologyService<OWLOntology, Instance> ontologyService;
	private final GenerationEngine<OntologyContainer<OWLOntology>, Instance> generationEngine;
	private final MetricsCalculator metricsCalculator;

	public static void main(String[] args) {
		new SpringApplicationBuilder(OntologyDataGeneratorApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Override
	public void run(String... args) {
		log.info("Application started!");
		String ontologyFilename = getOntologyFilename(args);
		URI inputOntologyUrl = getInputOntologyUrl(ontologyFilename);
		OWLOntology ontology = ontologyService.loadOntology(inputOntologyUrl);
		ontologyService.validateOntology(ontology);
		ontologyService.generateInstances(ontology, generationEngine);
		URI outputOntologyUrl = getOutputOntologyUrl(ontologyFilename);
		ontologyService.saveOntology(ontology, outputOntologyUrl);
		ontologyService.validateOntology(ontology);
		calculateMetricsIfRequested(ontology, args);
		log.info("Application finished successfully!");
	}

	private String getOntologyFilename(String... args) {
		try {
			return args[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error("Supply ontology file as first argument!");
			throw new IllegalArgumentAppException("Required argument not found", e);
		}
	}

	private URI getInputOntologyUrl(String ontologyFilename) {
		return localUrlProvider.getUrlForResource(inputDirectory, ontologyFilename);
	}

	private URI getOutputOntologyUrl(String ontologyFilename) {
		return localUrlProvider.getUrlForResource(outputDirectory, ontologyFilename);
	}

	private void calculateMetricsIfRequested(OWLOntology ontology, String... args) {
		if (isCalculateMetricsArgumentPresent(args)) {
			OntologyContainer<OWLOntology> container = ontologyService.parseOntology(ontology);
			metricsCalculator.calculateMetrics(container);
		}
	}

	private boolean isCalculateMetricsArgumentPresent(String... args) {
		return Stream.of(args)
				.filter(CALCULATE_METRICS_ARG::equals)
				.findAny()
				.isPresent();
	}

}
