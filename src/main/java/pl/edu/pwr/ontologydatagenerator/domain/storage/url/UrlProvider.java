package pl.edu.pwr.ontologydatagenerator.domain.storage.url;

import java.net.URI;

public interface UrlProvider {

    URI getUrlForResource(String... pathSegments);

}
