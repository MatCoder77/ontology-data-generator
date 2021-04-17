package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.time;

import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenDateTime;
import pl.edu.pwr.ontologydatagenerator.domain.generator.Generator;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenSequential;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.GenStaticValue;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.List;

public class DateTimeGenerator extends GenSequential implements Generator {

    public DateTimeGenerator(Temporal startDate, Temporal endDate, DateTimePrecission precisson) {
        this.concatenateResults = true;
        this.generators = List.of(getDataTimeGenerator(startDate, endDate, precisson), getStaticValueGenerator(precisson.getReminder()));
    }

    private GenDateTime getDataTimeGenerator(Temporal startDate, Temporal endDate, DateTimePrecission precisson) {
        String format = precisson.getFormat();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return new GenDateTime()
                .withStartDate(formatter.format(startDate))
                .withEndDate(formatter.format(endDate))
                .withInputFormat(format)
                .withOutputFormat(format);
    }

    private GenStaticValue getStaticValueGenerator(String staticValue) {
        return new GenStaticValue()
                .withValue(staticValue);
    }

}
