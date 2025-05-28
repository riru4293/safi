/*
 * Copyright (c) 2025, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.mydns.projectk.safi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static jp.mydns.projectk.safi.util.LambdaUtils.alwaysThrow;
import static jp.mydns.projectk.safi.util.LambdaUtils.compute;

/**
 Utilities for collection.

 <p>
 Implementation requirements.
 <ul>
 <li>This class has not variable field member and it has all method is static.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public class CollectionUtils {

private CollectionUtils() {
}

/**
 Collect to toUnmodifiable the {@code LinkedHashMap} from {@code Map.Entry}.

 @param <K> key type
 @param <V> value type
 @return the {@code Collector} for collect to {@code LinkedHashMap}
 @throws IllegalArgumentException if detected duplicate keys
 @since 3.0.0
 */
public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toLinkedHashMap() {
    return toLinkedHashMap(alwaysThrow());
}

/**
 Collect to toUnmodifiable the {@code LinkedHashMap} from {@code Map.Entry}.

 @param <K> key type
 @param <V> value type
 @param mergeFunc merge function if detected duplicate keys
 @return the {@code Collector} for collect to {@code LinkedHashMap}
 @throws NullPointerException if {@code mergeFunc} is {@code null}
 @since 3.0.0
 */
public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toLinkedHashMap(
    BinaryOperator<V> mergeFunc) {
    return collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue,
        Objects.requireNonNull(mergeFunc), LinkedHashMap::new), Collections::unmodifiableMap);
}

/**
 Collect to toUnmodifiable the {@code TreeMap} from {@code Map.Entry}.

 @param <K> key type
 @param <V> value type
 @return the {@code Collector} for collect to {@code TreeMap}
 @throws IllegalArgumentException if detected duplicate keys
 @since 3.0.0
 */
public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toTreeMap() {
    return toTreeMap(alwaysThrow());
}

/**
 Collect to toUnmodifiable the {@code TreeMap} from {@code Map.Entry}.

 @param <K> key type
 @param <V> value type
 @param mergeFunc merge function if detected duplicate keys
 @return the {@code Collector} for collect to {@code TreeMap}
 @throws NullPointerException if {@code mergeFunc} is {@code null}
 @since 3.0.0
 */
public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toTreeMap(BinaryOperator<V> mergeFunc) {
    return collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue,
        Objects.requireNonNull(mergeFunc), TreeMap::new), Collections::unmodifiableMap);
}

/**
 Collects from {@code Map.Entry} into {@code TreeMap} with immutable and case-insensitive keys.

 @param <V> value type
 @return the {@code Collector} for collect to {@code TreeMap} with immutable and case-insensitive
 keys
 @throws NullPointerException if {@code mergeFunc} is {@code null}
 @since 3.0.0
 */
public static <V> Collector<Map.Entry<String, V>, ?, Map<String, V>> toCaseInsensitiveMap() {
    return toCaseInsensitiveMap(alwaysThrow());
}

/**
 Collects from {@code Map.Entry} into {@code TreeMap} with immutable and case-insensitive keys.

 @param <V> value type
 @param mergeFunc merge function if detected duplicate keys
 @return the {@code Collector} for collect to {@code TreeMap} with immutable and case-insensitive
 keys
 @throws NullPointerException if {@code mergeFunc} is {@code null}
 @since 3.0.0
 */
public static <V> Collector<Map.Entry<String, V>, ?, Map<String, V>>
    toCaseInsensitiveMap(BinaryOperator<V> mergeFunc) {

    return collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue, Objects.requireNonNull(
        mergeFunc),
        () -> new TreeMap<String, V>(String.CASE_INSENSITIVE_ORDER)), Collections::unmodifiableMap);
}

/**
 Returns an unmodifiable view of the specified list.

 @param <V> value type
 @param list the {@code List}
 @return unmodifiable unmodifiable view of {@code list}. Returns {@code null} if {@code list} is
 {@code null}.
 @since 3.0.0
 */
