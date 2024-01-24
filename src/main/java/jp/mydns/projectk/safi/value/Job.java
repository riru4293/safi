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
import jakarta.json.bind.annotation.JsonbTransient;
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
import java.util.Map;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Job information.
 *
 * A <i>Job</i> represents a single batch processing execution and manages its life-cycle.
 *
 * Life-cycle include SCHEDULE, RUNNING, SUCCESS, FAILURE, and ABORT, and these allow to express states such as
 * execution reservation, running, and execution result. At the time of creation <i>Job</i>, it is always in the
 * execution reservation state, and changes to the execution result after passing through the now running state 1 or 0
 * times. It does not change from the execution result state.
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
 *     "title": "Jobdef",
 *     "description": "Job information. A Job represents a single batch processing execution and manages its life-cycle.",
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
 *                 "ABORT",
 *                 "SUCCESS",
 *                 "FAILURE"
 *             ]
 *         },
 *         "scheduleTime": {
 *             "description": "Schedule date-time of the job execution.",
 *             "type": "date-time"
 *         },
 *         "limitTime": {
 *             "description": "Time limit of the job execution.",
 *             "type": "date-time"
 *         },
 *         "beginTime": {
 *             "description": "Begin date-time of the job execution.",
 *             "type": "date-time"
 *         },
 *         "endTime": {
 *             "description": "End date-time of the job execution.",
 *             "type": "date-time"
 *         },
 *         "schedefId": {
 *             "schedefId": "Schedule-definition id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "messages": {
 *             "description": "Result messages.",
 *             "type": "array",
 *             "items": {
 *                 "type": "string"
 *             }
 *         },
 *         "options": {
 *             "description": "Optional configurations at job execution.",
 *             "type": "object"
 *         },
 *         "jobdef": {
 *             "description": "Job definition.",
 *             "$ref": "https://project-k.mydns.jp/safi/jobdef.schema.json"
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
 *         "status",
 *         "scheduleTime",
 *         "limitTime",
 *         "messages",
 *         "options",
 *         "jobdef",
 *         "version"
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(Job.Deserializer.class)
@Schema(name = "Job", description = "Job information.")
public interface Job extends Map.Entry<String, Job>, PersistableValue {

    /**
     * Get job id.
     *
     * @return job id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Job id. It format is the ULID.")
    String getId();

    /**
     * Get the {@code JobStatus}.
     *
     * @return the {@code JobStatus}
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Job status.")
    JobStatus getStatus();

    /**
     * Get schedule date-time of job execution.
     *
     * @return schedule date-time of job execution. It timezone is UTC.
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    @Schema(type = "string", description = "Schedule date-time of job execution. It timezone is UTC.")
    OffsetDateTime getScheduleTime();

    /**
     * Get the time limit of job execution.
     *
     * @return time limit of job execution. It timezone is UTC.
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    @Schema(type = "string", description = "Time limit of job execution. It timezone is UTC.")
    OffsetDateTime getLimitTime();

    /**
     * Get the begin date-time of the job execution.
     *
     * @return begin date-time of the job execution. It timezone is UTC. If empty means not yet begin execution.
     * @since 1.0.0
     */
    @Schema(type = "string", description = "Begin date-time of the job execution. It timezone is UTC.")
    Optional<@TimeRange @TimeAccuracy OffsetDateTime> getBeginTime();

    /**
     * Get the end date-time of the job execution.
     *
     * @return end date-time of the job execution. It timezone is UTC. If empty means not yet end execution.
     * @since 1.0.0
     */
    @Schema(type = "string", description = "End date-time of the job execution. It timezone is UTC.")
    Optional<@TimeRange @TimeAccuracy OffsetDateTime> getEndTime();

    /**
     * Get schedule-definition id.
     *
     * @return schedule-definition id. If empty means not created from schedule-definition.
     * @since 1.0.0
     */
    @Schema(description = "Schedule-definition id.")
    Optional<@Size(min = 1, max = 36) String> getSchedefId();

    /**
     * Get the summary messages of job execution result.
     *
     * @return summary messages of job execution result
     * @since 1.0.0
     */
    @Schema(description = "Summary messages of job execute result.")
    Optional<List<String>> getMessages();

    /**
     * Get optional configurations at job execution.
     *
     * @return optional configurations at job execution
     * @since 1.0.0
     */
    @Schema(description = "Optional configurations at job execution.")
    Optional<JsonObject> getOptions();

    /**
     * Get the {@code Jobdef}.
     *
     * @return the {@code Jobdef}
     * @since 1.0.0
     */
    @NotNull
    @Valid
    Jobdef getJobdef();

    /**
     * Get job id.
     *
     * @return job id
     * @since 1.0.0
     */
    @Override
    @JsonbTransient
    default String getKey() {
        return getId();
    }

    /**
     * Get the this instance.
     *
     * @return the this instance
     * @since 1.0.0
     */
    @Override
    @JsonbTransient
    default Job getValue() {
        return this;
    }

    /**
     * Unsupported.
     *
     * @param unused unused-value
     * @return none
     * @throws UnsupportedOperationException always
     * @since 1.0.0
     */
    @Override
    @JsonbTransient
    default Job setValue(Job unused) {
        throw new UnsupportedOperationException();
    }

    /**
     * Builder of the {@code Job}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder extends PersistableValue.AbstractBuilder<Builder, Job> {

        private String id;
        private JobStatus status;
        private OffsetDateTime scheduleTime;
        private OffsetDateTime limitTime;
        private OffsetDateTime beginTime;
        private OffsetDateTime endTime;
        private String schedefId;
        private List<String> messages;
        private JsonObject options;
        private Jobdef jobdef;

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
        public Builder with(Job src) {
            super.with(src);

            this.id = src.getId();
            this.status = src.getStatus();
            this.scheduleTime = src.getScheduleTime();
            this.limitTime = src.getLimitTime();
            this.beginTime = src.getBeginTime().orElse(null);
            this.endTime = src.getEndTime().orElse(null);
            this.schedefId = src.getSchedefId().orElse(null);
            this.messages = src.getMessages().orElse(null);
            this.options = src.getOptions().orElse(null);
            this.jobdef = src.getJobdef();

            return builderType.cast(this);
        }

        /**
         * Set job id.
         *
         * @param id job id
         * @return updated this
         * @since 1.0.0
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
         * @since 1.0.0
         */
        public Builder withStatus(JobStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Set the schedule date-time of the job execution.
         *
         * @param scheduleTime date-time of the job execution. It timezone is UTC.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withScheduleTime(OffsetDateTime scheduleTime) {
            this.scheduleTime = scheduleTime;
            return this;
        }

        /**
         * Set the time limit of the job execution.
         *
         * @param limitTime time limit of the job execution. It timezone is UTC.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withLimitTime(OffsetDateTime limitTime) {
            this.limitTime = limitTime;
            return this;
        }

        /**
         * Set the begin date-time of the job execution.
         *
         * @param beginTime begin date-time of the job execution. It timezone is UTC. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withBeginTime(OffsetDateTime beginTime) {
            this.beginTime = beginTime;
            return this;
        }

        /**
         * Set the end date-time of the job execution.
         *
         * @param endTime end date-time of the job execution
         * @return updated this
         * @since 1.0.0
         */
        public Builder withEndTime(OffsetDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * Set the schedule-definition id.
         *
         * @param schedefId schedule-definition id. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withSchedefId(String schedefId) {
            this.schedefId = schedefId;
            return this;
        }

        /**
         * Set summary messages of the job execution result.
         *
         * @param messages summary messages of the job execution result. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withMessages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        /**
         * Set the optional configurations at job execution.
         *
         * @param options optional configurations at job execution. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withOptions(JsonObject options) {
            this.options = options;
            return this;
        }

        /**
         * Set the {@code Jondef}.
         *
         * @param jobdef the {@code Jondef}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withJobdef(Jobdef jobdef) {
            this.jobdef = jobdef;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Job build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        /**
         * Implements of the {@code Job}.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean extends PersistableValue.AbstractBuilder.AbstractBean implements Job {

            private String id;
            private JobStatus status;
            private OffsetDateTime scheduleTime;
            private OffsetDateTime limitTime;
            private OffsetDateTime beginTime;
            private OffsetDateTime endTime;
            private String schedefId;
            private List<String> messages;
            private JsonObject options;
            private Jobdef jobdef;

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
             * @param builder the {@code Job.Builder}
             * @since 1.0.0
             */
            protected Bean(Job.Builder builder) {
                super(builder);

                this.id = builder.id;
                this.status = builder.status;
                this.scheduleTime = builder.scheduleTime;
                this.limitTime = builder.limitTime;
                this.beginTime = builder.beginTime;
                this.endTime = builder.endTime;
                this.schedefId = builder.schedefId;
                this.messages = builder.messages;
                this.options = builder.options;
                this.jobdef = builder.jobdef;
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
             * Set job id.
             *
             * @param id job id
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
            public JobStatus getStatus() {
                return status;
            }

            public void setStatus(JobStatus status) {
                this.status = status;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public OffsetDateTime getScheduleTime() {
                return scheduleTime;
            }

            public void setScheduleTime(OffsetDateTime scheduleTime) {
                this.scheduleTime = scheduleTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public OffsetDateTime getLimitTime() {
                return limitTime;
            }

            public void setLimitTime(OffsetDateTime limitTime) {
                this.limitTime = limitTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<OffsetDateTime> getBeginTime() {
                return Optional.ofNullable(beginTime);
            }

            public void setBeginTime(OffsetDateTime beginTime) {
                this.beginTime = beginTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<OffsetDateTime> getEndTime() {
                return Optional.ofNullable(endTime);
            }

            public void setEndTime(OffsetDateTime endTime) {
                this.endTime = endTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<String> getSchedefId() {
                return Optional.ofNullable(schedefId);
            }

            /**
             * Set schedule-definition id.
             *
             * @param schedefId schedule-definition id
             * @since 1.0.0
             */
            public void setSchedefId(String schedefId) {
                this.schedefId = schedefId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<List<String>> getMessages() {
                return Optional.ofNullable(messages);
            }

            /**
             * Set the summary messages of job execution result.
             *
             * @param messages summary messages of job execution result
             * @since 1.0.0
             */
            public void setMessages(List<String> messages) {
                this.messages = messages;
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
            public Jobdef getJobdef() {
                return jobdef;
            }

            /**
             * Set the {@code Jobdef}.
             *
             * @param jobdef the {@code Jobdef}
             * @since 1.0.0
             */
            public void setJobdef(Jobdef jobdef) {
                this.jobdef = jobdef;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "Job{" + "id=" + id + ", status=" + status + ", scheduleTime=" + scheduleTime
                        + ", limitTime=" + limitTime + ", beginTime=" + beginTime + ", endTime=" + endTime
                        + ", schedefId=" + schedefId + ", messages=" + messages + ", options=" + options
                        + ", jobdef=" + jobdef + ", version=" + version + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code Job}.
     *
     * @implSpec This class is immutable and thread-safe.
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<Job> {

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Job deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
