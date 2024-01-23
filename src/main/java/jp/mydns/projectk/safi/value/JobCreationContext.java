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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Information to create a new batch execution schedule as {@link Job}.
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
 *     "$id": "https://project-k.mydns.jp/safi/job-creation-context.schema.json",
 *     "title": "JobCreationContext",
 *     "description": "Information to create a new Job execution schedule.",
 *     "type": "object",
 *     "properties": {
 *         "jobdefid": {
 *             "description": "Jobdef id.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 36
 *         },
 *         "scheduleTime": {
 *             "description": "Schedule time of job execution.",
 *             "type": "date-time"
 *         },
 *         "plugdef": {
 *             "description": "An information for loading and executing the Plugin.",
 *             "$ref": "https://project-k.mydns.jp/safi/plugdef.schema.json"
 *         },
 *         "filtdef": {
 *             "description": "An information for filtering the contents.",
 *             "$ref": "https://project-k.mydns.jp/safi/filtdef.schema.json"
 *         },
 *         "trnsdef": {
 *             "description": "Transform definition.",
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
 *             "description": "Note for job-creation-context.",
 *             "type": "string"
 *         }
 *     },
 *     "required": [
 *         "jobdefid",
 *         "scheduleTime"
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(JobCreationContext.Deserializer.class)
@Schema(name = "JobCreationContext", description = "Information to create a new batch execution schedule as job.")
public interface JobCreationContext {

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Job definiton id.")
    String getJobdefId();

    /**
     * Get schedule time of job execution. If empty, it must be interpreted as the current date-time.
     *
     * @return schedule time of job execution. It timezone is UTC.
     * @since 1.0.0
     */
    @Schema(type = "string", description = "Schedule time of job execution. It timezone is UTC."
            + " If empty, it must be interpreted as the current date-time.")
    Optional<@TimeRange @TimeAccuracy OffsetDateTime> getScheduleTime();

    /**
     * Get the {@code Plugdef}. A value that overrides the default value suggested by the job definition.
     *
     * @return the {@code Plugdef}
     * @since 1.0.0
     */
    @Schema(description = "Plugin call setting. A value that overrides the default value suggested by the job definition.")
    Optional<@Valid Plugdef> getPlugdef();

    /**
     * Get the {@code Filtdef}. A value that overrides the default value suggested by the job definition.
     *
     * @return the {@code Filtdef}
     * @since 1.0.0
     */
    @Schema(description = "Content Filtering setting. A value that overrides the default value suggested by the job definition.")
    Optional<@Valid Filtdef> getFiltdef();

    /**
     * Get the transform definition. A value that overrides the default value suggested by the job definition.
     *
     * @return the transform definition
     * @since 1.0.0
     */
    @Schema(description = "The transform definition. A value that overrides the default value suggested by the job definition.")
    Optional<Map<String, @NotNull String>> getTrnsdef();

    /**
     * Get optional configurations at job execution. A value that overrides the default value suggested by the job
     * definition.
     *
     * @return option configurations at job execution
     * @since 1.0.0
     */
    @Schema(description = "Option settings at job execution. A value that overrides the default value suggested by the job definition.")
    Optional<JsonObject> getOptions();

    /**
     * Get note for job-creation-context.
     *
     * @return note for job-creation-context. It may be {code null}.
     * @since 1.0.0
     */
    @Schema(description = "Note for this job-creation-context.")
    String getNote();

    /**
     * JSON deserializer for {@code JobCreationContext}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<JobCreationContext> {

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public JobCreationContext deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Bean.class, jp);
        }

        /**
         * Implements of the {@code Bean}.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements JobCreationContext {

            private String jobdefId;
            private OffsetDateTime scheduleTime;
            private Plugdef plugdef;
            private Filtdef filtdef;
            private Map<String, String> trnsdef;
            private JsonObject options;
            private String note;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 1.0.0
             */
            protected Bean() {
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
             * Set job definition id.
             *
             * @param jobdefId job definition id
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
            public Optional<OffsetDateTime> getScheduleTime() {
                return Optional.ofNullable(scheduleTime);
            }

            /**
             * Set schedule time of job execution.
             *
             * @param scheduleTime schedule time of job execution
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
             * Set the {@code Filtdef}.
             *
             * @param trnsdef the {@code Filtdef}
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
             * Set the transform definition.
             *
             * @param trnsdef the transform definition
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
             * @param options option configurations at job execution
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
            public String getNote() {
                return note;
            }

            /**
             * Set note for job-creation-context.
             *
             * @param note note for job-creation-context. It can be set {code null}.
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
                return "JobCreationContext{" + "jobdefId=" + jobdefId + ", scheduleTime=" + scheduleTime
                        + ", plugdef=" + plugdef + ", filtdef=" + filtdef + ", trnsdef=" + trnsdef
                        + ", options=" + options + '}';
            }
        }
    }
}
