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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.time.Duration;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.ScheduleTriggerKing;
import jp.mydns.projectk.safi.validator.PositiveOrZeroDuration;
import jp.mydns.projectk.safi.validator.TimeAccuracy;

/**
 * Schedule cancelling trigger configuration.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/../schemas/schedule-trigger.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Schema(name = "ScheduleTrigger.Cancel", description = "Schedule cancelling trigger configuration.")
interface CancelTriggerValue extends ScheduleTriggerValue {

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    @Schema(type = "string", allowableValues = "CANCEL")
    ScheduleTriggerKing getKind();

    /**
     * Get schedule canceling duration.
     *
     * @return schedule canceling duration
     * @since 3.0.0
     */
    @Schema(type = "string", example = "PT24H", description
            = "Schedule canceling duration. Value equal to or greater than PT0S can be specified.")
    @NotNull(groups = {Default.class})
    @PositiveOrZeroDuration(groups = {Default.class})
    @TimeAccuracy(groups = {Default.class})
    Duration getDuration();

    /**
     * Builder of the {@code CancelTriggerValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, CancelTriggerValue> {

        private Duration duration;

        /**
         * Constructor.
         *
         * @since 3.0.0
         */
        public Builder() {
            super(Builder.class, ScheduleTriggerKing.CANCEL);
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
        public Builder with(CancelTriggerValue src) {
            super.with(Objects.requireNonNull(src));

            withDuration(src.getDuration());

            return this;
        }

        /**
         * Set schedule canceling duration.
         *
         * @param duration schedule canceling duration
         * @return updated this
         * @since 3.0.0
         */
        public Builder withDuration(Duration duration) {
            this.duration = duration;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public CancelTriggerValue unsafeBuild() {
            return new Builder.Bean(this);
        }

        protected static class Bean extends AbstractBuilder.AbstractBean implements CancelTriggerValue {

            private Duration duration;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected Bean() {
            }

            private Bean(Builder builder) {
                super(builder);
                this.duration = builder.duration;
            }

            /**
             * Get schedule canceling duration.
             *
             * @return schedule canceling duration
             * @since 3.0.0
             */
            @Override
            public Duration getDuration() {
                return duration;
            }

            /**
             * Set schedule canceling duration.
             *
             * @param duration schedule canceling duration
             * @since 3.0.0
             */
            public void setDuration(Duration duration) {
                this.duration = duration;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return String.format("CancelTriggerValue{anchorTime=%s, duration=%s}", anchorTime, duration);
            }
        }
    }
}
