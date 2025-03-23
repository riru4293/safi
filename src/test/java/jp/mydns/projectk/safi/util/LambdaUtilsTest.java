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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.entry;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code LambdaUtils}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class LambdaUtilsTest {

    /**
     * Test of firstWins method.
     *
     * @since 3.0.0
     */
    @Test
    void testFirstWins() {
        var expect = 1;
        var result = LambdaUtils.firstWins().apply(1, 9);
        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of lastWins method.
     *
     * @since 3.0.0
     */
    @Test
    void testLastWins() {
        var expect = 9;
        var result = LambdaUtils.lastWins().apply(1, 9);
        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of alwaysThrow method.
     *
     * @since 3.0.0
     */
    @Test
    void testAlwaysThrow() {
        ThrowingCallable proc = () -> LambdaUtils.alwaysThrow().apply(1, 9);
        assertThatIllegalArgumentException().isThrownBy(proc);
    }

    /**
     * Test of c method.
     *
     * @since 3.0.0
     */
    @Test
    void testC_Consumer() {
        List<String> items1 = new ArrayList<>();
        List<String> items2 = new ArrayList<>();

        Consumer<Integer> result = c(c(items1::addLast).andThen(items2::addFirst), Object::toString);

        result.accept(7);
        result.accept(5);
        result.accept(3);

        assertThat(items1).containsExactly("7", "5", "3");
        assertThat(items2).containsExactly("3", "5", "7");
    }

    /**
     * Test of c method.
     *
     * @since 3.0.0
     */
    @Test
    void testC_Consumer_Function() {
        List<String> items = new ArrayList<>();

        Consumer<Integer> result = c(items::addLast, Object::toString);

        result.accept(7);
        result.accept(3);

        assertThat(items).containsExactly("7", "3");
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

        List<Integer> result = LambdaUtils.narrowDown(pred).apply(src);

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

        List<String> result = LambdaUtils.convertElements(String::valueOf).apply(src);

        assertThat(result).containsExactly("3", "2", "1", "0");
    }

    /**
     * Test of f method.
     *
     * @since 3.0.0
     */
    @Test
    void testF() {
        Function<Integer, String> result
            = LambdaUtils.f(Object::toString).compose(i -> i + 1);
        assertThat(result).returns("4", f -> f.apply(3));

    }

    /**
     * Test of p method.
     *
     * @since 3.0.0
     */
    @Test
    void testP() {
        Predicate<String> result = LambdaUtils.p(LambdaUtils.p("foo"::equals), String::toLowerCase);
        assertThat(result).accepts("foo", "FOO").rejects("var");
    }

    /**
     * Test of alwaysTrue method.
     *
     * @since 3.0.0
     */
    @Test
    void testAlwaysTrue() {
        Predicate<String> result = LambdaUtils.alwaysTrue();
        assertThat(result).accepts("a", "n", "y");
    }

    /**
     * Test of toLinkedHashMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToLinkedHashMap() {
        var result = Stream.of(entry(2, "no2"), entry(1, "no1"), entry(3, "no3")).collect(LambdaUtils.toLinkedHashMap());

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
            .collect(LambdaUtils.toLinkedHashMap((v1, v2) -> v1 + v2));

        assertThat(result).containsExactly(entry(2, "no2++"), entry(1, "no1"), entry(3, "no3"));
    }

    /**
     * Test of toTreeMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToTreeMap() {
        var result = Stream.of(entry(2, "no2"), entry(1, "no1"), entry(3, "no3")).collect(LambdaUtils.toTreeMap());

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
            .collect(LambdaUtils.toTreeMap((v1, v2) -> v1 + v2));

        assertThat(result).containsExactly(entry(1, "no1"), entry(2, "no2++"), entry(3, "no3"));
    }

    /**
     * Test of toCaseInsensitiveMap method.
     *
     * @since 3.0.0
     */
    @Test
    void testToCaseInsensitiveMap() {
        var result = Stream.of(entry("A", "no2"), entry("B", "no1")).collect(LambdaUtils.toCaseInsensitiveMap());

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
            .collect(LambdaUtils.toCaseInsensitiveMap((v1, v2) -> v1 + v2));

        assertThat(result).containsKeys("a", "b").containsExactlyInAnyOrderEntriesOf(Map.of("A", "no2", "B", "no1--"));
    }

    /**
     * Test of s method.
     *
     * @since 3.0.0
     */
    @Test
    void testS() {
        Supplier<Integer> sup = () -> 5;
        Function<Integer, String> proc = i -> "[" + String.valueOf(i * 2) + "]";

        var result = LambdaUtils.s(sup, proc).get();

        assertThat(result).isEqualTo("[10]");
    }

    /**
     * Test of compute method.
     *
     * @since 3.0.0
     */
    @Test
    void testCompute() {
        var expect = Map.of("k1", 1, "k2", 2);

        var result = Map.of("k1", "1", "k2", "2").entrySet().stream().map(LambdaUtils.compute(Integer::valueOf))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(expect);
    }
}
