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
package jp.mydns.projectk.safi.value.trial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.util.Objects;
import jp.mydns.projectk.safi.value.NamedValue;

/**
 * Definition for <i>Job</i> scheduling. With this definition, the schedule that can be created is limited to the period
 * between {@literal 2000-01-01T00:00:00Z} and {@literal 2999-12-31T23:59:59Z}.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/./schemas/schedef.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(SchedefValue.Deserializer.class)
@Schema(name = "Schedef", description = "Definition for Job scheduling.")
public interface SchedefValue extends NamedValue {

    /**
     * Get schedule definition id.
     *
     * @return schedule definition id
     * @since 3.0.0
     */
    @NotNull
    @Size(min = 1, max = 36)
    @Schema(description = "Schedule definition id.", example = "test")
    String getId();

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Size(min = 1, max = 36, groups = {Default.class})
    @Schema(description = "Job definition id.", example = "test")
    String getJobdefId();

    /**
     * Get scheduling priority. The larger the number, the higher the priority.
     *
     * @return scheduling priority
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Size(min = 1, max = 1, groups = {Default.class})
    @Schema(description = "Scheduling priority. The larger the number, the higher the priority.", example = "F")
    String getPriority();

    /**
     * Get schedule trigger configuration.
     *
     * @return schedule trigger configuration
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Valid
    ScheduleTriggerValue getTrigger();

    /**
     * Builder of the {@code SchedefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, SchedefValue> {

        private String id;
        private String jobdefId;
        private String priority;
        private ScheduleTriggerValue trigger;

        /**
         * Constructor.
         *
         * @since 3.0.0
         */
        public Builder() {
            super(Builder.class);
        }

        /**
         * Set all properties from {@code src}.
         *
         * @param src source value
         * @return updated this
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 3.0.0
         */
        @Override
        public Builder with(SchedefValue src) {
            Objects.requireNonNull(src);
            this.id = src.getId();
            this.jobdefId = src.getJobdefId();
            this.priority = src.getPriority();
            this.trigger = src.getTrigger();
            return this;
        }

        /**
         * Set schedule definition id.
         *
         * @param id schedule definition id
         * @return updated this
         * @since 3.0.0
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

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
         * Set scheduling priority. The larger the number, the higher the priority.
         *
         * @param priority scheduling priority
         * @return updated this
         * @since 3.0.0
         */
        public Builder withPriority(String priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Set schedule trigger configuration.
         *
         * @param trigger schedule trigger configuration
         * @return updated this
         * @since 3.0.0
         */
        public Builder withTrigger(ScheduleTriggerValue trigger) {
            this.trigger = trigger;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public SchedefValue unsafeBuild() {
            return new Builder.Bean(this);
        }

        protected static class Bean extends AbstractBuilder.AbstractBean implements SchedefValue {

            private String id;
            private String jobdefId;
            private String priority;
            private ScheduleTriggerValue trigger;

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
                this.jobdefId = builder.jobdefId;
                this.priority = builder.priority;
                this.trigger = builder.trigger;
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
             * Set schedule definition id.
             *
             * @param id schedule definition id
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
            public String getPriority() {
                return priority;
            }

            /**
             * Set scheduling priority. The larger the number, the higher the priority.
             *
             * @param priority scheduling priority
             * @since 3.0.0
             */
            public void setPriority(String priority) {
                this.priority = priority;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public ScheduleTriggerValue getTrigger() {
                return trigger;
            }

            /**
             * Set schedule trigger configuration.
             *
             * @param trigger schedule trigger configuration
             * @since 3.0.0
             */
            public void setTrigger(ScheduleTriggerValue trigger) {
                this.trigger = trigger;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "SchedefValue{" + "id=" + id + ", jobdefId=" + jobdefId + ", validityPeriod=" + validityPeriod
                    + ", priority=" + priority + ", trigger=" + trigger + ", name=" + name + ", version=" + version
                    + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code SchedefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<SchedefValue> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public SchedefValue deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return ctx.deserialize(Builder.Bean.class, parser);
        }
    }
}
