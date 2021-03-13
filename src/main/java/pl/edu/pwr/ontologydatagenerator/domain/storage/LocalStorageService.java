package pl.edu.pwr.ontologydatagenerator.domain.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import pl.edu.pwr.ontologydatagenerator.infrastructure.asynchronous.CompletableFutures;
import pl.edu.pwr.ontologydatagenerator.infrastructure.exception.IllegalStateAppException;
import pl.edu.pwr.ontologydatagenerator.infrastructure.transform.TransformUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component("LocalStorageService")
class LocalStorageService implements StorageService {

    private final ResourceLoader resourceLoader;
    private final AsyncTaskExecutor executor;

    public LocalStorageService(ResourcePatternResolver resourceLoader, @Qualifier("customizedThreadPoolExecutor") AsyncTaskExecutor executor) {
        this.resourceLoader = resourceLoader;
        this.executor = executor;
    }

    @Override
    public Map<URI, Resource> getResources(Collection<URI> urls) {
        List<CompletableFuture<AbstractMap.SimpleEntry<URI, Resource>>> resourceFutures = urls.stream()
                .map(this::mapToGetResourceTask)
                .collect(Collectors.toList());
        return TransformUtils.transformToMap(CompletableFutures.allOf(resourceFutures).join(), Map.Entry::getKey, Map.Entry::getValue);
    }

    private CompletableFuture<AbstractMap.SimpleEntry<URI, Resource>> mapToGetResourceTask(URI url) {
        return CompletableFuture.supplyAsync(() -> getUrlAndResourceEntry(url, getResource(url)), executor);
    }

    private AbstractMap.SimpleEntry<URI, Resource> getUrlAndResourceEntry(URI url, Resource resource) {
        return new AbstractMap.SimpleEntry<>(url, resource);
    }

    @Override
    public Resource getResource(URI url) {
        return resourceLoader.getResource(url.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveResources(Map<URI, Resource> resourcesByUrl) {
        CompletableFuture<Void>[] uploadFutures = resourcesByUrl.entrySet().stream()
                .map(this::mapToSaveResourceTask)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(uploadFutures).join();
    }

    private CompletableFuture<Void> mapToSaveResourceTask(Map.Entry<URI, Resource> resourceByUrl) {
        return CompletableFuture.runAsync(() -> saveResource(resourceByUrl.getValue(), resourceByUrl.getKey()), executor);
    }

    @Override
    public void saveResource(Resource resourceToSave, URI url) {
        WritableResource resource = (WritableResource) resourceLoader.getResource(url.toString());
        try (OutputStream outputStream = resource.getOutputStream()) {
            resourceToSave.getInputStream().transferTo(outputStream);
        } catch (IOException exception) {
            log.error("Cannot save resource: ", exception);
            throw new IllegalStateAppException("Error during saving resource: ", exception);
        }
    }

    @Override
    public void saveResource(Consumer<OutputStream> resourceSaver, URI url) {
        try(OutputStream outputStream = FileUtils.openOutputStream(new File(url.getPath()))) {
            resourceSaver.accept(outputStream);
        } catch (IOException e) {
            log.error("Cannot save resource: ", e);
            throw new IllegalStateAppException(e);
        }
    }

}
