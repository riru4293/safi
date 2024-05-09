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
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.ContentKind;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * Definition for creates a {@code Job}.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <p>
 * JSON format
 * <pre><code>
 * {
 *     "$schema": "https://json-schema.org/draft/2020-12/schema",
 *     "$id": "https://project-k.mydns.jp/safi/jobdef.schema.json",
 *     "title": "Jobdef",
 *     "description": "Definition for creates a Job.",
 *     "type": "object",
 *     "properties": {
 *         "id": {
 *             "description": "Job definition id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "jobKind": {
 *             "description": "Kind of Job to create.",
 *             "type": "string",
 *             "enum": [
 *                 "IMPORT",
 *                 "EXPORT",
 *                 "REBUILD",
 *                 "ARCHIVE"
 *             ]
 *         },
 *         "contentKind": {
 *             "description": "Kind of content to process by Job.",
 *             "type": "string",
 *             "enum": [
 *                 "USER",
 *                 "MEDIUM",
 *                 "ORG1",
 *                 "ORG2",
 *                 "BELONG_ORG",
 *                 "GROUP",
 *                 "BELONG_GROUP"
 *             ]
 *         },
 *         "name": {
 *             "description": "Job definition name.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 255
 *         },
 *         "timeout": {
 *             "description": "Job execution timeout.",
 *             "type": "duration"
 *         },
 *         "plugdef": {
 *             "description": "Definition for Plug-in to loading and executing.",
 *             "$ref": "https://project-k.mydns.jp/safi/plugdef.schema.json"
 *         },
 *         "filtdef": {
 *             "description": "Definition for content narrow down.",
 *             "$ref": "https://project-k.mydns.jp/safi/filtdef.schema.json"
 *         },
 *         "trnsdef": {
 *             "description": "Definition for content transformation.",
 *             "type": "object",
 *             "patternProperties": {
 *                 "^.+$": {
 *                     "type": "string"
 *                 }
 *             }
 *         },
 *         "options": {
 *             "description": "Optional configuration at job execution.",
 *             "type": "object"
 *         },
 *         "validityPeriod": {
 *             "description": "Validity period.",
 *             "$ref": "https://project-k.mydns.jp/safi/validity-period.schema.json"
 *         },
 *         "persistenceContext": {
 *             "description": "Persistence information.",
 *             "$ref": "https://project-k.mydns.jp/safi/persistence-context.schema.json"
 *         },
 *         "note": {
 *             "description": "Note.",
 *             "type": "string"
 *         }
 *     },
 *     "required": [
 *         "id",
 *         "jobKind",
 *         "contentKind",
 *         "name",
 *         "timeout",
 *         "options",
 *         "validityPeriod"
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(Jobdef.Deserializer.class)
@Schema(name = "Jobdef", description = "Definition for creates a job.")
public interface Jobdef {

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Job definiton id.")
    String getId();

    /**
     * Get the {@code JobKind}.
     *
     * @return the {@code JobKind}
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Kind of Job to create.")
    JobKind getJobKind();

    /**
     * Get the {@code ContentKind}.
     *
     * @return the {@code ContentKind}
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Kind of content to process by Job.")
    ContentKind getContentKind();

    /**
     * Get job definition name.
     *
     * @return job definition name
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 255)
    @Schema(description = "Job definiton name.")
    String getName();

    /**
     * Get job execution timeout.
     *
     * @return job execution timeout
     * @since 1.0.0
     */
    @NotNull
    @Schema(type = "string", description = "Job execution timeout.")
    Duration getTimeout();

    /**
     * Get the {@code Plugdef}.
     *
     * @return the {@code Plugdef}
     * @since 1.0.0
     */
    @Schema(description = "Definition for loading and executing the Plugin.")
    Optional<@Valid Plugdef> getPlugdef();

    /**
     * Get the {@code Filtdef}.
     *
     * @return the {@code Filtdef}
     * @since 1.0.0
     */
    @Schema(description = "Definition for filtering the contents.")
    Optional<@Valid Filtdef> getFiltdef();

    /**
     * Get content transform definition.
     *
     * @return transform definition
     * @since 1.0.0
     */
    @Schema(description = "Definition for content transformation.")
    Optional<Map<String, @NotNull String>> getTrnsdef();

    /**
     * Get optional configuration at job execution.
     *
     * @return optional configuration
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Optional configuration at job execution.")
    JsonObject getOptions();

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    @NotNull
    @Valid
    @Schema(description = "Validity period.")
    ValidityPeriod getValidityPeriod();

    /**
     * Get the {@code PersistenceContext}.
     *
     * @return the {@code PersistenceContext}
     * @since 1.0.0
     */
    @Schema(description = "Persistence information.")
    Optional<@Valid PersistenceContext> getPersistenceContext();

    /**
     * Get note.
     *
     * @return note. It may be {@code null}.
     * @since 1.0.0
     */
    @Schema(description = "Note.")
    String getNote();

    /**
     * Builder of the {@link Jobdef}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private String id;
        private JobKind jobKind;
        private ContentKind contentKind;
        private String name;
        private Duration timeout;
        private Plugdef plugdef;
        private Filtdef filtdef;
        private Map<String, String> trnsdef;
        private JsonObject options;
        private ValidityPeriod validityPeriod;
        private PersistenceContext persistenceContext;
        private String note;

        /**
         * Constructs a new builder with all properties are {@code null}.
         *
         * @since 1.0.0
         */
        public Builder() {
        }

        /**
         * Constructs a new builder with set all properties by copying them from other value.
         *
         * @param src source value
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 1.0.0
         */
        public Builder(Jobdef src) {
            Objects.requireNonNull(src);

            this.id = src.getId();
            this.jobKind = src.getJobKind();
            this.contentKind = src.getContentKind();
            this.name = src.getName();
            this.timeout = src.getTimeout();
            src.getPlugdef().ifPresent(v -> this.plugdef = v);
            src.getFiltdef().ifPresent(v -> this.filtdef = v);
            src.getTrnsdef().ifPresent(v -> this.trnsdef = v);
            this.options = src.getOptions();
            this.validityPeriod = src.getValidityPeriod();
            src.getPersistenceContext().ifPresent(v -> this.persistenceContext = v);
            this.note = src.getNote();
        }

        /**
         * Set job definition id.
         *
         * @param id job definition id
         * @return updated this
         * @since 1.0.0
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set the {@code JobKind}.
         *
         * @param jobKind the {@code JobKind}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withJobKind(JobKind jobKind) {
            this.jobKind = jobKind;
            return this;
        }

        /**
         * Set the {@code ContentKind}.
         *
         * @param contentKind the {@code ContentKind}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withContentKind(ContentKind contentKind) {
            this.contentKind = contentKind;
            return this;
        }

        /**
         * Set job definition name.
         *
         * @param name job definition name
         * @return updated this
         * @since 1.0.0
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set job execution timeout.
         *
         * @param timeout job execution timeout
         * @return updated this
         * @since 1.0.0
         */
        public Builder withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Set the {@code Plugdef}.
         *
         * @param plugdef the {@code Plugdef}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withPlugdef(Plugdef plugdef) {
            this.plugdef = plugdef;
            return this;
        }

        /**
         * Set filtering setting.
         *
         * @param filtdef filtering setting
         * @return updated this
         * @since 1.0.0
         */
        public Builder withFiltdef(Filtdef filtdef) {
            this.filtdef = filtdef;
            return this;
        }

        /**
         * Set transform definition.
         *
         * @param trnsdef transform definition
         * @return updated this
         * @since 1.0.0
         */
        public Builder withTrnsdef(Map<String, String> trnsdef) {
            this.trnsdef = trnsdef;
            return this;
        }

        /**
         * Set optional configuration at job execution.
         *
         * @param options optional configuration
         * @return updated this
         * @since 1.0.0
         */
        public Builder withOptions(JsonObject options) {
            this.options = options;
            return this;
        }

        /**
         * Set the {@code ValidityPeriod}.
         *
         * @param validityPeriod the {@code ValidityPeriod}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withValidityPeriod(ValidityPeriod validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        /**
         * Set the {@code PersistenceContext}.
         *
         * @param persistenceContext the {@code PersistenceContext}. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withPersistenceContext(PersistenceContext persistenceContext) {
            this.persistenceContext = persistenceContext;
            return this;
        }

        /**
         * Set note.
         *
         * @param note note. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withNote(String note) {
            this.note = note;
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
        public Jobdef build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        /**
         * Implements of the {@code Jobdef} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements Jobdef {

            private String id;
            private JobKind jobKind;
            private ContentKind contentKind;
            private String name;
            private Duration timeout;
            private Plugdef plugdef;
            private Filtdef filtdef;
            private Map<String, String> trnsdef;
            private JsonObject options;
            private ValidityPeriod validityPeriod;
            private PersistenceContext persistenceContext;
            private String note;

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
             * @param builder the {@code Jobdef.Builder}
             * @throws NullPointerException if {@code builder} is {@code null}
             * @since 1.0.0
             */
            protected Bean(Builder builder) {
                Objects.requireNonNull(builder);

                this.id = builder.id;
                this.jobKind = builder.jobKind;
                this.contentKind = builder.contentKind;
                this.name = builder.name;
                this.timeout = builder.timeout;
                this.plugdef = builder.plugdef;
                this.filtdef = builder.filtdef;
                this.trnsdef = builder.trnsdef;
                this.options = builder.options;
                this.validityPeriod = builder.validityPeriod;
                this.persistenceContext = builder.persistenceContext;
                this.note = builder.note;
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
             * Set job definition id.
             *
             * @param id job definition id
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
            public JobKind getJobKind() {
                return jobKind;
            }

            /**
             * Set the {@code JobKind}.
             *
             * @param jobKind the {@code JobKind}
             * @since 1.0.0
             */
            public void setJobKind(JobKind jobKind) {
                this.jobKind = jobKind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public ContentKind getContentKind() {
                return contentKind;
            }

            /**
             * Set the {@code ContentKind}.
             *
             * @param contentKind the {@code ContentKind}
             * @since 1.0.0
             */
            public void setContentKind(ContentKind contentKind) {
                this.contentKind = contentKind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getName() {
                return name;
            }

            /**
             * Set job definition name.
             *
             * @param name job definition name
             * @since 1.0.0
             */
            public void setName(String name) {
                this.name = name;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Duration getTimeout() {
                return timeout;
            }

            /**
             * Set job execution timeout.
             *
             * @param timeout job execution timeout
             * @since 1.0.0
             */
            public void setTimeout(Duration timeout) {
                this.timeout = timeout;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<Plugdef> getPlugdef() {
                return Optional.ofNullable(plugdef);
            }

            /**
             * Set the {@code Plugdef}.
             *
             * @param plugdef the {@code Plugdef}
             * @since 1.0.0
             */
            public void setPlugdef(Plugdef plugdef) {
                this.plugdef = plugdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<Filtdef> getFiltdef() {
                return Optional.ofNullable(filtdef);
            }

            /**
             * Set filtering setting.
             *
             * @param filtdef filtering setting
             * @since 1.0.0
             */
            public void setFiltdef(Filtdef filtdef) {
                this.filtdef = filtdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<Map<String, String>> getTrnsdef() {
                return Optional.ofNullable(trnsdef);
            }

            /**
             * Set transform definition.
             *
             * @param trnsdef transform definition
             * @since 1.0.0
             */
            public void setTrnsdef(Map<String, String> trnsdef) {
                this.trnsdef = trnsdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public JsonObject getOptions() {
                return options;
            }

            /**
             * Set optional configuration at job execution.
             *
             * @param options optional configuration at job execution
             * @since 1.0.0
             */
            public void setOptions(JsonObject options) {
                this.options = options;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public ValidityPeriod getValidityPeriod() {
                return validityPeriod;
            }

            /**
             * Set the {@code ValidityPeriod}.
             *
             * @param validityPeriod the {@code ValidityPeriod}
             * @since 1.0.0
             */
            public void setValidityPeriod(ValidityPeriod validityPeriod) {
                this.validityPeriod = validityPeriod;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<PersistenceContext> getPersistenceContext() {
                return Optional.ofNullable(persistenceContext);
            }

            /**
             * Set the {@code PersistenceContext}.
             *
             * @param persistenceContext the {@code PersistenceContext}. It can be set {@code null}.
             * @since 1.0.0
             */
            public void setPersistenceContext(PersistenceContext persistenceContext) {
                this.persistenceContext = persistenceContext;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getNote() {
                return note;
            }

            /**
             * Set note.
             *
             * @param note note. It can be set {@code null}.
             * @since 1.0.0
             */
            public void setNote(String note) {
                this.note = note;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "Jobdef{" + "id=" + id + ", jobKind=" + jobKind + ", contentKind=" + contentKind + ", name=" + name + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code Jobdef}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<Jobdef> {

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
        public Jobdef deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
