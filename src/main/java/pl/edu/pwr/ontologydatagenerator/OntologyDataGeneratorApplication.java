package pl.edu.pwr.ontologydatagenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;


@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class OntologyDataGeneratorApplication implements CommandLineRunner {

	private static final String UNIV_BENCH_EXTEDED_QL = "univ-bench_v2.owl";
	private static final String UNIV_BENCH = "univ-bench.owl";

	private final UrlProvider localUrlProvider;
	private final OntologyService<OWLOntology> ontologyService;

	public static void main(String[] args) {
		SpringApplication.run(OntologyDataGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("Application started!");
		OWLOntology ontology = ontologyService.loadOntology(localUrlProvider.getUrlForResource(UNIV_BENCH_EXTEDED_QL));
		ontologyService.validateOntology(ontology);
		ontologyService.saveOntology(ontology, localUrlProvider.getUrlForResource("output/univ-bench2.owl"));
		ontologyService.parseOntology(ontology);
		log.info("Application finished successfully!");
	}

}
