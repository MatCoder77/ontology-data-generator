package pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.constraints;

import lombok.Getter;

@Getter
public class RangeValue<T> {

    final T value;
    final boolean isInclusive;

    private RangeValue(T value, boolean isInclusive) {
        this.value = value;
        this.isInclusive = isInclusive;
    }

    public static <T> RangeValue<T> of(T value, boolean isInclusive) {
        return new RangeValue<>(value, isInclusive);
    }

}
