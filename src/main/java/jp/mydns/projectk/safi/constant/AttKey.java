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
 * Definitions of the <i>Attribute-Key</i>. The <i>Attribute</i> is a key-value format element of the
 * {@link ContentValue} whose key is <i>Attribute-Key</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 * @see ContentValue#getAtts() <i>Attribute</i> is explained in {@code ContentValue#getAtts()}
 */
public enum AttKey {
    /**
     * <i>Attribute-Key</i> #1. Serializing this value results in {@code att01}.
     *
     * @since 1.0.0
     */
    ATT01("att01"),
    /**
     * <i>Attribute-Key</i> #2. Serializing this value results in {@code att02}.
     *
     * @since 1.0.0
     */
    ATT02("att02"),
    /**
     * <i>Attribute-Key</i> #3. Serializing this value results in {@code att03}.
     *
     * @since 1.0.0
     */
    ATT03("att03"),
    /**
     * <i>Attribute-Key</i> #4. Serializing this value results in {@code att04}.
     *
     * @since 1.0.0
     */
    ATT04("att04"),
    /**
     * <i>Attribute-Key</i> #5. Serializing this value results in {@code att05}.
     *
     * @since 1.0.0
     */
    ATT05("att05"),
    /**
     * <i>Attribute-Key</i> #6. Serializing this value results in {@code att06}.
     *
     * @since 1.0.0
     */
    ATT06("att06"),
    /**
     * <i>Attribute-Key</i> #7. Serializing this value results in {@code att07}.
     *
     * @since 1.0.0
     */
    ATT07("att07"),
    /**
     * <i>Attribute-Key</i> #8. Serializing this value results in {@code att08}.
     *
     * @since 1.0.0
     */
    ATT08("att08"),
    /**
     * <i>Attribute-Key</i> #9. Serializing this value results in {@code att09}.
     *
     * @since 1.0.0
     */
    ATT09("att09"),
    /**
     * <i>Attribute-Key</i> #10. Serializing this value results in {@code att10}.
     *
     * @since 1.0.0
     */
    ATT10("att10");

    private final String keyName;

    private AttKey(String keyName) {
        this.keyName = keyName;
    }

    /**
     * Get <i>Attribute-Key</i> name. It is the serialized result of {@code AttKey} when it is serialized to JSON, etc.
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
     * Resolve the {@code AttKey} from <i>Attribute-Key</i> name.
     *
     * @param keyName <i>Attribute-Key</i> name
     * @return the {@code AttKey}
     * @throws IllegalArgumentException if {@code keyName} is wrong
     * @since 1.0.0
     */
    public static AttKey of(String keyName) {
        return Optional.ofNullable(reverses.get(keyName)).orElseThrow(
                () -> new IllegalArgumentException("Specified name is wrong as Attribute-Key. [%s]".formatted(keyName)));
    }
}
