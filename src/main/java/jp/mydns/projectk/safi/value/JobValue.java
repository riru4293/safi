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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.trial.SchedefValue;

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
 * <a href="{@docRoot}/../schemas/job.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(JobValue.Deserializer.class)
@Schema(name = "Job", description = "Job information. Job representations that life cycle of one batch process.")
public interface JobValue extends PersistableValue {

    /**
     * Get job id.
     *
     * @return job id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Job id.")
    String getId();

    /**
     * Get the {@code JobStatus}.
     *
     * @return the {@code JobStatus}
     * @since 3.0.0
     */
    @NotNull
    @Schema(description = "Job status.")
    JobStatus getStatus();

    /**
     * Get the {@code JobKind}.
     *
     * @return the {@code JobKind}
     * @since 3.0.0
     */
    @NotNull
    @Schema(description = "Job kind.")
    JobKind getKind();

    /**
     * Get the {@code JobTarget}.
     *
     * @return the {@code JobTarget}
     * @since 3.0.0
     */
    @NotNull
    @Schema(description = "Target content type.")
    JobTarget getTarget();

    /**
     * Get job schedule time.
     *
     * @return job schedule time
     * @since 3.0.0
     */
    @NotNull
    @TimeRange(maxEpochSecond = 32_503_593_600L/*2999-12-31T00:00:00*/)
    @TimeAccuracy
    @Schema(description = "Job schedule time.")
    OffsetDateTime getScheduleTime();

    /**
     * Get limit time at job execution.
     *
     * @return limit time at job execution
     * @since 3.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    @Schema(description = "Limit time at job execution.")
    OffsetDateTime getLimitTime();

    /**
     * Get begin time at job execution.
     *
     * @return begin time
     * @since 3.0.0
     */
    @Schema(description = "Begin time at job execution.")
    Optional<@TimeRange @TimeAccuracy OffsetDateTime> getBeginTime();

    /**
     * Get end time at job execution.
     *
     * @return end time
     * @since 3.0.0
     */
    @Schema(description = "End time at job execution.")
    Optional<@TimeRange @TimeAccuracy OffsetDateTime> getEndTime();

    /**
     * Get optional configurations at job execution.
     *
     * @return optional configurations at job execution
     * @since 3.0.0
     */
    @Schema(ref = "#/components/schemas/Jobdef/properties/jobProperties")
    @NotNull
    JsonObject getProperties();

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Job definition id.")
    String getJobdefId();

    /**
     * Get job definition.
     *
     * @return job definition
     * @since 3.0.0
     */
    @NotNull
    @Valid
    @Schema(description = "Job definition.")
    JobdefValue getJobdef();

    /**
     * Get schedule definition id.
     *
     * @return schedule definition id
     * @since 3.0.0
     */
    @Schema(description = "Schedule definition id.")
    Optional<@Size(max = 36) String> getSchedefId();

    /**
     * Get schedule definition.
     *
     * @return schedule definition
     * @since 3.0.0
     */
    @Schema(description = "Schedule definition.")
    Optional<@Valid SchedefValue> getSchedef();

    /**
     * Get job execution result messages.
     *
     * @return result messages
     * @since 3.0.0
     */
    @Schema(description = "Job execution result messages.")
    Optional<List<String>> getResultMessages();

