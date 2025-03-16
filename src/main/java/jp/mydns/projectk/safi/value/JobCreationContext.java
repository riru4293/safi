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
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.validator.DurationRange;
import jp.mydns.projectk.safi.validator.PositiveOrZeroDuration;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Information to create a <i>Job</i>.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/../schemas/job-creation-context.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(JobCreationContext.Deserializer.class)
@Schema(name = "JobCreationContext", description = "Information to create a Job.")
public interface JobCreationContext {

    /**
     * Get job definition id to use.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotNull
    @Size(min = 1, max = 36)
    @Schema(description = "Job definition id to use.")
    String getJobdefId();

    /**
     * Get job schedule time.
     *
     * @return job schedule time
     * @since 3.0.0
     */
    @Schema(description = "Job schedule time. Means current time if null.", example = "2000-01-01T00:00:00Z")
    Optional<@TimeRange(maxEpochSecond = 32_503_593_600L/*2999-12-31T00:00:00*/) @TimeAccuracy OffsetDateTime>
        getScheduleTime();

    /**
     * Get job execution timeout. If not null, it overrides the value in the job definition.
     *
     * @return job execution timeout
     * @since 3.0.0
     */
    @Schema(type = "string", description = "Job execution timeout."
            + " If not null, it overrides the value in the job definition.", example = "PT10M")
    Optional<@PositiveOrZeroDuration @DurationRange(maxSecond = 86_399L/*23h59m59s*/) @TimeAccuracy Duration>
        getTimeout();

    /**
     * Get plugin name. If not null, it overrides the value in the job definition.
     *
     * @return plugin name
     * @since 3.0.0
     */
    @Schema(description = "Plugin name. If not null, it overrides the value in the job definition.",
            example = "PluginName")
    Optional<@Size(max = 50) String> getPluginName();

    /**
     * Get content transform definition. If not null, it overrides the value in the job definition.
     *
     * @return content transform definition
     * @since 3.0.0
     */
    @Schema(description = "Content transform definition. If not null, it overrides the value in the job definition.",
            ref = "#/components/schemas/Jobdef/properties/trnsdef")
    Optional<Map<String, String>> getTrnsdef();

    /**
     * Get content filtering definition. If not null, it overrides the value in the job definition..
     *
     * @return content filtering definition
     * @since 3.0.0
     */
    @Schema(description = "Content filtering definition. If not null, it overrides the value in the job definition.")
    Optional<@Valid FiltdefValue> getFiltdef();

    /**
     * Get optional configurations at job execution. If not null, it will be merged(overwrite) to value in the job
     * definition.
     *
     * @return optional configurations at job execution
     * @since 3.0.0
     */
    @Schema(description = "Optional configurations at job execution. If not null, it will be marged(overwrite) "
        + "to value in the job definition.", ref = "#/components/schemas/Jobdef/properties/jobProperties")
    Optional<JsonObject> getJobProperties();

    /**
     * Builder of the {@code JobCreationContext}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder {

        private String jobdefId;
        private OffsetDateTime scheduleTime;
        private Duration timeout;
        private String pluginName;
        private Map<String, String> trnsdef;
        private FiltdefValue filtdef;
        private JsonObject jobProperties;

        /**
         * Set job definition id.
         *
         * @param jobdefId job definition id
         * @return updated this
         * @since 3.0.0
         */
        public Builder withJobdefId(String jobdefId) {
            this.jobdefId = jobdefId;
            return this;
        }

        /**
         * Set job schedule time.
         *
         * @param scheduleTime job schedule time
         * @return updated this
         * @since 3.0.0
         */
        public Builder withScheduleTime(OffsetDateTime scheduleTime) {
            this.scheduleTime = scheduleTime;
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
         * Build a new inspected instance.
         *
         * @param validator the {@code Validator}
         * @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
         * @return new inspected instance
         * @throws NullPointerException if any argument is {@code null}
         * @throws ConstraintViolationException if occurred constraint violations when building
         * @since 3.0.0
         */
        public JobCreationContext build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
        }

        /**
         * Build a new instance. It instance may not meet that constraint. Use only if the original value is completely
         * reliable.
         *
         * @return new unsafe instance
         * @since 3.0.0
         */
        public JobCreationContext unsafeBuild() {
            return new Bean(this);
        }

        /**
         * Implements of the {@code JobCreationContext}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean implements JobCreationContext {

            private String jobdefId;
            private OffsetDateTime scheduleTime;
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
                this.jobdefId = builder.jobdefId;
                this.scheduleTime = builder.scheduleTime;
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
            public String getJobdefId() {
                return jobdefId;
            }

            /**
             * Set job definition id.
             *
             * @param jobdefId job definition id
             * @since 3.0.0
             */
            public void setJobdefId(String jobdefId) {
                this.jobdefId = jobdefId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<OffsetDateTime> getScheduleTime() {
                return Optional.ofNullable(scheduleTime);
            }

            /**
             * Set job schedule time.
             *
             * @param scheduleTime job schedule time
             * @since 3.0.0
             */
            public void setScheduleTime(OffsetDateTime scheduleTime) {
                this.scheduleTime = scheduleTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<Duration> getTimeout() {
                return Optional.ofNullable(timeout);
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
            public Optional<Map<String, String>> getTrnsdef() {
                return Optional.ofNullable(trnsdef);
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
            public Optional<FiltdefValue> getFiltdef() {
                return Optional.ofNullable(filtdef);
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
            public Optional<JsonObject> getJobProperties() {
                return Optional.ofNullable(jobProperties);
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
                return "JobCreationContext{" + "jobdefId=" + jobdefId + ", scheduleTime=" + scheduleTime
                    + ", timeout=" + timeout + ", pluginName=" + pluginName + ", trnsdef=" + trnsdef
                    + ", filtdef=" + filtdef + ", jobProperties=" + jobProperties + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code JobCreationContext}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<JobCreationContext> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public JobCreationContext deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