public static <V> List<V> toUnmodifiable(List<V> list) {
    return Optional.ofNullable(list).map(s -> {
        List<V> value = new ArrayList<>();
        value.addAll(list);
        return Collections.unmodifiableList(value);
    }).orElse(list);
}

/**
 Returns an unmodifiable view of the specified set.

 @param <V> value type
 @param set the {@code Set}
 @return unmodifiable unmodifiable view of {@code set}. Returns {@code null} if {@code set} is
 {@code null}.
 @since 3.0.0
 */
public static <V> Set<V> toUnmodifiable(Set<V> set) {
    return Optional.ofNullable(set).map(s -> {
        Set<V> value = new HashSet<>();
        value.addAll(set);
        return Collections.unmodifiableSet(value);
    }).orElse(set);
}

/**
 Returns an unmodifiable view of the specified set.

 @param <V> value type
 @param set the {@code Set}
 @return unmodifiable unmodifiable view of {@code set}. Returns {@code null} if {@code set} is
 {@code null}.
 @since 3.0.0
 */
public static <V> SequencedSet<V> toUnmodifiable(SequencedSet<V> set) {
    return Optional.ofNullable(set).map(s -> {
        SequencedSet<V> value = new LinkedHashSet<>();
        value.addAll(set);
        return Collections.unmodifiableSequencedSet(value);
    }).orElse(set);
}

/**
 Returns an unmodifiable view of the specified map.

 @param <K> key type
 @param <V> value type
 @param map the {@code Map}
 @return unmodifiable unmodifiable view of {@code map}. Returns {@code null} if {@code map} is
 {@code null}.
 @since 3.0.0
 */
public static <K, V> Map<K, V> toUnmodifiable(Map<K, V> map) {
    return Optional.ofNullable(map).map(s -> {
        Map<K, V> value = new HashMap<>();
        value.putAll(map);
        return Collections.unmodifiableMap(value);
    }).orElse(map);
}

/**
 Returns an unmodifiable view of the specified map.

 @param <K> key type
 @param <V> value type
 @param map the {@code Map}
 @return unmodifiable unmodifiable view of {@code map}. Returns {@code null} if {@code map} is
 {@code null}.
 @since 3.0.0
 */
public static <K, V> SequencedMap<K, V> toUnmodifiable(SequencedMap<K, V> map) {
    return Optional.ofNullable(map).map(s -> {
        SequencedMap<K, V> value = new LinkedHashMap<>();
        value.putAll(map);
        return Collections.unmodifiableSequencedMap(value);
    }).orElse(map);
}

/**
 Returns a function that test the list values with a predicate to narrow it down.

 @param <T> element type of list
 @param predicate the {@code Predicate}
 @return function that returns a narrowed list
 @throws NullPointerException if {@code p} is {@code null}
 @since 3.0.0
 */
public static <T> UnaryOperator<List<T>> narrowDown(Predicate<T> predicate) {
    return l -> l.stream().filter(predicate).toList();
}

/**
 Returns a function that convert {@code List} elements.

 @param <B> element type that before conversion
 @param <A> element type that after conversion
 @param converter function that convert list elements
 @return function that convert list {@code List}
 @throws NullPointerException if {@code converter} is {@code null}
 @since 3.0.0
 */
public static <B, A> Function<List<B>, List<A>> convertElements(Function<B, A> converter) {
    Objects.requireNonNull(converter);

    return l -> l.stream().map(converter).toList();
}

/**
 Returns a function that convert {@code Map} values.

 @param <K> key type.
 @param <B> value type that before conversion.
 @param <A> value type that after conversion.
 @param converter function that convert {@code Map} values
 @param mapFactory {@code Map} instance factory for after conversion.
 @return function that convert {@code Map} values
 @throws NullPointerException if any argument is {@code null}
 @since 3.0.0
 */
public static <K, B, A> Function<Map<K, B>, Map<K, A>> convertValues(Function<B, A> converter,
    Collector<Map.Entry<K, A>, ?, Map<K, A>> mapFactory) {

    Objects.requireNonNull(converter);
    Objects.requireNonNull(mapFactory);

    return m -> m.entrySet().stream().sequential()
        .map(compute(converter)).collect(mapFactory);
}

}