    /**
     * Builder of the {@code JobValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, JobValue> {

        private String id;
        private JobStatus status;
        private JobKind kind;
        private JobTarget target;
        private OffsetDateTime scheduleTime;
        private OffsetDateTime limitTime;
        private OffsetDateTime beginTime;
        private OffsetDateTime endTime;
        private JsonObject properties;
        private String jobdefId;
        private JobdefValue jobdef;
        private String schedefId;
        private SchedefValue schedef;
        private List<String> resultMessages;

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
            this.status = src.getStatus();
            this.kind = src.getKind();
            this.target = src.getTarget();
            this.scheduleTime = src.getScheduleTime();
            this.limitTime = src.getLimitTime();
            this.beginTime = src.getBeginTime().orElse(null);
            this.endTime = src.getEndTime().orElse(null);
            this.properties = src.getProperties();
            this.jobdefId = src.getJobdefId();
            this.jobdef = src.getJobdef();
            this.schedefId = src.getSchedefId().orElse(null);
            this.schedef = src.getSchedef().orElse(null);
            this.resultMessages = src.getResultMessages().orElse(null);

            return builderType.cast(this);
        }

        /**
         * Set job id.
         *
         * @param id job id
         * @return updated this
         * @since 3.0.0
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set the {@code JobStatus}.
         *
         * @param status the {@code JobStatus}
         * @return updated this
         * @since 3.0.0
         */
        public Builder withStatus(JobStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Set the {@code JobKind}.
         *
         * @param kind the {@code JobKind}
         * @return updated this
         * @since 3.0.0
         */
        public Builder withKind(JobKind kind) {
            this.kind = kind;
            return this;
        }

        /**
         * Set the {@code JobTarget}.
         *
         * @param target the {@code JobTarget}
         * @return updated this
         * @since 3.0.0
         */
        public Builder withTarget(JobTarget target) {
            this.target = target;
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
         * Set job execution limit time.
         *
         * @param limitTime job execution limit time
         * @return updated this
         * @since 3.0.0
         */
        public Builder withLimitTime(OffsetDateTime limitTime) {
            this.limitTime = limitTime;
            return this;
        }

        /**
         * Set job execution begin time.
         *
         * @param beginTime job execution begin time. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public Builder withBeginTime(OffsetDateTime beginTime) {
            this.beginTime = beginTime;
            return this;
        }

        /**
         * Set job execution end time.
         *
         * @param endTime job execution end time. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public Builder withEndTime(OffsetDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * Set optional configurations at job execution.
         *
         * @param properties optional configurations at job execution
         * @return updated this
         * @since 3.0.0
         */
        public Builder withProperties(JsonObject properties) {
            this.properties = properties;
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
         * Set job definition.
         *
         * @param jobdef job definition
         * @return updated this
         * @since 3.0.0
         */
        public Builder withJobdef(JobdefValue jobdef) {
            this.jobdef = jobdef;
            return this;
        }

        /**
         * Set schedule definition id.
         *
         * @param schedefId schedule definition id. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public Builder withSchedefId(String schedefId) {
            this.schedefId = schedefId;
            return this;
        }

        /**
         * Set schedule definition.
         *
         * @param schedef schedule definition. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public Builder withSchedef(SchedefValue schedef) {
            this.schedef = schedef;
            return this;
        }

        /**
         * Set result messages.
         *
         * @param resultMessages job execution result messages. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public Builder withResultMessages(List<String> resultMessages) {
            this.resultMessages = resultMessages;
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
         * Implements of the {@code JobValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean extends AbstractBuilder.AbstractBean implements JobValue {

            private String id;
            private JobStatus status;
            private JobKind kind;
            private JobTarget target;
            private OffsetDateTime scheduleTime;
            private OffsetDateTime limitTime;
            private OffsetDateTime beginTime;
            private OffsetDateTime endTime;
            private JsonObject properties;
            private String jobdefId;
            private JobdefValue jobdef;
            private String schedefId;
            private SchedefValue schedef;
            private List<String> resultMessages;

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
                this.status = builder.status;
                this.kind = builder.kind;
                this.target = builder.target;
                this.scheduleTime = builder.scheduleTime;
                this.limitTime = builder.limitTime;
                this.beginTime = builder.beginTime;
                this.endTime = builder.endTime;
                this.properties = builder.properties;
                this.jobdefId = builder.jobdefId;
                this.jobdef = builder.jobdef;
                this.schedefId = builder.schedefId;
                this.schedef = builder.schedef;
                this.resultMessages = builder.resultMessages;
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
            public JobStatus getStatus() {
                return status;
            }

            /**
             * Set the {@code JobStatus}.
             *
             * @param status the {@code JobStatus}
             * @since 3.0.0
             */
            public void setStatus(JobStatus status) {
                this.status = status;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JobKind getKind() {
                return kind;
            }

            /**
             * Set the {@code JobKind}.
             *
             * @param kind the {@code JobKind}
             * @since 3.0.0
             */
            public void setJobKind(JobKind kind) {
                this.kind = kind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JobTarget getTarget() {
                return target;
            }

            /**
             * Set the {@code JobTarget}.
             *
             * @param target the {@code JobTarget}
             * @since 3.0.0
             */
            public void setTarget(JobTarget target) {
                this.target = target;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public OffsetDateTime getScheduleTime() {
                return scheduleTime;
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
            public OffsetDateTime getLimitTime() {
                return limitTime;
            }

            /**
             * Set job execution limit time.
             *
             * @param limitTime job execution limit time
             * @since 3.0.0
             */
            public void setLimitTime(OffsetDateTime limitTime) {
                this.limitTime = limitTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<OffsetDateTime> getBeginTime() {
                return Optional.ofNullable(beginTime);
            }

            /**
             * Set job execution begin time.
             *
             * @param beginTime job execution begin time. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setBeginTime(OffsetDateTime beginTime) {
                this.beginTime = beginTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<OffsetDateTime> getEndTime() {
                return Optional.ofNullable(endTime);
            }

            /**
             * Set job execution end time.
             *
             * @param endTime job execution end time. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setEndTime(OffsetDateTime endTime) {
                this.endTime = endTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JsonObject getProperties() {
                return properties;
            }

            /**
             * Set optional configurations at job execution.
             *
             * @param properties optional configurations at job execution
             * @since 3.0.0
             */
            public void setProperties(JsonObject properties) {
                this.properties = properties;
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

            public void setJobdefId(String jobdefId) {
                this.jobdefId = jobdefId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JobdefValue getJobdef() {
                return jobdef;
            }

            public void setJobdef(JobdefValue jobdef) {
                this.jobdef = jobdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<String> getSchedefId() {
                return Optional.ofNullable(schedefId);
            }

            /**
             * Set schedule definition id.
             *
             * @param schedefId schedule definition id. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setSchedefId(String schedefId) {
                this.schedefId = schedefId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<SchedefValue> getSchedef() {
                return Optional.ofNullable(schedef);
            }

            /**
             * Set schedule definition.
             *
             * @param schedef schedule definition. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setSchedef(SchedefValue schedef) {
                this.schedef = schedef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<List<String>> getResultMessages() {
                return Optional.ofNullable(resultMessages);
            }

            /**
             * Set job execution result messages.
             *
             * @param resultMessages job execution result messages. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setResultMessages(List<String> resultMessages) {
                this.resultMessages = resultMessages;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "JobValue{" + "id=" + id + ", status=" + status + ", kind=" + kind + ", target=" + target
                    + ", scheduleTime=" + scheduleTime + ", limitTime=" + limitTime + ", beginTime=" + beginTime
                    + ", endTime=" + endTime + ", properties=" + properties + ", jobdefId=" + jobdefId
                    + ", jobdef=" + jobdef + ", schedefId=" + schedefId + ", schedef=" + schedef
                    + ", resultMessages=" + resultMessages + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code JobValue}.
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
