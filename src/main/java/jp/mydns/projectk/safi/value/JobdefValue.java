/*
 * Copyright (c) 2025, Project-K
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
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * Definition for creates a <i>Job</i>.
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
 *                 "REBUILD"
 *             ]
 *         },
 *         "jobTarget": {
 *             "description": "Target content type.",
 *             "type": "string",
 *             "enum": [
 *                 "USER",
 *                 "ASSET",
 *                 "BELONG_ORG",
 *                 "ORG1",
 *                 "ORG2",
 *                 "BELONG_GRP",
 *                 "GRP",
 *                 "PER_USER",
 *                 "PER_ASSET"
 *             ]
 *         },
 *         "timeout": {
 *             "description": "Job execution timeout.",
 *             "type": "duration"
 *         },
 *         "name": {
 *             "description": "Job definition name.",
 *             "type": "string",
 *             "maxLength": 250
 *         },
 *         "pluginName": {
 *             "description": "The name of the plugin that processes the content.",
 *             "type": "string",
 *             "maxLength": 50
 *         },
 *         "trnsdef": {
 *             "description": "Content transform definition.",
 *             "type": "object",
 *             "patternProperties": {
 *                 "^.+$": {
 *                     "type": "string"
 *                 }
 *             }
 *         },
 *         "filtdef": {
 *             "description": "Content filtering definiton.",
 *             "$ref": "https://project-k.mydns.jp/safi/filtdef.schema.json"
 *         },
 *         "jobProperties": {
 *             "description": "Optional configurations at job execution.",
 *             "type": "object"
 *         },
 *         "note": {
 *             "description": "Note of this value.",
 *             "type": "string"
 *         },
 *         "version": {
 *             "description": "Entity version stored in database. 0 if not yet stored.",
 *             "type": "integer",
 *             "minimum": 0
 *         },
 *         "registerTime": {
 *             "description": "Registered time. This value for reference only, setting it does not persist the value.",
 *             "type": "date-time"
 *         },
 *         "registerAccountId": {
 *             "description": "Registered account id. This value for reference only, setting it does not persist the value.",
 *             "type": "string",
 *             "maxLength": 250
 *         },
 *         "registerProcessName": {
 *             "description": "Registered process name. This value for reference only, setting it does not persist the value.",
 *             "type": "string",
 *             "maxLength": 250
 *         },
 *         "updateTime": {
 *             "description": "Update time. This value for reference only, setting it does not persist the value.",
 *             "type": "date-time"
 *         },
 *         "updateAccountId": {
 *             "description": "Update account id. This value for reference only, setting it does not persist the value.",
 *             "type": "string",
 *             "maxLength": 250
 *         },
 *         "updateProcessName": {
 *             "description": "Update peocess name. This value for reference only, setting it does not persist the value.",
 *             "type": "string",
 *             "maxLength": 250
 *         }
 *     },
 *     "required": [
 *         "id",
 *         "validityPeriod",
 *         "jobKind",
 *         "jobTarget",
 *         "timeout",
 *         "trnsdef",
 *         "filtdef",
 *         "jobProperties",
 *         "version"
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(JobdefValue.Deserializer.class)
@Schema(name = "Jobdef", description = "Definition for creates a job.")
public interface JobdefValue extends PersistableValue {

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Job definiton id.")
    String getId();

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 3.0.0
     */
    @NotNull
    @Valid
    ValidityPeriod getValidityPeriod();

    /**
     * Get the {@code JobKind}.
     *
     * @return the {@code JobKind}
     * @since 3.0.0
     */
    @NotNull
    @Schema(description = "Job kind.")
    JobKind getJobKind();

    /**
     * Get the {@code JobTarget}.
     *
     * @return the {@code JobTarget}
     * @since 3.0.0
     */
    @NotNull
    @Schema(description = "Target content type.")
    JobTarget getJobTarget();

    /**
     * Get job execution timeout.
     *
     * @return job execution timeout
     * @since 3.0.0
     */
    @NotNull
    @Schema(type = "string", description = "Job execution timeout.")
    Duration getTimeout();

    /**
     * Get job definition name.
     *
     * @return job definition name
     * @since 3.0.0
     */
    @Schema(description = "Job definiton name.")
    Optional<@Size(max = 250) String> getName();

    /**
     * Get plugin name.
     *
     * @return plugin name
     * @since 3.0.0
     */
    @Schema(description = "The name of the plugin that processes the content.")
    Optional<@Size(max = 50) String> getPluginName();

    /**
     * Get content transform definition.
     *
     * @return content transform definition
     * @since 3.0.0
     */
    @Schema(description = "Content transform definition.")
    @NotNull
    Map<String, String> getTrnsdef();

    /**
     * Get the {@code Filtdef}.
     *
     * @return the {@code Filtdef}
     * @since 3.0.0
     */
    @Schema(description = "Content filtering definiton.")
    @NotNull
    @Valid
    Filtdef getFiltdef();

    /**
     * Get optional configurations at job execution.
     *
     * @return optional configurations at job execution
     * @since 3.0.0
     */
    @Schema(description = "Optional configurations at job execution.")
    @NotNull
    JsonObject getJobProperties();

    /**
     * Builder of the {@code Jobdef}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, JobdefValue> {

        private String id;
        private ValidityPeriod validityPeriod;
        private JobKind jobKind;
        private JobTarget jobTarget;
        private Duration timeout;
        private String name;
        private String pluginName;
        private Map<String, String> trnsdef;
        private Filtdef filtdef;
        private JsonObject jobProperties;

        /**
         * Constructor.
         *
         * @since 3.0.0
         */
        public Builder() {
            super(Builder.class);
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public Builder with(JobdefValue src) {
            super.with(src);

            this.id = src.getId();
            this.validityPeriod = src.getValidityPeriod();
            this.jobKind = src.getJobKind();
            this.jobTarget = src.getJobTarget();
            this.timeout = src.getTimeout();
            this.name = src.getName().orElse(null);
            this.pluginName = src.getPluginName().orElse(null);
            this.trnsdef = src.getTrnsdef();
            this.filtdef = src.getFiltdef();
            this.jobProperties = src.getJobProperties();

            return builderType.cast(this);
        }

        /**
         * Set job definition id.
         *
         * @param id job definition id
         * @return updated this
         * @since 3.0.0
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set the {@code ValidityPeriod}.
         *
         * @param validityPeriod the {@code ValidityPeriod}
         * @return updated this
         * @since 3.0.0
         */
        public Builder withValidityPeriod(ValidityPeriod validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        /**
         * Set the {@code JobKind}.
         *
         * @param jobKind the {@code JobKind}
         * @return updated this
         * @since 3.0.0
         */
        public Builder withJobKind(JobKind jobKind) {
            this.jobKind = jobKind;
            return this;
        }

        /**
         * Set the {@code JobTarget}.
         *
         * @param contentKind the {@code JobTarget}
         * @return updated this
         * @since 3.0.0
         */
        public Builder withJobTarget(JobTarget contentKind) {
            this.jobTarget = contentKind;
            return this;
        }

        /**
         * Set job execution timeout.
         *
         * @param timeout job execution timeout
         * @return updated this
         * @since 3.0.0
         */
        public Builder withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Set job definition name.
         *
         * @param name job definition name
         * @return updated this
         * @since 3.0.0
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set plugin name.
         *
         * @param pluginName plugin name
         * @return updated this
         * @since 3.0.0
         */
        public Builder withPluginName(String pluginName) {
            this.pluginName = pluginName;
            return this;
        }

        /**
         * Set transform definition.
         *
         * @param trnsdef transform definition
         * @return updated this
         * @since 3.0.0
         */
        public Builder withTrnsdef(Map<String, String> trnsdef) {
            this.trnsdef = trnsdef;
            return this;
        }

        /**
         * Set filtering definition.
         *
         * @param filtdef filtering definition
         * @return updated this
         * @since 3.0.0
         */
        public Builder withFiltdef(Filtdef filtdef) {
            this.filtdef = filtdef;
            return this;
        }

        /**
         * Set optional configurations at job execution.
         *
         * @param jobProperties optional configurations at job execution
         * @return updated this
         * @since 3.0.0
         */
        public Builder withJobProperties(JsonObject jobProperties) {
            this.jobProperties = jobProperties;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public JobdefValue build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        /**
         * Implements of the {@code Jobdef}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean extends AbstractBuilder.AbstractBean implements JobdefValue {

            private String id;
            private ValidityPeriod validityPeriod;
            private JobKind jobKind;
            private JobTarget jobTarget;
            private Duration timeout;
            private String name;
            private String pluginName;
            private Map<String, String> trnsdef;
            private Filtdef filtdef;
            private JsonObject jobProperties;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected Bean() {
            }

            /**
             * Constructor.
             *
             * @param builder the {@code Jobdef.Builder}
             * @since 3.0.0
             */
            protected Bean(Builder builder) {
                super(builder);

                this.id = builder.id;
                this.validityPeriod = builder.validityPeriod;
                this.jobKind = builder.jobKind;
                this.jobTarget = builder.jobTarget;
                this.timeout = builder.timeout;
                this.name = builder.name;
                this.pluginName = builder.pluginName;
                this.trnsdef = builder.trnsdef;
                this.filtdef = builder.filtdef;
                this.jobProperties = builder.jobProperties;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getId() {
                return id;
            }

            /**
             * Set job definition id.
             *
             * @param id job definition id
             * @since 3.0.0
             */
            public void setId(String id) {
                this.id = id;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public ValidityPeriod getValidityPeriod() {
                return validityPeriod;
            }

            /**
             * Set the {@code ValidityPeriod}.
             *
             * @param validityPeriod the {@code ValidityPeriod}
             * @since 3.0.0
             */
            public void setValidityPeriod(ValidityPeriod validityPeriod) {
                this.validityPeriod = validityPeriod;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JobKind getJobKind() {
                return jobKind;
            }

            /**
             * Set the {@code JobKind}.
             *
             * @param jobKind the {@code JobKind}
             * @since 3.0.0
             */
            public void setJobKind(JobKind jobKind) {
                this.jobKind = jobKind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JobTarget getJobTarget() {
                return jobTarget;
            }

            /**
             * Set the {@code JobTarget}.
             *
             * @param jobTarget the {@code JobTarget}
             * @since 3.0.0
             */
            public void setJobTarget(JobTarget jobTarget) {
                this.jobTarget = jobTarget;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<String> getName() {
                return Optional.ofNullable(name);
            }

            /**
             * Set job definition name.
             *
             * @param name job definition name
             * @since 3.0.0
             */
            public void setName(String name) {
                this.name = name;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Duration getTimeout() {
                return timeout;
            }

            /**
             * Set job execution timeout.
             *
             * @param timeout job execution timeout
             * @since 3.0.0
             */
            public void setTimeout(Duration timeout) {
                this.timeout = timeout;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<String> getPluginName() {
                return Optional.ofNullable(pluginName);
            }

            /**
             * Set plugin name.
             *
             * @param pluginName plugin name
             * @since 3.0.0
             */
            public void setPluginName(String pluginName) {
                this.pluginName = pluginName;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Map<String, String> getTrnsdef() {
                return trnsdef;
            }

            /**
             * Set transform definition.
             *
             * @param trnsdef transform definition
             * @since 3.0.0
             */
            public void setTrnsdef(Map<String, String> trnsdef) {
                this.trnsdef = trnsdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Filtdef getFiltdef() {
                return filtdef;
            }

            /**
             * Set filtering setting.
             *
             * @param filtdef filtering setting
             * @since 3.0.0
             */
            public void setFiltdef(Filtdef filtdef) {
                this.filtdef = filtdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JsonObject getJobProperties() {
                return jobProperties;
            }

            /**
             * Set optional configurations at job execution.
             *
             * @param jobProperties optional configurations at job execution
             * @since 3.0.0
             */
            public void setJobProperties(JsonObject jobProperties) {
                this.jobProperties = jobProperties;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "Jobdef{" + "id=" + id + ", validityPeriod=" + validityPeriod + ", jobKind=" + jobKind
                    + ", jobTarget=" + jobTarget + ", timeout=" + timeout + ", name=" + name
                    + ", pluginName=" + pluginName + ", trnsdef=" + trnsdef + ", filtdef=" + filtdef
                    + ", jobProperties=" + jobProperties + ", version=" + version + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code Jobdef}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<JobdefValue> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public JobdefValue deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
