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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class {ContentMap}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class ContentMapTest {

    /**
     * Test of isEmpty method.
     *
     * @since 3.0.0
     */
    @Test
    void testIsEmpty() throws IOException {
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var empty = new ContentMap<String>(Collections.emptyIterator(), tmpDir, new TestConvertor());
        var noEmpty = new ContentMap<String>(Collections.singleton(Map.entry("k", "v")).iterator(), tmpDir,
            new TestConvertor());
        assertThat(empty.isEmpty()).isTrue();
        assertThat(noEmpty.isEmpty()).isFalse();
    }

    /**
     * Test of size method.
     *
     * @since 3.0.0
     */
    @Test
    void testSize() throws IOException {
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var empty = new ContentMap<String>(Collections.emptyIterator(), tmpDir, new TestConvertor());
        var noEmpty = new ContentMap<String>(Collections.singleton(Map.entry("k", "v")).iterator(), tmpDir,
            new TestConvertor());
        assertThat(empty.size()).isEqualTo(0);
        assertThat(noEmpty.size()).isEqualTo(1);
    }

    /**
     * Test of containsKey method.
     *
     * @since 3.0.0
     */
    @Test
    void testContainsKey() throws IOException {
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var instance = new ContentMap<String>(Collections.singleton(Map.entry("k", "v")).iterator(), tmpDir,
            new TestConvertor());
        assertThat(instance.containsKey("k")).isTrue();
        assertThat(instance.containsKey("v")).isFalse();
    }

    /**
     * Test of keySet method.
     *
     * @since 3.0.0
     */
    @Test
    void testKeySet() throws IOException {
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var instance = new ContentMap<String>(Map.of("k", "v", "k2", "v2").entrySet().iterator(), tmpDir,
            new TestConvertor());
        assertThat(instance.keySet()).containsExactlyInAnyOrder("k", "k2");
    }

    /**
     * Test of stream method.
     *
     * @since 3.0.0
     */
    @Test
    void testStream() throws IOException {
        var src = Map.of("k", "v", "k2", "v2");
        var expect = src.values();
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var instance = new ContentMap<String>(src.entrySet().iterator(), tmpDir, new TestConvertor());
        assertThat(instance.stream()).containsExactlyInAnyOrderElementsOf(expect);
    }

    /**
     * Test of get method.
     *
     * @since 3.0.0
     */
    @Test
    void testGet() throws IOException {
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var instance = new ContentMap<String>(Collections.singleton(Map.entry("k", "v")).iterator(), tmpDir,
            new TestConvertor());

        assertThat(instance.get("k")).isEqualTo("v");
        assertThat(instance.get("k2")).isNull();
    }

    /**
     * Test of hasDuplicates method.
     *
     * @since 3.0.0
     */
    @Test
    void testHasDuplicates() throws IOException {
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var duplicates = new ContentMap<String>(Stream.of(Map.entry("k", "v"), Map.entry("K", "V")).iterator(),
            tmpDir, new TestConvertor());
        var noDuplicates = new ContentMap<String>(Stream.of(Map.entry("k", "v")).iterator(), tmpDir, new TestConvertor());
        assertThat(duplicates.hasDuplicates()).isTrue();
        assertThat(noDuplicates.hasDuplicates()).isFalse();
    }

    /**
     * Test of duplicates method.
     *
     * @since 3.0.0
     */
    @Test
    void testDuplicates() throws IOException {
        var tmpDir = Path.of(System.getProperty("java.io.tmpdir"));

        var duplicates = new ContentMap<String>(Stream.of(
            Map.entry("k", "v"), Map.entry("K", "V"), Map.entry("k", "v2")).iterator(), tmpDir, new TestConvertor());

        assertThat(duplicates.duplicates()).containsExactlyInAnyOrder("v", "V", "v2");
    }

    private class TestConvertor implements ContentMap.Convertor<String> {

        @Override
        public String serialize(String c) {
            return c;
        }

        @Override
        public String deserialize(String s) {
            return s;
        }
    }
}
