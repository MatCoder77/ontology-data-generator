package pl.edu.pwr.ontologydatagenerator.infrastructure.collection;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@UtilityClass
public class CollectionUtils {

    @SafeVarargs
    public static <E, C extends Collection<E>> List<E> listOf(C collection, E... otherElements) {
        return collectionOf(collection, ArrayList::new, otherElements);
    }

    @SafeVarargs
    public static <E, C extends Collection<E>, R extends Collection<E>> R collectionOf(C collection, Supplier<R> collectionFactory, E... otherElements) {
        return Stream.concat(collection.stream(), Arrays.stream(otherElements))
                .collect(Collectors.toCollection(collectionFactory));
    }

    @SafeVarargs
    public static <E, C extends Collection<E>> List<E> listOf(E element, C... collections) {
        return collectionOf(element, ArrayList::new, collections);
    }

    @SafeVarargs
    public static <E, C extends Collection<E>, R extends Collection<E>> R collectionOf(E element, Supplier<R> collectionFactory, C... collections) {
        return Stream.of(List.of(element), Arrays.stream(collections).flatMap(Collection::stream).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(collectionFactory));
    }

    public static <T> Predicate<T> distinctBy(Function<? super T, Collection<?>> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.addAll(keyExtractor.apply(t));
    }

}
