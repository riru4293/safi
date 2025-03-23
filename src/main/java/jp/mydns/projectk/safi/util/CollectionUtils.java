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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static jp.mydns.projectk.safi.util.LambdaUtils.alwaysThrow;

/**
 * Utilities for collection.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class has not variable field member and it has all method is static.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Collect to unmodifiable the {@code LinkedHashMap} from {@code Map.Entry}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return the {@code Collector} for collect to {@code LinkedHashMap}
     * @throws IllegalArgumentException if detected duplicate keys
     * @since 3.0.0
     */
    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toLinkedHashMap() {
        return toLinkedHashMap(alwaysThrow());
    }

    /**
     * Collect to unmodifiable the {@code LinkedHashMap} from {@code Map.Entry}.
     *
     * @param <K> key type
     * @param <V> value type
     * @param mergeFunc merge function if detected duplicate keys
     * @return the {@code Collector} for collect to {@code LinkedHashMap}
     * @throws NullPointerException if {@code mergeFunc} is {@code null}
     * @since 3.0.0
     */
    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toLinkedHashMap(BinaryOperator<V> mergeFunc) {
        return collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue,
            Objects.requireNonNull(mergeFunc), LinkedHashMap::new), Collections::unmodifiableMap);
    }

    /**
     * Collect to unmodifiable the {@code TreeMap} from {@code Map.Entry}.
     *
     * @param <K> key type
     * @param <V> value type
     * @return the {@code Collector} for collect to {@code TreeMap}
     * @throws IllegalArgumentException if detected duplicate keys
     * @since 3.0.0
     */
    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toTreeMap() {
        return toTreeMap(alwaysThrow());
    }

    /**
     * Collect to unmodifiable the {@code TreeMap} from {@code Map.Entry}.
     *
     * @param <K> key type
     * @param <V> value type
     * @param mergeFunc merge function if detected duplicate keys
     * @return the {@code Collector} for collect to {@code TreeMap}
     * @throws NullPointerException if {@code mergeFunc} is {@code null}
     * @since 3.0.0
     */
    public static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toTreeMap(BinaryOperator<V> mergeFunc) {
        return collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue,
            Objects.requireNonNull(mergeFunc), TreeMap::new), Collections::unmodifiableMap);
    }

    /**
     * Collects from {@code Map.Entry} into {@code TreeMap} with immutable and case-insensitive keys.
     *
     * @param <V> value type
     * @return the {@code Collector} for collect to {@code TreeMap} with immutable and case-insensitive keys
     * @throws NullPointerException if {@code mergeFunc} is {@code null}
     * @since 3.0.0
     */
    public static <V> Collector<Map.Entry<String, V>, ?, Map<String, V>> toCaseInsensitiveMap() {
        return toCaseInsensitiveMap(alwaysThrow());
    }

    /**
     * Collects from {@code Map.Entry} into {@code TreeMap} with immutable and case-insensitive keys.
     *
     * @param <V> value type
     * @param mergeFunc merge function if detected duplicate keys
     * @return the {@code Collector} for collect to {@code TreeMap} with immutable and case-insensitive keys
     * @throws NullPointerException if {@code mergeFunc} is {@code null}
     * @since 3.0.0
     */
    public static <V> Collector<Map.Entry<String, V>, ?, Map<String, V>>
        toCaseInsensitiveMap(BinaryOperator<V> mergeFunc) {

        return collectingAndThen(toMap(Map.Entry::getKey, Map.Entry::getValue, Objects.requireNonNull(mergeFunc),
            () -> new TreeMap<String, V>(String.CASE_INSENSITIVE_ORDER)), Collections::unmodifiableMap);
    }
}
