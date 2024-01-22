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
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * <i>ID-Content</i> of the <i>User</i> that represents one user.
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
 * JSON format
 * <pre><code>
 * {
 *     "$schema": "https://json-schema.org/draft/2020-12/schema",
 *     "$id": "https://project-k.mydns.jp/user-content.schema.json",
 *     "title": "UserContent",
 *     "description": "ID-Content of the User",
 *     "type": "object",
 *     "properties": {
 *         "id": {
 *             "description": "User id",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "enabled": {
 *             "description": "Validity state flag",
 *             "type": "boolean"
 *         },
 *         "name": {
 *             "description": "User name",
 *             "type": "string",
 *             "maxLength": 100
 *         },
 *         "attributes": {
 *             "description": "Attribute collection",
 *             "type": "object",
 *             "properties": {
 *                 "att01": {
 *                     "description": "Attribute #2",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att02": {
 *                     "description": "Attribute #1",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att03": {
 *                     "description": "Attribute #3",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att04": {
 *                     "description": "Attribute #4",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att05": {
 *                     "description": "Attribute #5",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att06": {
 *                     "description": "Attribute #6",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att07": {
 *                     "description": "Attribute #7",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att08": {
 *                     "description": "Attribute #8",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att09": {
 *                     "description": "Attribute #9",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 },
 *                 "att10": {
 *                     "description": "Attribute #10",
 *                     "type": "string",
 *                     "maxLength": 200
 *                 }
 *             },
 *             "validityPeriod": {
 *                 "description": "Validity period",
 *                 "$ref": "https://project-k.mydns.jp/validity-period.schema.json"
 *             },
 *             "digest": {
 *                 "description": "Digest value",
 *                 "type": "string",
 *                 "minLength": 1,
 *                 "maxLength": 128
 *             },
 *             "note": {
 *                 "description": "Note for this content",
 *                 "type": "string"
 *             },
 *             "version": {
 *                 "description": "Content version stored in database. 0 if not yet stored.",
 *                 "type": "integer",
 *                 "minimum": 0
 *             }
 *         },
 *         "required": [
 *             "id",
 *             "enabled",
 *             "attributes",
 *             "validityPeriod",
 *             "version"
 *         ]
 *     }
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Schema(name = "UserValue", description = "ID-Content of the User.")
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

        private final DigestGenerator digestGenerator;

        /**
         * Constructor.
         *
         * @param digestGenerator the {@code DigestGenerator}
         * @throws NullPointerException if {@code digestGenerator} is {@code null}
         * @since 1.0.0
         */
        public Builder(DigestGenerator digestGenerator) {
            super(Builder.class);
            this.digestGenerator = Objects.requireNonNull(digestGenerator);
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
            final String digest = digestGenerator.generate(id, enabled, name, atts, validityPeriod);
            return ValidationUtils.requireValid(new Bean(this, digest), validator, groups);
        }

        /**
         * Implements of the {@code UserValue}.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean extends ContentValue.AbstractBuilder.AbstractBean implements UserValue {

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 1.0.0
             */
            protected Bean() {
            }

            /**
             * Constructor.
             *
             * @param builder the {@code UserValue.Builder}
             * @param digest digest value. It must be provided by {@code builder}.
             * @since 1.0.0
             */
            protected Bean(UserValue.Builder builder, String digest) {
                super(builder, digest);
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
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

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public UserValue deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
