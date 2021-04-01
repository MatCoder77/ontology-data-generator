package pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.distribution;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.domain.generator.DistributionProvider;
import pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration.Distribution;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.dataproperty.DataProperty;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.HasIdentifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.identifier.Identifier;
import pl.edu.pwr.ontologydatagenerator.domain.ontology.objectproperty.ObjectProperty;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PDGFDistributionProvider implements DistributionProvider<Distribution> {

    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_TYPE = "app.generator.pdgf.datageneration.property.{0}.distribution.type";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_BETA = "app.generator.pdgf.datageneration.property.{0}.distribution.beta";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_ALPHA = "app.generator.pdgf.datageneration.property.{0}.distribution.alpha";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_N = "app.generator.pdgf.datageneration.property.{0}.distribution.n";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_P = "app.generator.pdgf.datageneration.property.{0}.distribution.p";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_LAMBDA = "app.generator.pdgf.datageneration.property.{0}.distribution.lambda";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_MEAN = "app.generator.pdgf.datageneration.property.{0}.distribution.mean";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_SD = "app.generator.pdgf.datageneration.property.{0}.distribution.sd";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_PK = "app.generator.pdgf.datageneration.property.{0}.distribution.pk";
    private static final String PROPERTY_TEMPLATE_DISTRIBUTION_RO = "app.generator.pdgf.datageneration.property.{0}.distribution.ro";

    private final Environment environment;

    @Override
    public Distribution getDistribution(Identifier concept, DataProperty dataProperty) {
        return getDistributionBasedOnConfigurationOrDefault(dataProperty);
    }

    private Distribution getDistributionBasedOnConfigurationOrDefault(HasIdentifier property) {
        return getPropertyValue(property, PROPERTY_TEMPLATE_DISTRIBUTION_TYPE, DistributionType.class, environment)
                .map(distributionType -> distributionType.getDistributionBasedOnConfiguration(property, environment))
                .orElseGet(PDGFDistributionProvider::getUniformDistribution);
    }

    private static <T> T requirePropertyValue(HasIdentifier property, String propertyTemplate, Class<T> targetClass, Environment environment) {
        return getPropertyValue(property, propertyTemplate, targetClass, environment)
                .orElseThrow(() -> new IllegalStateAppException(MessageFormat.format("Property {0} not defined", MessageFormat.format(propertyTemplate, property.getName()))));
    }

    private static <T> Optional<T> getPropertyValue(HasIdentifier property, String propertyTemplate, Class<T> targetClass, Environment environment) {
        return Optional.ofNullable(environment.getProperty(MessageFormat.format(propertyTemplate, property.getName()), targetClass));
    }

    @Override
    public Distribution getDistribution(Identifier concept, ObjectProperty dataProperty) {
        return getUniformDistribution();
    }

    @SuppressWarnings("SameReturnValue")
    private static Distribution getUniformDistribution() {
        return null;
    }

    private static Distribution getBetaDistribution(double alpha, double beta) {
        return new Distribution()
                .withName(DistributionType.Beta.name())
                .withAlpha(alpha)
                .withBeta(beta);
    }

    private static Distribution getBinomialDistribution(long n, double p) {
        return new Distribution()
                .withName(DistributionType.Binomial.name())
                .withN(n)
                .withP(p);
    }

    private static Distribution getExponentialDistribution(double lambda) {
        return new Distribution()
                .withName(DistributionType.Exponential.name())
                .withLambda(lambda);
    }

    private static Distribution getLogarithmicDistribution(double p) {
        return new Distribution()
                .withName(DistributionType.Logarithmic.name())
                .withP(p);
    }

    private static Distribution getLogNormalFastDistribution(double mean, double sd) {
        return new Distribution()
                .withName(DistributionType.LogNormalFast.name())
                .withMean(mean)
                .withSd(sd);
    }

    private static Distribution getNormalDistribution(double mean, double sd) {
        return new Distribution()
                .withName(DistributionType.Normal.name())
                .withMean(mean)
                .withSd(sd);
    }

    private static Distribution getZetaDistribution(double pk, double ro)  {
        return new Distribution()
                .withName(DistributionType.Zeta.name())
                .withPk(pk)
                .withRo(ro);
    }

    enum DistributionType {
        Uniform {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                return getUniformDistribution();
            }
        },
        Beta {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                double alpha = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_ALPHA, Double.class, environment);
                double beta = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_BETA, Double.class, environment);
                return getBetaDistribution(alpha, beta);
            }
        },
        Binomial {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                long n = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_N, Long.class, environment);
                double p = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_P, Double.class, environment);
                return getBinomialDistribution(n, p);
            }
        },
        Exponential {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                double lambda = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_LAMBDA, Double.class, environment);
                return getExponentialDistribution(lambda);
            }
        },
        Logarithmic {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                double p = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_P, Double.class, environment);
                return getLogarithmicDistribution(p);
            }
        },
        LogNormalFast {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                double mean = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_MEAN, Double.class, environment);
                double sd = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_SD, Double.class, environment);
                return getLogNormalFastDistribution(mean, sd);
            }
        },
        Normal {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                double mean = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_MEAN, Double.class, environment);
                double sd = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_SD, Double.class, environment);
                return getNormalDistribution(mean, sd);
            }
        },
        Zeta {
            @Override
            Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment) {
                double pk = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_PK, Double.class, environment);
                double ro = requirePropertyValue(identifier, PROPERTY_TEMPLATE_DISTRIBUTION_RO, Double.class, environment);
                return getZetaDistribution(pk, ro);
            }
        };

        abstract Distribution getDistributionBasedOnConfiguration(HasIdentifier identifier, Environment environment);
    }

}
