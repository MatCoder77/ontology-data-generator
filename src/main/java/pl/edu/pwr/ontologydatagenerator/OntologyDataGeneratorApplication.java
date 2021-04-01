package pl.edu.pwr.ontologydatagenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.edu.pwr.ontologydatagenerator.domain.generator.SchemaDefinitonService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.PDGFSchemaDefinition;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyContainer;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;


@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties
public class OntologyDataGeneratorApplication implements CommandLineRunner {

	private static final String UNIV_BENCH_EXTEDED_QL = "input/univ-bench_v2.owl";
	private static final String UNIV_BENCH = "input/univ-bench.owl";

	private final UrlProvider localUrlProvider;
	private final OntologyService<OWLOntology> ontologyService;
	private final SchemaDefinitonService<PDGFSchemaDefinition, OWLOntology> schemaDefinitonService;

	public static void main(String[] args) {
		SpringApplication.run(OntologyDataGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("Application started!");
		OWLOntology ontology = ontologyService.loadOntology(localUrlProvider.getUrlForResource(UNIV_BENCH_EXTEDED_QL));
		ontologyService.validateOntology(ontology);
		ontologyService.saveOntology(ontology, localUrlProvider.getUrlForResource("output/univ-bench2.owl"));
		OntologyContainer<OWLOntology> owlOntologyOntologyContainer = ontologyService.parseOntology(ontology);
		PDGFSchemaDefinition pdgfSchemaDefinition = schemaDefinitonService.buildSchemaDefinition(owlOntologyOntologyContainer);
		schemaDefinitonService.saveSchemaDefinition(pdgfSchemaDefinition, localUrlProvider.getUrlForResource("output/TEST.xml"));
		log.info("Application finished successfully!");
	}

}
