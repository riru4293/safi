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
package jp.mydns.projectk.safi.value;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * {@code User} is represents one user. It is main-content in this application.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class can be converted to JSON Object.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <p>
 * JSON Format<pre><code>
 * {
 *     "id": "user-id",
 *     "enabled": true,
 *     "name": "user-name",
 *     "attributes": {
 *         "att01": "att01-value",
 *         "att02": "att02-value",
 *         "att03": "att03-value",
 *         "att04": "att04-value",
 *         "att05": "att05-value",
 *         "att06": "att06-value",
 *         "att07": "att07-value",
 *         "att08": "att08-value",
 *         "att09": "att09-value",
 *         "att10": "att10-value"
 *     },
 *     "validityPeriod": {
 *         "from": "2000-01-01T00:00:00Z",
 *         "to": "2999-12-31T23:59:59Z",
 *         "ban": false
 *     },
 *     "digest": "digest-value",
 *     "note": "description",
 *     "version": 3
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Schema(name = "User", description = "'User' is represents one user. It is main-content in this application.")
@JsonbTypeDeserializer(UserValue.Deserializer.class)
public interface UserValue extends ContentValue {

    /**
     * Builder of the {@link UserValue}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder extends ContentValue.AbstractBuilder<Builder, UserValue> {

        private final Function<Stream<Object>, String> digestGen;

        /**
         * Constructor.
         *
         * @param digestGen digest value generator
         * @throws NullPointerException if {@code digestGen} is {@code null}
         * @since 1.0.0
         */
        public Builder(Function<Stream<Object>, String> digestGen) {
            super(Builder.class);
            this.digestGen = Objects.requireNonNull(digestGen);
        }

        /**
         * Build a new inspected instance.
         *
         * @param validator the {@code Validator}
         * @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
         * @return new inspected instance
         * @throws NullPointerException if any argument is {@code null}
         * @throws ConstraintViolationException if occurred constraint violations when building
         * @since 1.0.0
         */
        @Override
        public UserValue build(Validator validator, Class<?>... groups) {
            String digest = digestGen.apply(Stream.of(id, enabled, name, atts, validityPeriod));
            return ValidationUtils.requireValid(new Bean(this, digest), validator, groups);
        }

        protected static class Bean extends ContentValue.AbstractBuilder.AbstractBean implements UserValue {

            protected Bean() {
            }

            protected Bean(UserValue.Builder builder, String digest) {
                super(builder, digest);
            }

            @Override
            public String toString() {
                return "User{" + "id=" + id + ", enabled=" + enabled + ", name=" + name + ", atts=" + atts
                        + ", validityPeriod=" + validityPeriod + ", digest=" + digest + ", version=" + version + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code UserValue}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<UserValue> {

        @Override
        public UserValue deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
