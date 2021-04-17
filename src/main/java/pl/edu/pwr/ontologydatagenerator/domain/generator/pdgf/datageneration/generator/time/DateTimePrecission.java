package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.generator.time;

import lombok.Getter;

import java.time.temporal.ChronoUnit;

@Getter
enum DateTimePrecission {

    YEARS("yyyy", "-01-01T00:00:00Z", ChronoUnit.YEARS),
    MONTHS("yyyy-MM", "-01T00:00:00Z", ChronoUnit.MONTHS),
    DAYS("yyyy-MM-dd", "T00:00:00Z", ChronoUnit.DAYS),
    HOURS("yyyy-MM-dd'T'HH", ":00:00Z", ChronoUnit.HOURS),
    MINUTES("yyyy-MM-dd'T'HH:mm", ":00Z", ChronoUnit.MINUTES),
    SECONDS("yyyy-MM-dd'T'HH:mm:ss", "Z", ChronoUnit.SECONDS);

    private final String format;
    private final String reminder;
    private final ChronoUnit unit;

    DateTimePrecission(String format, String reminder, ChronoUnit unit) {
        this.format = format;
        this.reminder = reminder;
        this.unit = unit;
    }

}
