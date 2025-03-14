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
import jp.mydns.projectk.safi.validator.DurationRange;
import jp.mydns.projectk.safi.validator.PositiveOrZeroDuration;
import jp.mydns.projectk.safi.validator.TimeAccuracy;

/**
 * <i>Job</i> information. {@code Job} representations that life cycle of one batch process.
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
 *     "$id": "https://project-k.mydns.jp/safi/job.schema.json",
 *     "title": "Job",
 *     "description": "Job information.",
 *     "type": "object",
 *     "properties": {
 *         "id": {
 *             "description": "Job id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "status": {
 *             "description": "Job status.",
 *             "type": "string",
 *             "enum": [
 *                 "SCHEDULE",
 *                 "RUNNING",
 *                 "SUCCESS",
 *                 "FAILURE",
 *                 "ABORT"
 *             ]
 *         },
 *         "kind": {
 *             "description": "Job kind.",
 *             "type": "string",
 *             "enum": [
 *                 "IMPORT",
 *                 "EXPORT",
 *                 "REBUILD"
 *             ]
 *         },
 *         "target": {
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
 *         "scheduleTime": {
 *             "description": "Job schedule time.",
 *             "type": "date-time"
 *         },
 *         "limitTime": {
 *             "description": "Limit time at job execution.",
 *             "type": "date-time"
 *         },
 *         "beginTime": {
 *             "description": "Begin time at job execution.",
 *             "type": "date-time"
 *         },
 *         "endTime": {
 *             "description": "End time at job execution.",
 *             "type": "date-time"
 *         },
 *         "properties": {
 *             "description": "Optional configurations at job execution.",
 *             "type": "object"
 *         },
 *         "jobdefId": {
 *             "description": "Job definition id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "jobdef": {
 *             "description": "Job definition.",
 *             "$ref": "https://project-k.mydns.jp/safi/jobdef.schema.json"
 *         },
 *         "schedefId": {
 *             "description": "Schedule definition id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "schedef": {
 *             "description": "Schedule definition.",
 *             "$ref": "https://project-k.mydns.jp/safi/schedef.schema.json"
 *         },
 *         "resultMessages": {
 *             "description": "Result messages.",
 *             "type": "array"
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
 *         "status",
 *         "kind",
 *         "target",
 *         "scheduleTime",
 *         "limitTime",
 *         "properties",
 *         "jobdefId",
 *         "jobdef",
 *         "version"
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(JobValue.Deserializer.class)
@Schema(name = "Jobdef", description = "Definition for creates a job.")
public interface JobValue extends NamedValue {

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Job definition id.")
    String getId();

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
    @PositiveOrZeroDuration
    @DurationRange(maxSecond = 86_399L/*23h59m59s*/)
    @TimeAccuracy
    @Schema(type = "string", description = "Job execution timeout.")
    Duration getTimeout();

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
     * Get the {@code FiltdefValue}.
     *
     * @return the {@code FiltdefValue}
     * @since 3.0.0
     */
    @Schema(description = "Content filtering definition.")
    @NotNull
    @Valid
    FiltdefValue getFiltdef();

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
     * Builder of the {@code JobdefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, JobValue> {

        private String id;
        private JobKind jobKind;
        private JobTarget jobTarget;
        private Duration timeout;
        private String pluginName;
        private Map<String, String> trnsdef;
        private FiltdefValue filtdef;
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
        public Builder with(JobValue src) {
            super.with(src);

            this.id = src.getId();
            this.jobKind = src.getJobKind();
            this.jobTarget = src.getJobTarget();
            this.timeout = src.getTimeout();
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
        public Builder withFiltdef(FiltdefValue filtdef) {
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
        public JobValue build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public JobValue unsafeBuild() {
            return new Bean(this);
        }

        /**
         * Implements of the {@code JobdefValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean extends AbstractBuilder.AbstractBean implements JobValue {

            private String id;
            private JobKind jobKind;
            private JobTarget jobTarget;
            private Duration timeout;
            private String pluginName;
            private Map<String, String> trnsdef;
            private FiltdefValue filtdef;
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
             * @param builder the {@code JobdefValue.Builder}
             * @since 3.0.0
             */
            protected Bean(Builder builder) {
                super(builder);

                this.id = builder.id;
                this.jobKind = builder.jobKind;
                this.jobTarget = builder.jobTarget;
                this.timeout = builder.timeout;
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
            public FiltdefValue getFiltdef() {
                return filtdef;
            }

            /**
             * Set filtering setting.
             *
             * @param filtdef filtering setting
             * @since 3.0.0
             */
            public void setFiltdef(FiltdefValue filtdef) {
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
                return "JobdefValue{" + "id=" + id + ", validityPeriod=" + validityPeriod + ", jobKind=" + jobKind
                    + ", jobTarget=" + jobTarget + ", timeout=" + timeout + ", name=" + name
                    + ", pluginName=" + pluginName + ", trnsdef=" + trnsdef + ", filtdef=" + filtdef
                    + ", jobProperties=" + jobProperties + ", version=" + version + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code JobdefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<JobValue> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public JobValue deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
