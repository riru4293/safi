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
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Persistence information.
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
 *     "$id": "https://project-k.mydns.jp/safi/persistence-context.schema.json",
 *     "title": "PersistenceContext",
 *     "description": "Persistence information.",
 *     "type": "object",
 *     "properties": {
 *         "version": {
 *             "description": "Persistence version number.",
 *             "type": "integer",
 *             "minimum": 1
 *         },
 *         "registerTime": {
 *             "description": "First persistence date-time.",
 *             "type": "date-time"
 *         },
 *         "registerAccountId": {
 *             "description": "Account id that first performed persistence.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 255
 *         },
 *         "registerProcessName": {
 *             "description": "Process name that first performed persistence.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 255
 *         },
 *         "updateTime": {
 *             "description": "Last persistence date-time.",
 *             "type": "date-time"
 *         },
 *         "updateAccountId": {
 *             "description": "Account id that last performed persistence.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 255
 *         },
 *         "updateProcessName": {
 *             "description": "Process name that last performed persistence.",
 *             "type": "string",
 *             "minLength": 1,
 *             "maxLength": 255
 *         },
 *         "required": [
 *             "version",
 *             "registerTime",
 *             "registerAccountId",
 *             "registerProcessName",
 *             "updateTime",
 *             "updateAccountId",
 *             "updateProcessName"
 *         ]
 *     }
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(PersistenceContext.Deserializer.class)
@Schema(name = "PersistenceContext", description = "Persistence information.")
public interface PersistenceContext {

    /**
     * Get the persistence version number. The version is a value greater than or equal to 1, starting from 1 and
     * incrementing with each update.
     *
     * @return persistence version number
     * @since 1.0.0
     */
    @Schema(description = "Persistence Version number.")
    @Min(value = 1, groups = {Default.class})
    int getVersion();

    /**
     * Get date-time of first persistence.
     *
     * @return first persistence date-time
     * @since 1.0.0
     */
    @NotNull(groups = {Default.class})
    @TimeRange(groups = {Default.class})
    @TimeAccuracy(groups = {Default.class})
    @Schema(description = "The date-time of first persistence.")
    OffsetDateTime getRegisterTime();

    /**
     * Get account that first performed persistence.
     *
     * @return account id
     * @since 1.0.0
     */
    @NotBlank(groups = {Default.class})
    @Size(max = 255, groups = {Default.class})
    @Schema(description = "The account id that first performed persistence.")
    String getRegisterAccountId();

    /**
     * Get process that first performed persistence.
     *
     * @return process name
     * @since 1.0.0
     */
    @NotBlank(groups = {Default.class})
    @Size(max = 255, groups = {Default.class})
    @Schema(description = "The process name that first performed persistence.")
    String getRegisterProcessName();

    /**
     * Get date-time of last persistence.
     *
     * @return last persistence date-time
     * @since 1.0.0
     */
    @NotNull(groups = {Default.class})
    @TimeRange(groups = {Default.class})
    @TimeAccuracy(groups = {Default.class})
    @Schema(description = "The date-time of last persistence.")
    OffsetDateTime getUpdateTime();

    /**
     * Get account that last performed persistence.
     *
     * @return account id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 255)
    @Schema(description = "The account id that last performed persistence.")
    String getUpdateAccountId();

    /**
     * Get process that last performed persistence.
     *
     * @return process name
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 255)
    @Schema(description = "The process name that last performed persistence.")
    String getUpdateProcessName();

    /**
     * JSON deserializer for {@code PersistenceContext}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<PersistenceContext> {

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
        public PersistenceContext deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return ctx.deserialize(Bean.class, parser);
        }

        /**
         * {@code PersistenceContext} for JSON deserialization.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements PersistenceContext {

            private int version;
            private OffsetDateTime registerTime;
            private String registerAccountId;
            private String registerProcessName;
            private OffsetDateTime updateTime;
            private String updateAccountId;
            private String updateProcessName;

            /**
             * Constructor just for JSON deserialization.
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
            public int getVersion() {
                return version;
            }

            /**
             * Set the persistence version number.
             *
             * @param version version number
             * @since 1.0.0
             */
            public void setVersion(int version) {
                this.version = version;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public OffsetDateTime getRegisterTime() {
                return registerTime;
            }

            /**
             * Set date-time of first persistence.
             *
             * @param registerTime first persistence date-time
             * @since 1.0.0
             */
            public void setRegisterTime(OffsetDateTime registerTime) {
                this.registerTime = registerTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getRegisterAccountId() {
                return registerAccountId;
            }

            /**
             * Set account that first performed persistence.
             *
             * @param registerAccountId account id
             * @since 1.0.0
             */
            public void setRegisterAccountId(String registerAccountId) {
                this.registerAccountId = registerAccountId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getRegisterProcessName() {
                return registerProcessName;
            }

            /**
             * Set process that first performed persistence.
             *
             * @param registerProcessName process name
             * @since 1.0.0
             */
            public void setRegisterProcessName(String registerProcessName) {
                this.registerProcessName = registerProcessName;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public OffsetDateTime getUpdateTime() {
                return updateTime;
            }

            /**
             * Set date-time of last persistence.
             *
             * @param updateTime last persistence date-time
             * @since 1.0.0
             */
            public void setUpdateTime(OffsetDateTime updateTime) {
                this.updateTime = updateTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getUpdateAccountId() {
                return updateAccountId;
            }

            /**
             * Set account that last performed persistence.
             *
             * @param updateAccountId account id
             * @since 1.0.0
             */
            public void setUpdateAccountId(String updateAccountId) {
                this.updateAccountId = updateAccountId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getUpdateProcessName() {
                return updateProcessName;
            }

            /**
             * Set process that last performed persistence.
             *
             * @param updateProcessName process name
             * @since 1.0.0
             */
            public void setUpdateProcessName(String updateProcessName) {
                this.updateProcessName = updateProcessName;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "PersistenceContext{" + "version=" + version + ", registerTime=" + registerTime
                        + ", registerAccountId=" + registerAccountId + ", registerProcessName=" + registerProcessName
                        + ", updateTime=" + updateTime + ", updateAccountId=" + updateAccountId
                        + ", updateProcessName=" + updateProcessName + '}';
            }
        }
    }
}
