package pl.edu.pwr.ontologydatagenerator.domain.storage.url;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
class LocalUrlProvider implements UrlProvider {

    @Value("${app.datastore}") private final String baseUrl;

    @Override
    public URI getUrlForResource(String... pathSegments) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(pathSegments)
                .build()
                .toUri();
    }

}

