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

import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.CursoredStream;

/**
 * Utilities for {@link Stream}.
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
public final class StreamUtils {

    private static final int CHUNK_SIZE = 1000;

    private StreamUtils() {
    }

    /**
     * Build a chunked stream from the {@code Stream}.
     *
     * @param <T> stream value type
     * @param src source stream
     * @return hunked stream of the {@code src}
     * @throws NullPointerException if {@code src} is {@code null}
     */
    public static <T> Stream<List<T>> toChunkedStream(Stream<T> src) {
        Objects.requireNonNull(src);

        Iterator<T> cursor = src.iterator();
        var iterator = new Iterator<List<T>>() {
            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public List<T> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                List<T> chunk = new ArrayList<>();
                for (int i = 0; hasNext() && i < CHUNK_SIZE; i++) {
                    chunk.add(cursor.next());
                }
                return Collections.unmodifiableList(chunk);
            }
        };

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false).onClose(src::close);
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
            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
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
}
