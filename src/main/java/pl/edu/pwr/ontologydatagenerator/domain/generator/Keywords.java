package pl.edu.pwr.ontologydatagenerator.domain.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Keywords {

    private Set<String> conceptKeywords = new LinkedHashSet<>();
    private Set<String> propertyKeywords = new LinkedHashSet<>();

}
