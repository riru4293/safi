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
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.ContentKind;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * <i>Job</i> information.
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
 *     "title": "Job",
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
 *             "description": "Schedule of the job execution.",
 *             "type": "date-time"
 *         },
 *         "limitTime": {
 *             "description": "Time limit of the job execution.",
 *             "type": "date-time"
 *         },
 *         "kind": {
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
 *         "beginTime": {
 *             "description": "Begin time of the job execution.",
 *             "type": "date-time"
 *         },
 *         "endTime": {
 *             "description": "End time of the job execution.",
 *             "type": "date-time"
 *         },
 *         "messages": {
 *             "description": "Result messages.",
 *             "type": "array",
 *             "items": {
 *                 "type": "string"
 *             }
 *         },
 *         "schedefId": {
 *             "schedefId": "Schedule-definition id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "jobdefId": {
 *             "schedefId": "Job definition id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "options": {
 *             "description": "Optional configuration at job execution.",
 *             "type": "object"
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
 *         "status",
 *         "scheduleTime",
 *         "limitTime",
 *         "kind",
 *         "contentKind",
 *         "jobdefId",
 *         "options"
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(Job.Deserializer.class)
@Schema(name = "Job", description = "Job information. A Job represents a single batch processing execution and manages its life-cycle.")
public interface Job {

    /**
     * Get job id.
     *
     * @return job id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 26)
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
     * Get schedule of job execution.
     *
     * @return schedule time
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    @Schema(type = "date-time", description = "Schedule of job execution.")
    OffsetDateTime getScheduleTime();

    /**
     * Get the time limit of job execution.
     *
     * @return time limit
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    @Schema(type = "date-time", description = "Time limit of job execution.")
    OffsetDateTime getLimitTime();

    /**
     * Get the {@code JobKind}.
     *
     * @return the {@code JobKind}
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Job kind.")
    JobKind getKind();

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
     * Get the begin time of the job execution.
     *
     * @return begin time. If empty means not yet begin execution.
     * @since 1.0.0
     */
    @Schema(type = "date-time", description = "Begin time of the job execution.")
    Optional<@TimeRange @TimeAccuracy OffsetDateTime> getBeginTime();

    /**
     * Get the end time of the job execution.
     *
     * @return end time. If empty means not yet end execution.
     * @since 1.0.0
     */
    @Schema(type = "date-time", description = "End time of the job execution.")
    Optional<@TimeRange @TimeAccuracy OffsetDateTime> getEndTime();

    /**
     * Get the summary messages of job execution result.
     *
     * @return summary messages
     * @since 1.0.0
     */
    @Schema(description = "Summary messages of job execute result.")
    Optional<List<@NotBlank String>> getMessages();

    /**
     * Get schedule-definition id.
     *
     * @return schedule-definition id. If empty means not created from schedule-definition.
     * @since 1.0.0
     */
    @Schema(description = "Schedule-definition id.")
    Optional<@Size(min = 1, max = 36) String> getSchedefId();

    /**
     * Get Job definition id.
     *
     * @return Job definition id
     * @since 1.0.0
     */
    @Schema(description = "Job definition id.")
    @NotBlank
    @Size(max = 36)
    String getJobdefId();

    /**
     * Get optional configuration at job execution.
     *
     * @return optional configuration
     * @since 1.0.0
     */
    @Schema(description = "Optional configuration at job execution.")
    JsonObject getOptions();

    /**
     * Get the {@code PersistenceContext}.
     *
     * @return persistence information
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
     * Get a {@code Map.Entry} whose key is {@link #getId()} and whose value is this instance.
     *
     * @return the {@code Map.Entry}
     * @since 1.0.0
     */
    Map.Entry<String, Job> asEntry();

    /**
     * Builder of the {@link Job}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private String id;
        private JobStatus status;
        private OffsetDateTime scheduleTime;
        private OffsetDateTime limitTime;
        private JobKind kind;
        private ContentKind contentKind;
        private Plugdef plugdef;
        private Filtdef filtdef;
        private Map<String, String> trnsdef;
        private OffsetDateTime beginTime;
        private OffsetDateTime endTime;
        private List<String> messages;
        private String schedefId;
        private String jobdefId;
        private JsonObject options;
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
        public Builder(Job src) {
            Objects.requireNonNull(src);

            this.id = src.getId();
            this.status = src.getStatus();
            this.scheduleTime = src.getScheduleTime();
            this.limitTime = src.getLimitTime();
            this.kind = src.getKind();
            this.contentKind = src.getContentKind();
            src.getPlugdef().ifPresent(v -> this.plugdef = v);
            src.getFiltdef().ifPresent(v -> this.filtdef = v);
            src.getTrnsdef().ifPresent(v -> this.trnsdef = v);
            src.getBeginTime().ifPresent(v -> this.beginTime = v);
            src.getEndTime().ifPresent(v -> this.endTime = v);
            src.getMessages().ifPresent(v -> this.messages = v);
            src.getSchedefId().ifPresent(v -> this.schedefId = v);
            this.jobdefId = src.getJobdefId();
            this.options = src.getOptions();
            src.getPersistenceContext().ifPresent(v -> this.persistenceContext = v);
            this.note = src.getNote();
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
         * Set the schedule of the job execution.
         *
         * @param scheduleTime schedule time
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
         * @param limitTime time limit
         * @return updated this
         * @since 1.0.0
         */
        public Builder withLimitTime(OffsetDateTime limitTime) {
            this.limitTime = limitTime;
            return this;
        }

        /**
         * Set the {@code JobKind}.
         *
         * @param kind the {@code JobKind}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withKind(JobKind kind) {
            this.kind = kind;
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
         * Set the begin time of the job execution.
         *
         * @param beginTime begin time. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withBeginTime(OffsetDateTime beginTime) {
            this.beginTime = beginTime;
            return this;
        }

        /**
         * Set the end time of the job execution.
         *
         * @param endTime end time. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withEndTime(OffsetDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * Set summary messages of the job execution result.
         *
         * @param messages summary messages. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withMessages(List<String> messages) {
            this.messages = messages;
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
         * Set the Job definition id.
         *
         * @param jobdefId Job definition id
         * @return updated this
         * @since 1.0.0
         */
        public Builder withJobdefId(String jobdefId) {
            this.jobdefId = jobdefId;
            return this;
        }

        /**
         * Set the optional configuration at job execution.
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
        public Job build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        /**
         * Implements of the {@code Job} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements Job {

            private String id;
            private JobStatus status;
            private OffsetDateTime scheduleTime;
            private OffsetDateTime limitTime;
            private JobKind kind;
            private ContentKind contentKind;
            private Plugdef plugdef;
            private Filtdef filtdef;
            private Map<String, String> trnsdef;
            private OffsetDateTime beginTime;
            private OffsetDateTime endTime;
            private List<String> messages;
            private String schedefId;
            private String jobdefId;
            private JsonObject options;
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
             * @param builder the {@code Job.Builder}
             * @throws NullPointerException if {@code builder} is {@code null}
             * @since 1.0.0
             */
            protected Bean(Builder builder) {
                Objects.requireNonNull(builder);

                this.id = builder.id;
                this.status = builder.status;
                this.scheduleTime = builder.scheduleTime;
                this.limitTime = builder.limitTime;
                this.kind = builder.kind;
                this.contentKind = builder.contentKind;
                this.plugdef = builder.plugdef;
                this.filtdef = builder.filtdef;
                this.trnsdef = builder.trnsdef;
                this.beginTime = builder.beginTime;
                this.endTime = builder.endTime;
                this.messages = builder.messages;
                this.schedefId = builder.schedefId;
                this.jobdefId = builder.jobdefId;
                this.options = builder.options;
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

            /**
             * Set the {@code JobStatus}.
             *
             * @param status the {@code JobStatus}
             * @since 1.0.0
             */
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

            /**
             * Set the schedule of job execution.
             *
             * @param scheduleTime schedule time
             * @since 1.0.0
             */
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

            /**
             * Set the time limit of job execution.
             *
             * @param limitTime time limit
             * @since 1.0.0
             */
            public void setLimitTime(OffsetDateTime limitTime) {
                this.limitTime = limitTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public JobKind getKind() {
                return kind;
            }

            /**
             * Set the {@code JobKind}.
             *
             * @param kind the {@code JobKind}
             * @since 1.0.0
             */
            public void setKind(JobKind kind) {
                this.kind = kind;
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
            public Optional<OffsetDateTime> getBeginTime() {
                return Optional.ofNullable(beginTime);
            }

            /**
             * Set the begin time of the job execution.
             *
             * @param beginTime begin time. It can be set {@code null}.
             * @since 1.0.0
             */
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

            /**
             * Set the end time of the job execution.
             *
             * @param endTime end time. It can be set {@code null}.
             * @since 1.0.0
             */
            public void setEndTime(OffsetDateTime endTime) {
                this.endTime = endTime;
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
             * @param messages summary messages. It can be set {@code null}.
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
            public Optional<String> getSchedefId() {
                return Optional.ofNullable(schedefId);
            }

            /**
             * Set schedule-definition id.
             *
             * @param schedefId schedule-definition id. It can be set {@code null}.
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
            public String getJobdefId() {
                return jobdefId;
            }

            /**
             * Set Job definition id.
             *
             * @param jobdefId Job definition id
             * @since 1.0.0
             */
            public void setJobdefId(String jobdefId) {
                this.jobdefId = jobdefId;
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
             * @param options optional configuration
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
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Map.Entry<String, Job> asEntry() {
                return new AbstractMap.SimpleImmutableEntry<>(id, this);
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "Job{" + "id=" + id + ", status=" + status
                        + ", scheduleTime=" + scheduleTime + ", limitTime=" + limitTime
                        + ", kind=" + kind + ", contentKind=" + contentKind
                        + ", beginTime=" + beginTime + ", endTime=" + endTime
                        + ", schedefId=" + schedefId + ", jobdefId=" + jobdefId + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code Job}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<Job> {

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
        public Job deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
