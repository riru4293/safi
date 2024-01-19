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
package jp.mydns.projectk.safi.constant;

import java.util.Map;
import java.util.Optional;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.value.ContentValue;

/**
 * Attribute key definitions. The {@code attribute} is a key-value format element of {@link ContentValue} whose key is
 * {@code attribute-key}. Here we define its key.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public enum AttKey {
    /**
     * Attribute 1.
     *
     * @since 1.0.0
     */
    ATT01("att01"),
    /**
     * Attribute 2.
     *
     * @since 1.0.0
     */
    ATT02("att02"),
    /**
     * Attribute 3.
     *
     * @since 1.0.0
     */
    ATT03("att03"),
    /**
     * Attribute 4.
     *
     * @since 1.0.0
     */
    ATT04("att04"),
    /**
     * Attribute 5.
     *
     * @since 1.0.0
     */
    ATT05("att05"),
    /**
     * Attribute 6.
     *
     * @since 1.0.0
     */
    ATT06("att06"),
    /**
     * Attribute 7.
     *
     * @since 1.0.0
     */
    ATT07("att07"),
    /**
     * Attribute 8.
     *
     * @since 1.0.0
     */
    ATT08("att08"),
    /**
     * Attribute 9.
     *
     * @since 1.0.0
     */
    ATT09("att09"),
    /**
     * Attribute 10.
     *
     * @since 1.0.0
     */
    ATT10("att10");

    private final String keyName;

    private AttKey(String keyName) {
        this.keyName = keyName;
    }

    /**
     * Get attribute key name. Attribute key names are used as serialized values when serializing to JSON etc.
     *
     * @return attribute key name
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return keyName;
    }

    private static final Map<String, AttKey> reverses
            = Stream.of(AttKey.values()).collect(toUnmodifiableMap(AttKey::toString, identity()));

    /**
     * Resolve from attribute key name.
     *
     * @param keyName attribute key name
     * @return the {@code AttKey}
     * @throws IllegalArgumentException if {@code name} is unexpected
     * @since 1.0.0
     */
    public static AttKey of(String keyName) {
        return Optional.ofNullable(reverses.get(keyName)).orElseThrow(
                () -> new IllegalArgumentException("Incorrect attribute key name. [%s]".formatted(keyName)));
    }
}
