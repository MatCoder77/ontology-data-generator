package pl.edu.pwr.ontologydatagenerator.domain.storage;


import org.springframework.core.io.Resource;

import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public interface StorageService {

    Resource getResource(URI url);

    Map<URI, Resource> getResources(Collection<URI> url);

    void saveResource(Resource resource, URI url);

    void saveResources(Map<URI, Resource> resourcesByUrl);

    void saveResource(Consumer<OutputStream> resourceSaver, URI url);

}
