package pl.edu.pwr.ontologydatagenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.edu.pwr.ontologydatagenerator.domain.generator.GenerationEngineConfigurationService;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.configuration.PDGFConfiguration;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.OntologyService;
import pl.edu.pwr.ontologydatagenerator.domain.storage.url.UrlProvider;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class OntologyDataGeneratorApplication implements CommandLineRunner {

	private final UrlProvider localUrlProvider;
	private final OntologyService<OWLOntology> ontologyService;
	private final GenerationEngineConfigurationService<PDGFConfiguration> configurationService;

	public static void main(String[] args) {
		SpringApplication.run(OntologyDataGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("Application started!");
		OWLOntology ontology = ontologyService.loadOntology(localUrlProvider.getUrlForResource("pizza.owl"));
		ontologyService.validateOntology(ontology);
		ontologyService.saveOntology(ontology, localUrlProvider.getUrlForResource("output/pizza2.owl"));
		configurationService.saveConfiguration(configurationService.getDefaultConfiguration(), localUrlProvider.getUrlForResource("/schema/scg2.xml"));
		log.info("Application finished successfully!");
	}

}
