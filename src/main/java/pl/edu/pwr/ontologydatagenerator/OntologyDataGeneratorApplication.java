package pl.edu.pwr.ontologydatagenerator;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;

@SpringBootApplication
@RequiredArgsConstructor
public class OntologyDataGeneratorApplication implements CommandLineRunner {

	private final UrlProvider localUrlProvider;
	private final OntologyService<OWLOntology> ontologyService;

	public static void main(String[] args) {
		SpringApplication.run(OntologyDataGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Application started!");
		OWLOntology ontology = ontologyService.loadOntology(localUrlProvider.getUrlForResource("pizza.owl"));
		ontologyService.validateOntology(ontology);
		ontologyService.saveOntology(ontology, localUrlProvider.getUrlForResource("output/pizza2.owl"));
	}

}
