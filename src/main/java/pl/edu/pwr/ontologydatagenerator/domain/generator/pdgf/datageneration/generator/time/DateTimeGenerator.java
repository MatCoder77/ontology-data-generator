package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.time;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenDateTime;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public class DateTimeGenerator extends GenDateTime implements Generator {

    public DateTimeGenerator(Temporal startDate, Temporal endDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        this.startDate = formatter.format(startDate);
        this.endDate = formatter.format(endDate);
        this.inputFormat = format;
        this.outputFormat = format;
    }

}
