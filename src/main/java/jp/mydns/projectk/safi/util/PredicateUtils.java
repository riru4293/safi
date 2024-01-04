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

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utilities for {@link Predicate}.
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
public final class PredicateUtils {

    private PredicateUtils() {
    }

    /**
     * Returns a predicate received as is. This method is alias of {@link #predicate(Predicate)}.
     *
     * @param <T> the type of arguments to the specified predicate
     * @param toBeReturned predicate to be returned as is
     * @return a predicate received as is
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T> Predicate<T> p(Predicate<T> toBeReturned) {
        return predicate(toBeReturned);
    }

    /**
     * Returns a predicate received as is.
     *
     * @param <T> the type of arguments to the specified predicate
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
     * @param <T> input value type
     * @param <C> the type of arguments to the specified predicate
     * @param toBeReturned predicate for the conversion value
     * @param preConversion the function to apply before the predicate is tested
     * @return a predicate for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, C> Predicate<T> p(Predicate<C> toBeReturned, Function<T, C> preConversion) {
        return predicate(toBeReturned, preConversion);
    }

    /**
     * Returns a predicate for the result of applying the function. It is used to judge with conversion value without
     * changing the input value.
     *
     * @param <T> input value type
     * @param <C> the type of arguments to the specified predicate
     * @param toBeReturned predicate for the conversion value
     * @param preConversion the function to apply before the predicate is tested
     * @return a predicate for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, C> Predicate<T> predicate(Predicate<C> toBeReturned, Function<T, C> preConversion) {

        Objects.requireNonNull(toBeReturned);
        Objects.requireNonNull(preConversion);

        return input -> toBeReturned.test(preConversion.apply(input));

    }

    /**
     * Returns a predicate that always {@code true}.
     *
     * @param <T> input value type
     * @return a predicate that always {@code true}
     * @since 1.0.0
     */
    public static <T> Predicate<T> alwaysTrue() {
        return input -> true;
    }
}
