package pl.edu.pwr.ontologydatagenerator.infrastructure.collection;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

}
