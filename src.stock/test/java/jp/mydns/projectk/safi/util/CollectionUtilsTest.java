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

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code CollectionUtils}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class CollectionUtilsTest {

    /**
     * Test of toLinkedHashMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToLinkedHashMap() {
        var result = Stream.of(entry(2, "no2"), entry(1, "no1"), entry(3, "no3")).collect(CollectionUtils.toLinkedHashMap());

        assertThat(result).containsExactly(entry(2, "no2"), entry(1, "no1"), entry(3, "no3"));
    }

    /**
     * Test of toLinkedHashMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToLinkedHashMap_BinaryOperator() {
        var result = Stream.of(entry(2, "no2"), entry(1, "no1"), entry(3, "no3"), entry(2, "++"))
            .collect(CollectionUtils.toLinkedHashMap((v1, v2) -> v1 + v2));

        assertThat(result).containsExactly(entry(2, "no2++"), entry(1, "no1"), entry(3, "no3"));
    }

    /**
     * Test of toTreeMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToTreeMap() {
        var result = Stream.of(entry(2, "no2"), entry(1, "no1"), entry(3, "no3")).collect(CollectionUtils.toTreeMap());

        assertThat(result).containsExactly(entry(1, "no1"), entry(2, "no2"), entry(3, "no3"));
    }

    /**
     * Test of toTreeMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToTreeMap_BinaryOperator() {
        var result = Stream.of(entry(2, "no2"), entry(1, "no1"), entry(3, "no3"), entry(2, "++"))
            .collect(CollectionUtils.toTreeMap((v1, v2) -> v1 + v2));

        assertThat(result).containsExactly(entry(1, "no1"), entry(2, "no2++"), entry(3, "no3"));
    }

    /**
     * Test of toCaseInsensitiveMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToCaseInsensitiveMap() {
        var result = Stream.of(entry("A", "no2"), entry("B", "no1")).collect(CollectionUtils.toCaseInsensitiveMap());

        assertThat(result).containsKeys("a", "b").containsExactlyInAnyOrderEntriesOf(Map.of("A", "no2", "B", "no1"));
    }

    /**
     * Test of toCaseInsensitiveMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToCaseInsensitiveMap_BinaryOperator() {
        var result = Stream.of(entry("A", "no2"), entry("B", "no1"), entry("b", "--"))
            .collect(CollectionUtils.toCaseInsensitiveMap((v1, v2) -> v1 + v2));

        assertThat(result).containsKeys("a", "b").containsExactlyInAnyOrderEntriesOf(Map.of("A", "no2", "B", "no1--"));
    }

    /**
     * Test of narrowDown method.
     *
     * @since 3.0.0
     */
    @Test
    void testNarrowDown() {
        List<Integer> src = List.of(3, 2, 1, 0);
        Predicate<Integer> pred = i -> i % 2 == 0;

        List<Integer> result = CollectionUtils.narrowDown(pred).apply(src);

        assertThat(result).containsExactly(2, 0);
    }

    /**
     * Test of convertElements method.
     *
     * @since 3.0.0
     */
    @Test
    void testConvertElements() {
        List<Object> src = List.of(3, 2, "1", 0);

        List<String> result = CollectionUtils.convertElements(String::valueOf).apply(src);

        assertThat(result).containsExactly("3", "2", "1", "0");
    }
}
