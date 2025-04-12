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
package jp.mydns.projectk.safi.value;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * A container that stores a large amount of content. Used to store large amounts of content that causes out of memory.
 * The content you store must have a unique ID. If duplicate keys are detected, all content with the same key will be
 * excluded. This class creates a working file. It will be deleted on close.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * </ul>
 *
 * @param <T> content type
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class ContentMap<T> implements Closeable {

    private final Map<String, Point> points = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, List<Point>> duplicates = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Path tmpFile;
    private final RandomAccessFile storage;
    private final Convertor<T> converter;

    /**
     * Constructor.
     *
     * @param contents id and value of content
     * @param tmpDir where to save temporary files
     * @param converter the {@code Convertor}
     * @throws IOException if occurs I/O error
     * @throws NullPointerException if any argument is {@code null} or if exists a {@code null} in {@code contents}
     * @since 3.0.0
     */
    public ContentMap(Iterator<Entry<String, T>> contents, Path tmpDir, Convertor<T> converter) throws IOException {
        Objects.requireNonNull(contents);

        this.converter = Objects.requireNonNull(converter);
        this.tmpFile = Files.createTempFile(Objects.requireNonNull(tmpDir), null, null);

        long offset = 0;

        try (var os = Files.newOutputStream(tmpFile); var bos = new BufferedOutputStream(os);) {

            while (contents.hasNext()) {
                // Write one content.
                Entry<String, T> e = contents.next();
                byte[] content = converter.serialize(e.getValue()).getBytes(StandardCharsets.UTF_8);
                bos.write(content);

                Point point = new Point(offset, offset + content.length);
                String key = e.getKey();

                if (duplicates.containsKey(key)) {
                    duplicates.compute(key, (k, v) -> {
                        v.add(point);
                        return v;
                    });
                } else {
                    Optional.ofNullable(points.put(key, point)).ifPresent(p -> {
                        var dupPoints = new ArrayList<Point>();

                        dupPoints.add(p);
                        dupPoints.add(points.remove(key));

                        duplicates.put(key, dupPoints);
                    });
                }

                offset = point.to;
            }
        } catch (IOException | RuntimeException ex) {
            Files.deleteIfExists(tmpFile);
            throw ex;
        }

        this.storage = new RandomAccessFile(tmpFile.toFile(), "r");
    }

    /**
     * Returns {@code true} if no content storing.
     *
     * @return {@code true} if no content storing
     * @since 3.0.0
     */
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /**
     * Returns storing number of content.
     *
     * @return storing number of content
     * @since 3.0.0
     */
    public int size() {
        return points.size();
    }

    /**
     * Returns {@code true} if exists a content associated with {@code key}.
     *
     * @param key content id
     * @return {@code true} if exists a content associated with {@code key}
     * @since 3.0.0
     */
    public boolean containsKey(String key) {
        return points.containsKey(key);
    }

    /**
     * Returns all content ids.
     *
     * @return all content ids
     * @since 3.0.0
     */
    public Set<String> keySet() {
        return points.keySet();
    }

    /**
     * Get all values stored in this.
     *
     * @return all values stored in this
     * @since 3.0.0
     */
    public Stream<T> stream() {
        return keySet().stream().map(this::get);
    }

    /**
     * Get a value associated with {@code key}.
     *
     * @param key content id
     * @return a value associated with {@code key}. Returns {@code null} if not exists.
     * @throws UncheckedIOException if occurs I/O exception
     * @since 3.0.0
     */
    public T get(String key) {
        if (!containsKey(key)) {
            return null;
        }

        return get(points.get(key));
    }

    private T get(Point point) {
        synchronized (storage) {
            byte[] buf = new byte[Math.toIntExact(point.to - point.from)];

            try {
                storage.seek(point.from);
                storage.readFully(buf);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }

            return converter.deserialize(new String(buf, StandardCharsets.UTF_8));
        }
    }

    /**
     * Indicate that exists a duplicate value.
     *
     * @return {@code true} if exists a duplicate, otherwise {@code false}.
     * @since 3.0.0
     */
    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }

    /**
     * Get duplicate values.
     *
     * @return duplicate values
     * @since 3.0.0
     */
    public Stream<T> duplicates() {
        return duplicates.values().stream().flatMap(List::stream).map(this::get);
    }

    /**
     * Delete the temporary files used by this class.
     *
     * @throws IOException if occurs I/O error
     * @since 3.0.0
     */
    @Override
    public void close() throws IOException {
        storage.close();
        Files.deleteIfExists(tmpFile);
    }

    /**
     * Content serialize and de-serialize.
     *
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * </ul>
     *
     * @param <T> Content type
     * @version 3.0.0
     * @since 3.0.0
     */
    public static interface Convertor<T> {

        /**
         * Serialize to {@code String}.
         *
         * @param c content value
         * @return serialized content value
         * @since 3.0.0
         */
        String serialize(T c);

        /**
         * De-serialize from {@code String}.
         *
         * @param s serialized content value
         * @return content value
         * @since 3.0.0
         */
        T deserialize(String s);
    }

    private class Point {

        private final long from;
        private final long to;

        private Point(long from, long to) {
            this.from = from;
            this.to = to;
        }
    }
}
