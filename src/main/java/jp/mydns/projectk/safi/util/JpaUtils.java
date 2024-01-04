/*
 * Copyright (c) 2024, Project-K
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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonCollectors;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.TypedQuery;
import java.io.StringReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterators;
import static java.util.stream.Collectors.toUnmodifiableMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.CursoredStream;

/**
 * Utilities for JPA.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class has not variable field member and it has all method is static.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class JpaUtils {

    public static final String PREFIX_JSONOBJECT = "jsonobject___";
    public static final String PREFIX_JSONARRAY = "jsonarray___";

    private static final int CHUNK_SIZE = 1000;

    private JpaUtils() {
    }

    /**
     * Build a chunked stream from query results. Implementation depends on the EclipseLink.
     *
     * @param <T> entity type
     * @param query typed query
     * @return chunked stream of query results
     * @throws NullPointerException if {@code query} is {@code null}
     * @throws PersistenceException if the query execution was failed
     * @since 1.0.0
     */
    public static <T> Stream<List<T>> toChunkedStream(TypedQuery<T> query) {
        return toChunkedStream(Objects.requireNonNull(query), Map.of());
    }

    /**
     * Build a chunked stream from query results. Implementation depends on the EclipseLink.
     *
     * @param <T> entity type
     * @param query typed query
     * @param hints additional hints
     * @return chunked stream of query results
     * @throws NullPointerException if any argument is {@code null}
     * @throws ClassCastException if JPA implement is not EclipseLink
     * @throws PersistenceException if the query execution was failed
     * @see QueryHints
     * @since 1.0.0
     */
    public static <T> Stream<List<T>> toChunkedStream(TypedQuery<T> query, Map<String, Object> hints) {

        Objects.requireNonNull(query);
        Objects.requireNonNull(hints);

        query.setHint(QueryHints.CURSOR, HintValues.TRUE);
        query.setHint(QueryHints.CURSOR_PAGE_SIZE, CHUNK_SIZE);
        query.setHint(QueryHints.JDBC_FETCH_SIZE, CHUNK_SIZE);

        hints.entrySet().forEach(e -> query.setHint(e.getKey(), e.getValue()));

        CursoredStream cursor = CursoredStream.class.cast(query.getSingleResult());

        Iterator<List<T>> iterator = new Iterator<>() {

            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<T> next() {

                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                cursor.clear();

                return (List<T>) Collections.unmodifiableList(cursor.next(CHUNK_SIZE));

            }
        };

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false).onClose(cursor::close);

    }

    /**
     * Conversion to {@code Map<String, String>} from {@code Tuple}.
     *
     * @param tuple the {@code Tuple}
     * @return a map where the key is the alias name and the value is the string representation. Alias names may contain
     * certain prefixes, but key names do not. The prefix has {@value #PREFIX_JSONOBJECT},
     * {@value #PREFIX_JSONARRAY}.
     * @throws NullPointerException if {@code tuple} is {@code null}
     * @since 1.0.0
     */
    public static Map<String, String> toMap(Tuple tuple) {
        return Objects.requireNonNull(tuple).getElements().stream().map(TupleElement::getAlias).filter(Objects::nonNull)
                .collect(toUnmodifiableMap(JpaUtils::removePrefix, n -> String.valueOf(tuple.get(n))));
    }

    /**
     * Conversion to {@code JsonObject} from {@code Tuple}.
     *
     * @param tuple the {@code Tuple}
     * @return a {@code JsonObject} where the key is the alias name. Alias names may contain certain prefixes, but key
     * names do not. The prefix has {@value #PREFIX_JSONOBJECT},
     * {@value #PREFIX_JSONARRAY}.
     * @throws NullPointerException if {@code tuple} is {@code null}
     * @since 1.0.0
     */
    public static JsonObject toJsonObject(Tuple tuple) {
        return Objects.requireNonNull(tuple).getElements().stream().map(TupleElement::getAlias).filter(Objects::nonNull)
                .map(a -> toJsonEntry(a, tuple)).collect(JsonCollectors.toJsonObject());
    }

    private static Map.Entry<String, JsonValue> toJsonEntry(String a, Tuple t) {

        String key = JpaUtils.removePrefix(a);
        String value = t.get(a, String.class);

        if (a.startsWith(PREFIX_JSONOBJECT)) {
            try (var jr = Json.createReader(new StringReader(value))) {
                return Map.entry(key, jr.readObject());
            }
        }

        if (a.startsWith(PREFIX_JSONARRAY)) {
            try (var jr = Json.createReader(new StringReader(value))) {
                return Map.entry(key, jr.readArray());
            }
        }

        return Map.entry(key, Json.createValue(value));

    }

    private static String removePrefix(String a) {
        return removePrefix(removePrefix(a, PREFIX_JSONOBJECT), PREFIX_JSONARRAY);
    }

    private static String removePrefix(String a, String prefix) {
        return a.startsWith(prefix) ? a.substring(prefix.length()) : a;
    }
}
