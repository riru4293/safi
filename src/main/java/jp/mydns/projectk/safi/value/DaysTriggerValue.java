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
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.time.Month;
import java.util.Objects;
import java.util.SequencedSet;
import jp.mydns.projectk.safi.constant.ScheduleTriggerKing;
import jp.mydns.projectk.safi.util.CollectionUtils;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedIntegerSetAdapter;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedMonthSetAdapter;

/**
 * Days schedule trigger configuration.
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
interface DaysTriggerValue extends ScheduleTriggerValue {
    
    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    @Schema(type = "string", defaultValue = "DAYS")
    ScheduleTriggerKing getKind();

    /**
     * Get target months of scheduling.
     *
     * @return target months of scheduling
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Schema(description = "Target months of schedulinge.")
    SequencedSet<@NotNull(groups = {Default.class}) Month> getMonths();

    /**
     * Get target days of scheduling.
     *
     * @return target days of scheduling
     * @since 3.0.0
     */
    @Schema(description = "Target days of schedulinge.")
    SequencedSet<@NotNull(groups = {Default.class}) @Min(value = 1, groups = {Default.class})
    @Max(value = 31, groups = {Default.class}) Integer> getDays();

    /**
     * Builder of the {@code DaysTriggerValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, DaysTriggerValue> {

        private SequencedSet<Month> months;
        private SequencedSet<Integer> days;

        /**
         * Constructor.
         *
         * @since 3.0.0
         */
        public Builder() {
            super(Builder.class, ScheduleTriggerKing.DAYS);
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
        public Builder with(DaysTriggerValue src) {
            super.with(Objects.requireNonNull(src));
            this.months = CollectionUtils.toUnmodifiable(src.getMonths());
            this.days = CollectionUtils.toUnmodifiable(src.getDays());
            return this;
        }

        /**
         * Set target months of scheduling.
         *
         * @param months target months of scheduling
         * @return updated this
         * @since 3.0.0
         */
        public Builder withMonths(SequencedSet<Month> months) {
            this.months = CollectionUtils.toUnmodifiable(months);
            return this;
        }

        /**
         * Set target day numbers of scheduling.
         *
         * @param days target day numbers of scheduling
         * @return updated this
         * @since 3.0.0
         */
        public Builder withDays(SequencedSet<Integer> days) {
            this.days = CollectionUtils.toUnmodifiable(days);
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public DaysTriggerValue unsafeBuild() {
            return new Builder.Bean(this);
        }

        protected static class Bean extends AbstractBuilder.AbstractBean implements DaysTriggerValue {

            private SequencedSet<Month> months;
            private SequencedSet<Integer> days;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected Bean() {
            }

            private Bean(Builder builder) {
                super(builder);
                this.months = builder.months;
                this.days = builder.days;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTypeAdapter(SequencedMonthSetAdapter.class)
            public SequencedSet<Month> getMonths() {
                return months;
            }

            /**
             * Set target months of scheduling. The empty means all.
             *
             * @param months target months of scheduling
             * @since 3.0.0
             */
            @JsonbTypeAdapter(SequencedMonthSetAdapter.class)
            public void setMonths(SequencedSet<Month> months) {
                this.months = CollectionUtils.toUnmodifiable(months);
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTypeAdapter(SequencedIntegerSetAdapter.class)
            public SequencedSet<Integer> getDays() {
                return days;
            }

            /**
             * Set target day numbers of scheduling. The empty means all.
             *
             * @param days target day numbers of scheduling
             * @since 3.0.0
             */
            @JsonbTypeAdapter(SequencedIntegerSetAdapter.class)
            public void setDays(SequencedSet<Integer> days) {
                this.days = CollectionUtils.toUnmodifiable(days);
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return String.format("DaysTriggerValue{months=%s, days=%s, anchorTime=%s}",
                    months, days, anchorTime);
            }
        }
    }
}
