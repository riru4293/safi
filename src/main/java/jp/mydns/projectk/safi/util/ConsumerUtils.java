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
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utilities for {@link Consumer}.
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
public final class ConsumerUtils {

    private ConsumerUtils() {
    }

    /**
     * Returns a consumer received as is. This method is alias of {@link #consumer(Consumer)}.
     *
     * @param <T> input value type of consumer
     * @param toBeReturned consumer to be returned as is
     * @return a consumer received as is.
     * @throws NullPointerException if {@code toBeReturned} is {@code null}
     * @since 1.0.0
     */
    public static <T> Consumer<T> c(Consumer<T> toBeReturned) {
        return consumer(toBeReturned);
    }

    /**
     * Returns a consumer received as is.
     *
     * @param <T> input value type of consumer
     * @param toBeReturned consumer to be returned as is
     * @return a consumer received as is.
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
     * @param <T> input value type of consumer
     * @param <C> the type of arguments to the specified consumer
     * @param toBeReturned consumer to be returned as is
     * @param preConversion the function to apply before consume
     * @return a consumer for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, C> Consumer<T> c(Consumer<C> toBeReturned, Function<T, C> preConversion) {
        return consumer(toBeReturned, preConversion);
    }

    /**
     * Returns a consumer for the result of applying the function.
     *
     * @param <T> input value type of consumer
     * @param <C> the type of arguments to the specified consumer
     * @param toBeReturned consumer to be returned as is
     * @param preConversion the function to apply before consume
     * @return a consumer for the result of applying the function
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public static <T, C> Consumer<T> consumer(Consumer<C> toBeReturned, Function<T, C> preConversion) {

        Objects.requireNonNull(toBeReturned);
        Objects.requireNonNull(preConversion);

        return input -> toBeReturned.accept(preConversion.apply(input));
    }
}
