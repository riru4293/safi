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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.util.CollectionUtils;
import jp.mydns.projectk.safi.validator.DurationRange;
import jp.mydns.projectk.safi.validator.PositiveOrZeroDuration;
import jp.mydns.projectk.safi.validator.TimeAccuracy;

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
 * <a href="{@docRoot}/../schemas/jobdef.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(JobdefValue.Deserializer.class)
@Schema(name = "Jobdef", description = "Definition for creates a job.")
public interface JobdefValue extends NamedValue {

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotNull
    @Size(min = 1, max = 36)
    @Schema(description = "Job definition id.", example = "test")
    String getId();

    /**
     * Get the {@code JobKind}.
     *
     * @return the {@code JobKind}
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Schema(description = "Job kind.")
    JobKind getJobKind();

    /**
     * Get the {@code JobTarget}.
     *
     * @return the {@code JobTarget}
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Schema(description = "Target content type.")
    JobTarget getJobTarget();

    /**
     * Get job execution timeout.
     *
     * @return job execution timeout
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @PositiveOrZeroDuration(groups = {Default.class})
    @DurationRange(maxSecond = 86_399L/*23h59m59s*/, groups = {Default.class})
    @TimeAccuracy(groups = {Default.class})
    @Schema(type = "string", description = "Job execution timeout. Values from PT0S to PT23H59M59S can be specified.")
    Duration getTimeout();

    /**
     * Get plugin name.
     *
     * @return plugin name
     * @since 3.0.0
     */
    @Schema(description = "The name of the plugin that processes the content. It is case insensitive.", maxLength = 50)
    Optional<@Size(max = 50, groups = {Default.class}) String> getPluginName();

    /**
     * Get content transform definition.
     *
     * @return content transform definition
     * @since 3.0.0
     */
    @Schema(description
        = """
The key is the property name after transformation, and the value is the transformation expression. Details of the expression are as follows.

# Trunsform expression syntax

| Element              | Syntax              | Description |
|----------------------|---------------------|-------------|
| Literal element      | \\`literal-value\\` | The literal value is enclosed by \\`. If you want to escape it, prefix it with \\. |
| Input element        | [input-value-name]  | The input value encloses its name in [ and ]. |
| Function element     | func(arg, arg)      | Function consists of a function name followed by arguments enclosed in ( and ). The arguments are separated by , and the number varies depending on the function. The argument is any kind of the element that "Input" or "Literal" or "Function". |
| Elements joiner      | &                   | Joiner concatenates the values before and after the element. Also, there must be white space before and after the Joiner. |

# Trunsform example

## Premise

- Original values: {"id": " 01 ", "name": "taro"}
- Trunsform expression: [name] & \\`'s number is \\` & LPAD( TRIM( [id] ), \\`4\\`, \\`P\\` )
- Functions
  - TRIM
    - syntax: TRIM( arg-value )
    - description: Trim leading and trailing spaces from arguments.
  - LPAD
    - syntax: LPAD( arg-value, arg-length, arg-padding-char )
    - description: Pads a string to the left with the specified characters until the specified number of digits is reached.

## Process of calculating

| Step   | Interpretation of the expression |
|--------|----------------------------------|
| Step.0 | [name] & \\`'s number is \\` & LPAD( TRIM( [id] ), \\`4\\`, \\`P\\` ) |
| Step.1 | [name] & \\`'s number is \\` & LPAD( TRIM( __\\` 01 \\`__ ), \\`4\\`, \\`P\\` ) |
| Step.2 | [name] & \\`'s number is \\` & LPAD( __\\`01\\`__, \\`4\\`, \\`P\\` ) |
| Step.3 | __\\`taro\\`__ & \\`'s number is \\` & __\\`PP01\\`__ |
| Result | __taro's number is PP01__ |""",
            example = "{\"name\":\"toTitleCase([firstName]) & ` ` & toTitleCase([lastName])\", \"id\":\"[userId]\"}",
            type = "object", additionalPropertiesSchema = String.class)
    Optional<Map<String, String>> getTrnsdef();

    /**
     * Get the {@code FiltdefValue}.
     *
     * @return the {@code FiltdefValue}
     * @since 3.0.0
     */
    Optional<@Valid FiltdefValue> getFiltdef();

    /**
     * Get optional configurations at job execution.
     *
     * @return optional configurations at job execution
     * @since 3.0.0
     */
    @Schema(description = "Optional configurations at job execution.", example = "{}")
    @NotNull(groups = {Default.class})
    JsonObject getJobProperties();

    /**
     * Builder of the {@code JobdefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, JobdefValue> {

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
        public Builder with(JobdefValue src) {
            super.with(Objects.requireNonNull(src));

            this.id = src.getId();
            this.jobKind = src.getJobKind();
            this.jobTarget = src.getJobTarget();
            this.timeout = src.getTimeout();
            this.pluginName = src.getPluginName().orElse(null);
            this.trnsdef = src.getTrnsdef().orElse(null);
            this.filtdef = src.getFiltdef().orElse(null);
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
         * @param jobTarget the {@code JobTarget}
         * @return updated this
         * @since 3.0.0
         */
        public Builder withJobTarget(JobTarget jobTarget) {
            this.jobTarget = jobTarget;
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
            this.trnsdef = CollectionUtils.toUnmodifiable(trnsdef);
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
        public JobdefValue unsafeBuild() {
            return new Bean(this);
        }

        /**
         * Implements of the {@code JobdefValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean extends AbstractBuilder.AbstractBean implements JobdefValue {

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

            private Bean(Builder builder) {
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
                this.trnsdef = CollectionUtils.toUnmodifiable(trnsdef);
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
