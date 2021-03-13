package pl.edu.pwr.ontologydatagenerator.domain.storage.url;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
class LocalUrlProvider implements UrlProvider {

    private final String baseUrl;

    public LocalUrlProvider(@Value("${app.datastore}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public URI getUrlForResource(String relativePath) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(relativePath)
                .build()
                .toUri();
    }

}

