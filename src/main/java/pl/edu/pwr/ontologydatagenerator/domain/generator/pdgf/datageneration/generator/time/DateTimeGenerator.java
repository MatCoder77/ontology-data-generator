package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.time;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenDateTime;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.Generator;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Set;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

public class DateTimeGenerator extends GenDateTime implements Generator {

    public DateTimeGenerator(Temporal startDate, Temporal endDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        this.startDate = formatter.format(startDate);
        this.endDate = formatter.format(endDate);
        this.inputFormat = format;
        this.outputFormat = format;
    }

    @Override
    public Set<OWL2Datatype> getSupportedDataTypes() {
        return Set.of(RDFS_LITERAL, XSD_DATE_TIME, XSD_DATE_TIME_STAMP);
    }

}
