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
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Map;
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
 *         "validityPeriod": {
 *             "description": "Validity period.",
 *             "$ref": "https://project-k.mydns.jp/safi/validity-period.schema.json"
 *         },
 *         "jobKind": {
 *             "description": "Job kind.",
 *             "type": "string",
 *             "enum": [
 *                 "IMPORT",
 *                 "EXPORT",
 *                 "REBUILD",
 *                 "ARCHIVE"
 *             ]
 *         },
 *         "contentKind": {
 *             "description": "Content kind.",
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
 *             "maxLength": 100
 *         },
 *         "timeout": {
 *             "description": "Job execution timeout.",
 *             "type": "duration"
 *         },
 *         "plugdef": {
 *             "description": "An information for loading and executing the Plug-in.",
 *             "$ref": "https://project-k.mydns.jp/safi/plugdef.schema.json"
 *         },
 *         "filtdef": {
 *             "description": "An information for filtering the contents.",
 *             "$ref": "https://project-k.mydns.jp/safi/filtdef.schema.json"
 *         },
 *         "trnsdef": {
 *             "description": "Configuration for transformation.",
 *             "type": "object",
 *             "patternProperties": {
 *                 "^.+$": {
 *                     "type": "string"
 *                 }
 *             }
 *         },
 *         "options": {
 *             "description": "Optional configurations at job execution.",
 *             "type": "object"
 *         },
 *         "note": {
 *             "description": "Note for this entity.",
 *             "type": "string"
 *         },
 *         "version": {
 *             "description": "Entity version stored in database. 0 if not yet stored.",
 *             "type": "integer",
 *             "minimum": 0
 *         }
 *     },
 *     "required": [
 *         "id",
 *         "validityPeriod",
 *         "jobKind",
 *         "contentKind",
 *         "name",
 *         "timeout",
 *         "plugdef",
 *         "filtdef",
 *         "trnsdef",
 *         "options",
 *         "version"
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
public interface Jobdef extends PersistableValue {

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
    @Schema(description = "Job kind.")
    JobKind getJobKind();

    /**
     * Get the {@code ContentKind}.
     *
     * @return the {@code ContentKind}
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Content kind that job handles.")
    ContentKind getContentKind();

    /**
     * Get job definition name.
     *
     * @return job definition name
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 100)
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
    @Schema(description = "An information for loading and executing the Plugin.")
    Optional<@Valid Plugdef> getPlugdef();

    /**
     * Get the {@code Filtdef}.
     *
     * @return the {@code Filtdef}
     * @since 1.0.0
     */
    @Schema(description = "CAn information for filtering the contents.")
    Optional<@Valid Filtdef> getFiltdef();

    /**
     * Get content transform definition.
     *
     * @return content transform definition
     * @since 1.0.0
     */
    @Schema(description = "Content transform definition.")
    Optional<Map<String, @NotNull String>> getTrnsdef();

    /**
     * Get optional configurations at job execution.
     *
     * @return optional configurations at job execution
     * @since 1.0.0
     */
    @Schema(description = "Optional configurations at job execution.")
    Optional<JsonObject> getOptions();

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    @NotNull
    @Valid
    ValidityPeriod getValidityPeriod();

    /**
     * Builder of the {@code Jobdef}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder extends AbstractBuilder<Builder, Jobdef> {

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

        /**
         * Constructor.
         *
         * @since 1.0.0
         */
        public Builder() {
            super(Builder.class);
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Builder with(Jobdef src) {
            super.with(src);

            this.id = src.getId();
            this.jobKind = src.getJobKind();
            this.contentKind = src.getContentKind();
            this.name = src.getName();
            this.timeout = src.getTimeout();
            this.plugdef = src.getPlugdef().orElse(null);
            this.filtdef = src.getFiltdef().orElse(null);
            this.trnsdef = src.getTrnsdef().orElse(null);
            this.options = src.getOptions().orElse(null);
            this.validityPeriod = src.getValidityPeriod();

            return builderType.cast(this);
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
         * Set optional configurations at job execution.
         *
         * @param options optional configurations at job execution
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
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Jobdef build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        /**
         * Implements of the {@code Jobdef}.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean extends AbstractBuilder.AbstractBean implements Jobdef {

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
             * @param builder the {@code Jobdef.Builder}
             * @since 1.0.0
             */
            protected Bean(Builder builder) {
                super(builder);

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
            public Optional<JsonObject> getOptions() {
                return Optional.ofNullable(options);
            }

            /**
             * Set optional configurations at job execution.
             *
             * @param options optional configurations at job execution
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
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "Jobdef{" + "id=" + id + ", jobKind=" + jobKind + ", contentKind=" + contentKind + ", name=" + name
                        + ", timeout=" + timeout + ", plugdef=" + plugdef + ", filtdef=" + filtdef + ", trnsdef=" + trnsdef
                        + ", options=" + options + ", validityPeriod=" + validityPeriod + ", version=" + version + '}';
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
