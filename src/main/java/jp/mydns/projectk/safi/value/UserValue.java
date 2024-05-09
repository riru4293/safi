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
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.RecordValueFormat;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * <i>ID-Content</i> of the <i>User</i> that represents one user. All other <i>ID-Content</i> are directly or indirectly
 * related to <i>User</i>, and <i>User</i> is the base point of the relationship. It is persisted via
 * {@link UserEntity}.
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
 *     "$id": "https://project-k.mydns.jp/safi/user-content.schema.json",
 *     "title": "UserContent",
 *     "description": "ID-Content of the User.",
 *     "type": "object",
 *     "properties": {
 *         "id": {
 *             "description": "User id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "enabled": {
 *             "description": "Validity state.",
 *             "type": "boolean"
 *         },
 *         "name": {
 *             "description": "User name.",
 *             "type": "string",
 *             "maxLength": 255
 *         },
 *         "digest": {
 *             "description": "Digest value.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 128
 *         },
 *         "attributes": {
 *             "description": "Attribute values.",
 *             "type": "object",
 *             "properties": {
 *                 "att01": {
 *                     "description": "Attribute value #1.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att02": {
 *                     "description": "Attribute value #2.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att03": {
 *                     "description": "Attribute value #3.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att04": {
 *                     "description": "Attribute value #4.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att05": {
 *                     "description": "Attribute value #5.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att06": {
 *                     "description": "Attribute value #6.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att07": {
 *                     "description": "Attribute value #7.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att08": {
 *                     "description": "Attribute value #8.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att09": {
 *                     "description": "Attribute value #9.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 },
 *                 "att10": {
 *                     "description": "Attribute value #10.",
 *                     "type": "string",
 *                     "maxLength": 255
 *                 }
 *             },
 *             "validityPeriod": {
 *                 "description": "Validity period.",
 *                 "$ref": "https://project-k.mydns.jp/safi/validity-period.schema.json"
 *             },
 *             "persistenceContext": {
 *                 "description": "Persistence information.",
 *                 "$ref": "https://project-k.mydns.jp/safi/persistence-context.schema.json"
 *             },
 *             "note": {
 *                 "description": "Note for this entity.",
 *                 "type": "string"
 *             }
 *         },
 *         "required": [
 *             "id",
 *             "enabled",
 *             "attributes",
 *             "validityPeriod"
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
public interface UserValue extends ContentValue<UserValue> {

    /**
     * Get paired entity. The pairs here mean the same content that has already been persisted.
     *
     * @return paired entity
     * @since 1.0.0
     */
    @JsonbTransient
    Optional<UserEntity> getEntity();

    /**
     * Builder of the {@link UserValue}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder extends ContentValue.AbstractBuilder<Builder, UserValue> {

        private final DigestGenerator digestGenerator;
        private UserEntity entity;

        /**
         * Constructs a new builder with all properties are {@code null}.
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
         * Constructs a new builder with set all properties by copying them from other value.
         *
         * @param src source value
         * @param digestGenerator the {@code DigestGenerator}
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        public Builder(UserValue src, DigestGenerator digestGenerator) {
            this(digestGenerator);
            Objects.requireNonNull(src).getEntity().ifPresent(v -> this.entity = v);
        }

        /**
         * Set the paired entity.
         *
         * @param entity paired entity
         * @return updated this
         * @since 1.0.0
         */
        public Builder withEntity(UserEntity entity) {
            this.entity = entity;
            return this;
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
        public UserValue build(Validator validator, Class<?>... groups) {
            final String digest = digestGenerator.generate(id, enabled, name, atts, validityPeriod);
            return ValidationUtils.requireValid(new Bean(this, digest), validator, groups);
        }

        /**
         * Implements of the {@code UserValue} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean extends ContentValue.AbstractBuilder.AbstractBean<UserValue> implements UserValue {

            private UserEntity entity;

            /**
             * Constructor just for JSON deserialization.
             *
             * @since 1.0.0
             */
            protected Bean() {
            }

            /**
             * Construct with set all properties from builder.
             *
             * @param builder the {@code UserValue.Builder}
             * @param digest digest value. It must be provided by {@code builder}.
             * @throws NullPointerException if any argument is {@code null}
             * @since 1.0.0
             */
            protected Bean(UserValue.Builder builder, String digest) {
                super(builder, digest);
                this.entity = builder.entity;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<UserEntity> getEntity() {
                return Optional.ofNullable(entity);
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Map.Entry<String, ContentValue<UserValue>> asEntry() {
                return new AbstractMap.SimpleImmutableEntry<>(id, this);
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public RecordValueFormat getFormat() {
                return RecordValueFormat.USER;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "User{" + "id=" + id + ", enabled=" + enabled + ", name=" + name + ", digest=" + digest + '}';
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
         * Construct a new JSON deserializer.
         *
         * @since 1.0.0
         */
        public Deserializer() {
        }

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
