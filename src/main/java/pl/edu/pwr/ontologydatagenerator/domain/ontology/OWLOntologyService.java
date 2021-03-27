package pl.edu.pwr.ontologydatagenerator.domain.ontology;

import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.concept.Concept;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.OWLDataPropertyService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.OWLObjectPropertyService;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.domain.storage.StorageService;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.ThrowingConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Service
@Qualifier("OWL")
@RequiredArgsConstructor
class OWLOntologyService implements OntologyService<OWLOntology>  {

    private final StorageService storageService;
    private final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    private final OWLOntologyValidator ontologyValidator;
    private final OWLDataPropertyService dataPropertyService;
    private final OWLObjectPropertyService objectPropertyService;

    @Override
    public OWLOntology loadOntology(URI url) {
        Resource ontologyResource = storageService.getResource(url);
        try(InputStream ontologyStream = ontologyResource.getInputStream()) {
           return ontologyManager.loadOntologyFromOntologyDocument(ontologyStream);
        } catch (OWLOntologyCreationException | IOException e) {
            log.error("Cannot create ontology from resource: ", e);
            throw new IllegalStateAppException(e);
        }
    }

    @Override
    public void saveOntology(OWLOntology ontology, URI url) {
        try {
            storageService.saveResource(getOntologySaver(ontology), url);
        } catch (RuntimeException e) {
            log.error("Cannot save ontology to resource: ", e);
            throw new IllegalStateException(e);
        }

    }

    private Consumer<OutputStream> getOntologySaver(OWLOntology ontology) {
        return ThrowingConsumer.wrapper(outputStream -> ontologyManager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), outputStream));
    }

    @Override
    public void validateOntology(OWLOntology ontology) {
        ontologyValidator.validate(ontology);
    }

    @Override
    public OntologyContainer<OWLOntology> parseOntology(OWLOntology ontology) {
        Collection<Concept> concepts = getConcepts(ontology);
        return OntologyContainer.of(ontology);
    }

    private Collection<Concept> getConcepts(OWLOntology ontology) {
        Set<OWLClass> classes = ontology.getClassesInSignature();
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLReasoner reasoner = getResasoner(ontology);
        List<DataProperty> dataProperties = dataPropertyService.getDataProperties(dataFactory, reasoner);
        List<ObjectProperty> objectProperties = objectPropertyService.getObjectProperties(reasoner);
        return null;
    }

    private OWLReasoner getResasoner(OWLOntology ontology) {
        return new ReasonerFactory().createReasoner(ontology);
    }

}
