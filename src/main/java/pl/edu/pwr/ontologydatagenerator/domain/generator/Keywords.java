package pl.edu.pwr.ontologydatagenerator.domain.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Keywords {

    private Set<String> conceptKeywords;
    private Set<String> propertyKeywords;

    public static Keywords of(Set<String> conceptKeywords, Set<String> propertyKeywords) {
        return new Keywords(conceptKeywords, propertyKeywords);
    }

}
