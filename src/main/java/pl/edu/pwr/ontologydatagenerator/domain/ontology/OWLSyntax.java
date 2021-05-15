package pl.edu.pwr.ontologydatagenerator.domain.ontology;

import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

public enum OWLSyntax {

    FUNCTIONAL {
        @Override
        OWLDocumentFormat getFormat() {
            return new FunctionalSyntaxDocumentFormat();
        }
    },
    RDF_XML {
        @Override
        OWLDocumentFormat getFormat() {
            return new RDFXMLDocumentFormat();
        }
    },
    OWL_XML {
        @Override
        OWLDocumentFormat getFormat() {
            return new OWLXMLDocumentFormat();
        }
    },
    MANCHESTER {
        @Override
        OWLDocumentFormat getFormat() {
            return new ManchesterSyntaxDocumentFormat();
        }
    },
    TURTLE {
        @Override
        OWLDocumentFormat getFormat() {
            return new TurtleDocumentFormat();
        }
    };

    abstract OWLDocumentFormat getFormat();

}
