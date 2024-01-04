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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Utilities for {@link Function}.
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
public final class FunctionUtils {

    private FunctionUtils() {
    }

    /**
     * Test the list values with a predicate to narrow it down.
     *
     * @param <T> the type of arguments to the specified predicate
     * @param p the predicate
     * @return narrowed list by predicate
     * @throws NullPointerException if {@code p} is {@code null}
     * @since 1.0.0
     */
    public static <T> UnaryOperator<List<T>> testOfList(Predicate<T> p) {
        return l -> l.stream().filter(p).toList();
    }

    /**
     * Apply the function to list values.
     *
     * @param <T> input value type of function
     * @param <R> output value type of function
     * @param f the function
     * @return list that applied the function
     * @throws NullPointerException if {@code f} is {@code null}
     * @since 1.0.0
     */
    public static <T, R> Function<List<T>, List<R>> applyToList(Function<T, R> f) {
        return l -> l.stream().map(f).toList();
    }

    /**
     * Returns a function received as is. This method is alias of {@link #function(Function)}.
     *
     * @param <T> input value type of function
     * @param <R> output value type of function
     * @param toBeReturned function to be returned as is
     * @return a function received as is.
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T, R> Function<T, R> f(Function<T, R> toBeReturned) {
        return function(toBeReturned);
    }

    /**
     * Returns a function received as is.
     *
     * @param <T> input value type of function
     * @param <R> output value type of function
     * @param toBeReturned function to be returned as is
     * @return a function received as is.
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T, R> Function<T, R> function(Function<T, R> toBeReturned) {
        return Objects.requireNonNull(toBeReturned);
    }
}
