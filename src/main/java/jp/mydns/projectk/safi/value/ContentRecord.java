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
import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import jp.mydns.projectk.safi.constant.JobPhase;
import jp.mydns.projectk.safi.constant.RecordKind;
import jp.mydns.projectk.safi.constant.RecordValueFormat;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * A record of one processed content. It is a format for recording the value at the time when the process success or
 * failure.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <p>
 * JSON format
 * <pre><code>
 * {
 *     "$schema": "https://json-schema.org/draft/2020-12/schema",
 *     "$id": "https://project-k.mydns.jp/safi/content-record.schema.json",
 *     "title": "ContentRecord",
 *     "description": "A record of one processed content.",
 *     "type": "object",
 *     "properties": {
 *         "id": {
 *             "description": "Content id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "kind": {
 *             "description": "Record kind.",
 *             "type": "string",
 *             "enum": [
 *                 "REGISTER",
 *                 "DELETION",
 *                 "FAILURE"
 *             ]
 *         },
 *         "failurePhase": {
 *             "description": "Processing phase in which content processing failed.",
 *             "type": "string",
 *             "enum": [
 *                 "FETCHING",
 *                 "TRANSFORMATION",
 *                 "VALIDATION",
 *                 "PROVISIONING"
 *             ]
 *         },
 *         "format": {
 *             "description": "Format of record value.",
 *             "type": "string",
 *             "enum": [
 *                 "TRANSFORM_RESULT",
 *                 "IMPORTATION_USER",
 *                 "IMPORTATION_MEDIUM",
 *                 "IMPORTATION_ORG",
 *                 "IMPORTATION_BELONG_ORG",
 *                 "IMPORTATION_GROUP",
 *                 "IMPORTATION_BELONG_GROUP",
 *                 "USER",
 *                 "MEDIUM",
 *                 "ORG",
 *                 "BELONG_ORG",
 *                 "GROUP",
 *                 "BELONG_GROUP"
 *             ]
 *         },
 *         "value": {
 *             "description": "Content value.",
 *             "type": "object"
 *         },
 *         "message": {
 *             "description": "Result message.",
 *             "type": "string",
 *             "minLength": 1
 *         },
 *         "required": [
 *             "kind",
 *             "format",
 *             "value",
 *             "message"
 *         ],
 *         "allOf": [
 *             {
 *                 "if": {
 *                     "kind": {
 *                         "operation": {
 *                             "const": "FAILURE"
 *                         }
 *                     }
 *                 },
 *                 "then": {
 *                     "required": [
 *                         "failurePhase"
 *                     ]
 *                 },
 *                 "else": {
 *                     "required": [
 *                         "id"
 *                     ]
 *                 }
 *             }
 *         ]
 *     }
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(ContentRecord.Deserializer.class)
@Schema(name = "ContentRecord", description = "A record of one processed content.")
public interface ContentRecord {

    /**
     * Get content id.
     *
     * @return content id. {@code null} if not exists.
     * @since 1.0.0
     */
    @Schema(description = "Content id.")
    @Size(min = 1, max = 36, groups = {Default.class})
    String getId();

    /**
     * Get the kind of processing result.
     *
     * @return the {@code RecordKind}
     * @since 1.0.0
     */
    @Schema(description = "Recording result kind.")
    @NotNull(groups = {Default.class})
    RecordKind getKind();

    /**
     * Get the phase that failed processing.
     *
     * @return the {@code JobPhase}. It is {@code null} if processing is successful end.
     * @since 1.0.0
     */
    @Schema(description = "Failed processing phase.")
    JobPhase getFailurePhase();

    /**
     * Get the format of record value.
     *
     * @return the {@code RecordValueFormat}
     * @since 1.0.0
     */
    @Schema(description = "Format of record value.")
    @NotNull(groups = {Default.class})
    RecordValueFormat getFormat();

    /**
     * Get content value. It is a value at the time of recording in KeyValue format, and the content depends on the
     * recording value.
     *
     * @return content value
     * @since 1.0.0
     */
    @Schema(description = "Content value.")
    @NotNull(groups = {Default.class})
    JsonObject getValue();

    /**
     * Get the result message of processed content.
     *
     * @return result message
     * @since 1.0.0
     */
    @Schema(description = "Result message.")
    @NotBlank(groups = {Default.class})
    String getMessage();

    /**
     * Builder of the {@link ContentRecord}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private String id;
        private RecordKind kind;
        private JobPhase failurePhase;
        private RecordValueFormat format;
        private JsonObject value;
        private String message;

        /**
         * Constructs a new builder with all properties are {@code null}.
         *
         * @since 1.0.0
         */
        public Builder() {
        }

        /**
         * Set content id.
         *
         * @param id content id
         * @return updated this
         * @since 1.0.0
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set record kind.
         *
         * @param kind record kind
         * @return updated this
         * @since 1.0.0
         */
        public Builder withKind(RecordKind kind) {
            this.kind = kind;
            return this;
        }

        /**
         * Set job failure phase.
         *
         * @param failurePhase failure phase. If no failure, it can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withFailurePhase(JobPhase failurePhase) {
            this.failurePhase = failurePhase;
            return this;
        }

        /**
         * Set format of record value.
         *
         * @param format format of record value
         * @return updated this
         * @since 1.0.0
         */
        public Builder withFormat(RecordValueFormat format) {
            this.format = format;
            return this;
        }

        /**
         * Set content value.
         *
         * @param value content value
         * @return updated this
         */
        public Builder withValue(JsonObject value) {
            this.value = value;
            return this;
        }

        /**
         * Set result message.
         *
         * @param message result message
         * @return updated this
         */
        public Builder withMessage(String message) {
            this.message = message;
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
        public ContentRecord build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        /**
         * Implements of the {@code ContentRecord} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements ContentRecord {

            private String id;
            private RecordKind kind;
            private JobPhase failurePhase;
            private JsonObject value;
            private String message;
            private RecordValueFormat format;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 1.0.0
             */
            protected Bean() {
            }

            /**
             * Construct with set all properties from builder.
             *
             * @param builder the {@code ContentRecord.Builder}
             * @since 1.0.0
             */
            protected Bean(Builder builder) {
                this.id = builder.id;
                this.kind = builder.kind;
                this.failurePhase = builder.failurePhase;
                this.format = builder.format;
                this.value = builder.value;
                this.message = builder.message;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getId() {
                return id;
            }

            /**
             * Set content id.
             *
             * @param id content id
             * @since 1.0.0
             */
            public void setId(String id) {
                this.id = id;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public RecordKind getKind() {
                return kind;
            }

            /**
             * Set the kind of processing result.
             *
             * @param kind the {@code RecordKind}
             * @since 1.0.0
             */
            public void setKind(RecordKind kind) {
                this.kind = kind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public JobPhase getFailurePhase() {
                return failurePhase;
            }

            /**
             * Set the phase that failed processing.
             *
             * @param failurePhase the JobPhase. It is null if processing is successful end.
             * @since 1.0.0
             */
            public void setFailurePhase(JobPhase failurePhase) {
                this.failurePhase = failurePhase;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public RecordValueFormat getFormat() {
                return format;
            }

            /**
             * Set the format of processing result.
             *
             * @param format the {@code RecordValueFormat}
             * @since 1.0.0
             */
            public void setFormat(RecordValueFormat format) {
                this.format = format;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public JsonObject getValue() {
                return value;
            }

            /**
             * Set content value.
             *
             * @param value content value
             * @since 1.0.0
             */
            public void setValue(JsonObject value) {
                this.value = value;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getMessage() {
                return message;
            }

            /**
             * Set the result message of processed content.
             *
             * @param message result message
             * @since 1.0.0
             */
            public void setMessage(String message) {
                this.message = message;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "ContentRecord{" + "id=" + id + ", kind=" + kind + ", message=" + message + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code ContentRecord}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<ContentRecord> {

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
        public ContentRecord deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
