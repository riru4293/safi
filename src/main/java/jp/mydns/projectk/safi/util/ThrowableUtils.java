/*
 * Copyright (c) 2022, Project-K
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

import java.util.AbstractMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import static java.util.function.Predicate.not;
import java.util.stream.Stream;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;

/**
 * Utilities for {@link Throwable}.
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
public final class ThrowableUtils {

    private ThrowableUtils() {
    }

    /**
     * Extract message from the {@code Throwable}.
     *
     * @param t the {@code Throwable}
     * @return the key is the class name of {@code Throwable}, the value is message of {@code Throwable}.
     * @throws NullPointerException if {@code t} is {@code null}
     * @since 1.0.0
     */
    @SuppressWarnings("ThrowableResultIgnored")
    public static Entry<String, String> toMessageEntry(Throwable t) {
        return new AbstractMap.SimpleImmutableEntry<>(t.getClass().getSimpleName(), t.getMessage());
    }

    /**
     * Extract messages from the {@code Throwable}. Also extract nested {@code Throwable} messages. Omit message if
     * blank.
     *
     * @param t an any {@code Throwable}
     * @return the key is the class name of {@code Throwable}, the value is message of {@code Throwable}.
     * @throws NullPointerException if {@code t} is {@code null}
     * @since 1.0.0
     */
    @SuppressWarnings("ThrowableResultIgnored")
    public static List<Entry<String, String>> toMessageEntries(Throwable t) {
        return Stream.iterate(t, Throwable::getCause)
                .takeWhile(Objects::nonNull).filter(p(Objects::nonNull, Throwable::getMessage))
                .filter(p(not(String::isBlank), Throwable::getMessage))
                .map(ThrowableUtils::toMessageEntry)
                .toList();
    }
}
