package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.datacharcteristics.constraints;

import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PDGFDataPropertyConstraintsProvider {

    private static final String PROPERTY_TEMPLATE_CONSTRAINTS = "app.generator.pdgf.datageneration.properties.{0}.constraints.{1}";
    private static final String PROPERTY_TEMPLATE_PRECISION = "app.generator.pdgf.datageneration.properties.{0}.precision";

    private final Environment environment;

    public Map<OWLFacet, String> getDataPropertyConstraints(DataProperty property) {
        return Arrays.stream(OWLFacet.values())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Function.identity(), facet -> getFacetValue(property, facet)),
                        this::getMapWithPresentConstraints));
    }

    private Optional<String> getFacetValue(HasIdentifier property, OWLFacet facet) {
        return getPropertyValue(getConstraintPath(property, facet), String.class);
    }

    private String getConstraintPath(HasIdentifier property, OWLFacet facet) {
        return MessageFormat.format(PROPERTY_TEMPLATE_CONSTRAINTS, property.getName(), facet.getShortForm());
    }

    private <T> Optional<T> getPropertyValue(String path, Class<T> targetClass) {
        return Optional.ofNullable(environment.getProperty(path, targetClass));
    }

    private Map<OWLFacet, String> getMapWithPresentConstraints(Map<OWLFacet, Optional<String>> valuesByConstraints) {
        return valuesByConstraints.entrySet().stream()
                .filter(valueByConstraint -> valueByConstraint.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, valueByConstraint -> valueByConstraint.getValue().get()));
    }

    public <T> Optional<T> getDataPropertyPrecission(DataProperty dataProperty, Function<String, T> parser) {
        return getPropertyValue(getPrecissionPath(dataProperty), String.class)
                .map(parser);
    }

    private String getPrecissionPath(HasIdentifier property) {
        return MessageFormat.format(PROPERTY_TEMPLATE_PRECISION, property.getName());
    }

}
