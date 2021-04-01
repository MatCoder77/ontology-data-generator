package pl.edu.pwr.ontologydatagenerator.infrastructure.configuration.marshalling;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class BaseXmlMarshallerConfiguration {

    protected Marshaller getBaseMarshaller(String contextPath) {
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler", new CdataEscapeHandler());
            return marshaller;
        } catch (JAXBException e) {
            log.error("Cannot obtain XML marschaller instance");
            throw new IllegalStateAppException(e);
        }
    }

}
