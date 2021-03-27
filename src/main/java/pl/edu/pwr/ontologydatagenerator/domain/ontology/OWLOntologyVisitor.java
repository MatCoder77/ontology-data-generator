//package pl.edu.pwr.ontologydatagenerator.domain.ontology;
//
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.semanticweb.owlapi.model.*;
//import org.semanticweb.owlapi.util.OWLOntologyWalker;
//import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
//
//import java.util.Collection;
//import java.util.Set;
//
//@Slf4j
//@Getter
//public class OWLOntologyVisitor extends OWLOntologyWalkerVisitor {
//
//    private final OntologyContainer<OWLOntology> ontologyContainer;
//    private final IdentifierMapper identifierMapper;
//
//    public OWLOntologyVisitor(OWLOntologyWalker walker) {
//        super(walker);
//        walker.getOntology().classesInSignature()
//        this.ontologyContainer = OntologyContainer.of(walker.getOntology());
//        this.identifierMapper = new IdentifierMapper();
//    }
//
//    //////////////////////////////////// OWLAxiomVisitor ///////////////////////////////////////////////////////
//
//    @Override
//    public void visit(OWLDeclarationAxiom axiom) {
//        log.info("Visiting axiom {}", axiom);
//        addAbsentConceptsToContainer(axiom);
//        addAbsentDataPropertyToContainer(axiom);
//    }
//
//    private void addAbsentConceptsToContainer(OWLDeclarationAxiom declarationAxiom) {
//        identifierMapper.mapToIdentifiers(declarationAxiom.getClassesInSignature())
//                .forEach(ontologyContainer::addConceptIfAbsent);
//    }
//
//    private void addAbsentDataPropertyToContainer(OWLDeclarationAxiom declarationAxiom) {
//        identifierMapper.mapToIdentifiers(declarationAxiom.getDataPropertiesInSignature())
//                .forEach(ontologyContainer::addDataPropertyIfAbsent);
//    }
//
//    private void addAbsentObjectPropertyToContainer(OWLDeclarationAxiom declarationAxiom) {
//        identifierMapper.mapToIdentifiers(declarationAxiom.getObjectPropertiesInSignature())
//                .forEach(ontologyContainer::addDataPropertyIfAbsent);
//    }
//
//    @Override
//    public void visit(OWLDatatypeDefinitionAxiom axiom) {
//
//    }
//
//    ////////////////////////////////// OWLAnnotationObjectVisitor /////////////////////////////////////////////
//
//    @Override
//    public void visit(OWLAnnotation node) {
//
//    }
//
//    ////////////////////////////////// OWLAnnotationAxiomVisitor /////////////////////////////////////////////
//
//    @Override
//    public void visit(OWLAnnotationAssertionAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
//
//    }
//
//    ////////////////////////////////// OWLClassExpressionVisitor /////////////////////////////////////////////
//
//    @Override
//    public void visit(OWLObjectIntersectionOf ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectUnionOf ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectComplementOf ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectSomeValuesFrom ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectAllValuesFrom ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectHasValue ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectMinCardinality ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectExactCardinality ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectMaxCardinality ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectHasSelf ce) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectOneOf ce) {
//
//    }
//
//    @Override
//    public void visit(OWLDataSomeValuesFrom ce) {
//
//    }
//
//    @Override
//    public void visit(OWLDataAllValuesFrom ce) {
//
//    }
//
//    @Override
//    public void visit(OWLDataHasValue ce) {
//
//    }
//
//    @Override
//    public void visit(OWLDataMinCardinality ce) {
//
//    }
//
//    @Override
//    public void visit(OWLDataExactCardinality ce) {
//
//    }
//
//    @Override
//    public void visit(OWLDataMaxCardinality ce) {
//
//    }
//
//    ////////////////////////////////// OWLLogicaAxiomVisitor /////////////////////////////////////////////
//
//    @Override
//    public void visit(OWLSubClassOfAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDisjointClassesAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDataPropertyDomainAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectPropertyDomainAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDifferentIndividualsAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDisjointDataPropertiesAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectPropertyRangeAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDisjointUnionAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDataPropertyRangeAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLFunctionalDataPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLClassAssertionAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLEquivalentClassesAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLDataPropertyAssertionAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLSubDataPropertyOfAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLSameIndividualAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLSubPropertyChainOfAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
//
//    }
//
//    @Override
//    public void visit(OWLHasKeyAxiom axiom) {
//
//    }
//
//    ////////////////////////////////// OWLDataRangeVisitor /////////////////////////////////////////////
//
//    @Override
//    public void visit(OWLDataOneOf node) {
//
//    }
//
//    @Override
//    public void visit(OWLDataComplementOf node) {
//
//    }
//
//    @Override
//    public void visit(OWLDataIntersectionOf node) {
//
//    }
//
//    @Override
//    public void visit(OWLDataUnionOf node) {
//
//    }
//
//    @Override
//    public void visit(OWLDatatypeRestriction node) {
//
//    }
//
//    ////////////////////////////////// OWLClassVisitorBase /////////////////////////////////////////////
//    @Override
//    public void visit(OWLClass ce) {
//
//    }
//
//    ////////////////////////////////// OWLPropertyEntityVisitorBase ////////////////////////////////////
//
//    @Override
//    public void visit(OWLObjectInverseOf property) {
//
//    }
//
//    @Override
//    public void visit(OWLObjectProperty property) {
//
//    }
//
//    @Override
//    public void visit(OWLDataProperty property) {
//
//    }
//
//    @Override
//    public void visit(OWLAnnotationProperty property) {
//
//    }
//
//    ////////////////////////////////// OWLNamedObjectVisitor ////////////////////////////////////////
//
//    @Override
//    public void visit(OWLOntology ontology) {
//
//    }
//
//    ////////////////////////////////// OWLAnnotationValueVisitor ////////////////////////////////////////
//
//    @Override
//    public void visit(IRI iri) {
//
//    }
//
//    ////////////////////////////////// OWLAnonymousIndividualVisitorBase ////////////////////////////////////////
//
//    @Override
//    public void visit(OWLAnonymousIndividual individual) {
//
//    }
//
//    ////////////////////////////////// OWLDataVisitor ///////////////////////////////////////////////////////////
//
//    @Override
//    public void visit(OWLFacetRestriction node) {
//
//    }
//
//    ////////////////////////////////// OWLDataEntityVisitorBase //////////////////////////////////////////////
//    @Override
//    public void visit(OWLDatatype node) {
//
//    }
//
//    @Override
//    public void visit(OWLNamedIndividual individual) {
//
//    }
//
//    @Override
//    public void visit(OWLLiteral node) {
//
//    }
//}
