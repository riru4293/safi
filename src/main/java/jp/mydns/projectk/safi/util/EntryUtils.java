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

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Utilities for {@link Map.Entry}.
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
public final class EntryUtils {

    private EntryUtils() {
    }

    /**
     * Compute the value of {@code Entry} using the specified function and return the result as new {@code Entry}. The
     * original value is used for the key.
     *
     * @param <K> entry key type
     * @param <I> input entry value type
     * @param <O> output entry value type
     * @param f computation function
     * @return a new immutable entry using the computation result as a value
     * <p>
     * Usage example<pre>
     * {@code // values to upper case
     * Map<String, String> preMap = Map.of("k1", "v1", "k2", "v2");
     * Map<String, String> postMap = preMap.entrySet().stream()
     *     .map(compute(String::toUpperCase))
     *     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
     *
     * // Results are
     * //   preMap  -> {"key-1", "val-1", "key-2", "val-2"}
     * //   postMap -> {"key-1", "VAL-1", "key-2", "VAL-2"}
     * }
     * </pre>
     *
     * @throws NullPointerException if {@code f} is {@code null}
     * @since 1.0.0
     */
    public static <K, I, O> Function<Map.Entry<K, I>, Map.Entry<K, O>> compute(Function<I, O> f) {
        Objects.requireNonNull(f);
        return e -> Map.entry(e.getKey(), f.apply(e.getValue()));
    }
}
