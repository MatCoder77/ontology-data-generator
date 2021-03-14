package pl.edu.pwr.ontologydatagenerator.infrastructure.configuration.pdgf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.pwr.ontologydatagenerator.infrastructure.configuration.BaseXmlMarshallerConfiguration;

import javax.xml.bind.Marshaller;

@Slf4j
@Configuration
public class PDGFMarshallerConfiguration extends BaseXmlMarshallerConfiguration {

    private static final String PDGF_CONFIGURATION_CONTEXT = "pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.configuration";
    private static final String PDGF_DATAGENERATION_CONTEXT = "pl.edu.pwr.ontologydatagenerator.domain.generator.pdgf.datageneration";

    @Bean
    @Qualifier("PDGFConfiguration")
    public Marshaller getPDGFConfigurationMarshaller() {
        return getBaseMarshaller(PDGF_CONFIGURATION_CONTEXT);
    }

    @Bean
    @Qualifier("PDGFDatageneration")
    public Marshaller getPDGFDatagenerationMarshaller() {
        return getBaseMarshaller(PDGF_DATAGENERATION_CONTEXT);
    }

}
