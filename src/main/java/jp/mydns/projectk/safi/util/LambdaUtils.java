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

import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Utilities for Java lambda expressions. Provides frequent processing that when using lambda expressions.
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
public final class LambdaUtils {

    private LambdaUtils() {
    }

    /**
     * Returns a binary operator that always returns a first item.
     *
     * @param <T> value type
     * @return binary operator that always returns a first item
     * @since 1.0.0
     */
    public static <T> BinaryOperator<T> firstWins() {
        return (first, last) -> first;
    }

    /**
     * Returns a binary operator that always returns a last item.
     *
     * @param <T> value type
     * @return binary operator that always returns a last item
     * @since 1.0.0
     */
    public static <T> BinaryOperator<T> lastWins() {
        return (first, last) -> last;
    }

    /**
     * Returns a binary operator that always throw {@link IllegalArgumentException}. Used when stream collection does
     * not allow duplication.
     *
     * @param <T> value type
     * @return binary operator that always throw {@link IllegalArgumentException}
     * @since 1.0.0
     */
    public static <T> BinaryOperator<T> alwaysThrow() {
        return (first, last) -> {
            throw new IllegalArgumentException("Duplicate values are not allowed.");
        };
    }

    /**
     * Returns a consumer received as is. Use when you want to chain consumers. This method is alias of
     * {@link #consumer(Consumer)}.
     *
     * @param <T> value type
     * @param toBeReturned consumer to be returned as is
     * @return a consumer received as is
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T> Consumer<T> c(Consumer<T> toBeReturned) {
        return consumer(toBeReturned);
    }

    /**
     * Returns a consumer received as is. Use when you want to chain consumers.
     *
     * @param <T> value type
     * @param toBeReturned consumer to be returned as is
     * @return a consumer received as is
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T> Consumer<T> consumer(Consumer<T> toBeReturned) {
        return Objects.requireNonNull(toBeReturned);
    }

    /**
     * Returns a consumer for the result of applying the function. This method is alias of
     * {@link #consumer(Consumer, Function)}.
     *
     * @param <T> value type
     * @param <M> intermediate value type
     * @param consumer consumer that after converted by function
     * @param preConversion conversion function that before consume
     * @return a consumer for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, M> Consumer<T> c(Consumer<M> consumer, Function<T, M> preConversion) {
        return consumer(consumer, preConversion);
    }

    /**
     * Returns a consumer for the result of applying the function.
     *
     * @param <T> value type
     * @param <M> intermediate value type
     * @param consumer consumer that after converted by function
     * @param preConversion conversion function that before consume
     * @return a consumer for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, M> Consumer<T> consumer(Consumer<M> consumer, Function<T, M> preConversion) {

        Objects.requireNonNull(consumer);
        Objects.requireNonNull(preConversion);

        return v -> consumer.accept(preConversion.apply(v));
    }

    /**
     * Returns a function that test the list values with a predicate to narrow it down.
     *
     * @param <T> element type of list
     * @param predicate the {@code Predicate}
     * @return function that returns a narrowed list
     * @throws NullPointerException if {@code p} is {@code null}
     * @since 1.0.0
     */
    public static <T> UnaryOperator<List<T>> narrowDown(Predicate<T> predicate) {
        return l -> l.stream().filter(predicate).toList();
    }

    /**
     * Returns a function that convert list elements.
     *
     * @param <B> element type that before conversion
     * @param <A> element type that after conversion
     * @param converter function that convert list elements
     * @return function that convert list elements
     * @throws NullPointerException if {@code converter} is {@code null}
     * @since 1.0.0
     */
    public static <B, A> Function<List<B>, List<A>> convertElements(Function<B, A> converter) {

        Objects.requireNonNull(converter);

        return l -> l.stream().map(converter).toList();

    }

    /**
     * Returns a function received as is. Use when you want to chain functions. This method is alias of
     * {@link #function(Function)}.
     *
     * @param <I> input value type
     * @param <O> output value type
     * @param toBeReturned function to be returned as is
     * @return a function received as is
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <I, O> Function<I, O> f(Function<I, O> toBeReturned) {
        return function(toBeReturned);
    }

    /**
     * Returns a function received as is. Use when you want to chain functions.
     *
     * @param <I> input value type
     * @param <O> output value type
     * @param toBeReturned function to be returned as is
     * @return a function received as is
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <I, O> Function<I, O> function(Function<I, O> toBeReturned) {
        return Objects.requireNonNull(toBeReturned);
    }

    /**
     * Returns a predicate received as is. Use when you want to chain predicates. This method is alias of
     * {@link #predicate(Predicate)}.
     *
     * @param <T> value type
     * @param toBeReturned predicate to be returned as is
     * @return a predicate received as is
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T> Predicate<T> p(Predicate<T> toBeReturned) {
        return predicate(toBeReturned);
    }

    /**
     * Returns a predicate received as is. Use when you want to chain predicates.
     *
     * @param <T> value type
     * @param toBeReturned predicate to be returned as is
     * @return a predicate received as is
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T> Predicate<T> predicate(Predicate<T> toBeReturned) {
        return Objects.requireNonNull(toBeReturned);
    }

    /**
     * Returns a predicate for the result of applying the function. It is used to judge with processed value without
     * changing the original value. This method is alias of {@link #predicate(Predicate, Function)}.
     *
     * @param <T> value type
     * @param <M> intermediate value type
     * @param predicate predicate for converted value
     * @param preConversion conversion function that before predicate is tested
     * @return a predicate for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, M> Predicate<T> p(Predicate<M> predicate, Function<T, M> preConversion) {
        return predicate(predicate, preConversion);
    }

    /**
     * Returns a predicate for the result of applying the function. It is used to judge with conversion value without
     * changing the input value.
     *
     * @param <T> value type
     * @param <M> intermediate value type
     * @param predicate predicate for converted value
     * @param preConversion conversion function that before predicate is tested
     * @return a predicate for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, M> Predicate<T> predicate(Predicate<M> predicate, Function<T, M> preConversion) {

        Objects.requireNonNull(predicate);
        Objects.requireNonNull(preConversion);

        return v -> predicate.test(preConversion.apply(v));

    }

    /**
     * Returns a predicate that always {@code true}.
     *
     * @param <T> value type
     * @return a predicate that always {@code true}
     * @since 1.0.0
     */
    public static <T> Predicate<T> alwaysTrue() {
        return v -> true;
    }
}
