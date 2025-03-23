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
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Objects;
import java.util.SequencedSet;
import jp.mydns.projectk.safi.constant.ScheduleTriggerKing;
import jp.mydns.projectk.safi.util.CollectionUtils;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedDayOfWeekSetAdapter;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedIntegerSetAdapter;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedMonthSetAdapter;

/**
 * Weekdays schedule trigger configuration.
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
interface WeekdaysTriggerValue extends ScheduleTriggerValue {
    
    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    @Schema(type = "string", defaultValue = "WEEKDAYS")
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
     * Get target week numbers of scheduling.
     *
     * @return target week numbers of scheduling
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Schema(description = "Target week numbers of schedulinge.")
    SequencedSet<@NotNull(groups = {Default.class}) @Min(value = 1, groups = {Default.class})
    @Max(value = 5, groups = {Default.class}) Integer> getWeeks();

    /**
     * Get target weekdays of scheduling.
     *
     * @return target weekdays of scheduling
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Schema(description = "Target weekdays of schedulinge.")
    SequencedSet<@NotNull(groups = {Default.class}) DayOfWeek> getWeekdays();

    /**
     * Builder of the {@code WeekdaysTriggerValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, WeekdaysTriggerValue> {

        private SequencedSet<Month> months;
        private SequencedSet<Integer> weeks;
        private SequencedSet<DayOfWeek> weekdays;

        /**
         * Constructor.
         *
         * @since 3.0.0
         */
        public Builder() {
            super(Builder.class, ScheduleTriggerKing.WEEKDAYS);
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
        public Builder with(WeekdaysTriggerValue src) {
            super.with(Objects.requireNonNull(src));

            this.months = CollectionUtils.toUnmodifiable(src.getMonths());
            this.weeks = CollectionUtils.toUnmodifiable(src.getWeeks());
            this.weekdays = CollectionUtils.toUnmodifiable(src.getWeekdays());

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
         * Set target week numbers of scheduling.
         *
         * @param weeks target week numbers of scheduling
         * @return updated this
         * @since 3.0.0
         */
        public Builder withWeeks(SequencedSet<Integer> weeks) {
            this.weeks = CollectionUtils.toUnmodifiable(weeks);
            return this;
        }

        /**
         * Set target weekdays of scheduling.
         *
         * @param weekdays target weekdays of scheduling
         * @return updated this
         * @since 3.0.0
         */
        public Builder withWeekdays(SequencedSet<DayOfWeek> weekdays) {
            this.weekdays = CollectionUtils.toUnmodifiable(weekdays);
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public WeekdaysTriggerValue unsafeBuild() {
            return new Builder.Bean(this);
        }

        protected static class Bean extends AbstractBuilder.AbstractBean implements WeekdaysTriggerValue {

            private SequencedSet<Month> months;
            private SequencedSet<Integer> weeks;
            private SequencedSet<DayOfWeek> weekdays;

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
                this.weeks = builder.weeks;
                this.weekdays = builder.weekdays;
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
            public SequencedSet<Integer> getWeeks() {
                return weeks;
            }

            /**
             * Set target week numbers of scheduling. The empty means all. If it exceeds the maximum, it is treated as
             * the maximum.
             *
             * @param weeks target week numbers of scheduling
             * @since 3.0.0
             */
            @JsonbTypeAdapter(SequencedIntegerSetAdapter.class)
            public void setWeeks(SequencedSet<Integer> weeks) {
                this.weeks = CollectionUtils.toUnmodifiable(weeks);
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTypeAdapter(SequencedDayOfWeekSetAdapter.class)
            public SequencedSet<DayOfWeek> getWeekdays() {
                return weekdays;
            }

            /**
             * Set target weekdays of scheduling. The empty means all.
             *
             * @param weekdays target weekdays of scheduling
             * @since 3.0.0
             */
            @JsonbTypeAdapter(SequencedDayOfWeekSetAdapter.class)
            public void setWeekdays(SequencedSet<DayOfWeek> weekdays) {
                this.weekdays = CollectionUtils.toUnmodifiable(weekdays);
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return String.format("WeekdaysTriggerValue{months=%s, weeks=%s, weekdays=%s, anchorTime=%s}",
                    months, weeks, weekdays, anchorTime);
            }
        }
    }
}
