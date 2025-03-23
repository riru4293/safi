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
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.SequencedSet;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.ScheduleTriggerKing;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.adapter.AbstractSetAdapter.SetDayOfWeekAdapter;
import jp.mydns.projectk.safi.value.adapter.AbstractSetAdapter.SetIntegerAdapter;
import jp.mydns.projectk.safi.value.adapter.AbstractSetAdapter.SetMonthAdapter;

/**
 * <i>Job</i> schedule trigger configuration.
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
@JsonbTypeDeserializer(ScheduleTriggerValue.Deserializer.class)
@Schema(name = "SchedduleTrigger", description = "Job schedule trigger configuration.",
        example = "{\"kind\": \"ONCE\", \"anchorTime\": \"2700-10-10T07:09:42Z\"}",
        oneOf = {ScheduleTriggerValue.DaysTriggerValue.class, ScheduleTriggerValue.WeekdaysTriggerValue.class,
            ScheduleTriggerValue.OnceTriggerValue.class, ScheduleTriggerValue.CancelTriggerValue.class})
public interface ScheduleTriggerValue {

    /**
     * Get schedule trigger configuration kind.
     *
     * @return the {@code ScheduleTriggerKing}
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Schema(description = "Schedule trigger configuration kind.")
    ScheduleTriggerKing getKind();

    /**
     * Get anchor time of scheduling.
     *
     * @return anchor time of scheduling
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @TimeRange(groups = {Default.class})
    @TimeAccuracy(groups = {Default.class})
    @Schema(description = "Anchor time of scheduling.")
    OffsetDateTime getAnchorTime();

    /**
     * Days schedule trigger configuration.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface DaysTriggerValue extends ScheduleTriggerValue {

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
                this.months = src.getMonths();
                this.days = src.getDays();
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
                this.months = months;
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
                this.days = days;
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
                @JsonbTypeAdapter(SetMonthAdapter.class)
                public SequencedSet<Month> getMonths() {
                    return months;
                }

                /**
                 * Set target months of scheduling. The empty means all.
                 *
                 * @param months target months of scheduling
                 * @since 3.0.0
                 */
                @JsonbTypeAdapter(SetMonthAdapter.class)
                public void setMonths(SequencedSet<Month> months) {
                    this.months = months;
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                @JsonbTypeAdapter(SetIntegerAdapter.class)
                public SequencedSet<Integer> getDays() {
                    return days;
                }

                /**
                 * Set target day numbers of scheduling. The empty means all.
                 *
                 * @param days target day numbers of scheduling
                 * @since 3.0.0
                 */
                @JsonbTypeAdapter(SetIntegerAdapter.class)
                public void setDays(SequencedSet<Integer> days) {
                    this.days = days;
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

    /**
     * Weekdays schedule trigger configuration.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface WeekdaysTriggerValue extends ScheduleTriggerValue {

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

                this.months = src.getMonths();
                this.weeks = src.getWeeks();
                this.weekdays = src.getWeekdays();

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
                this.months = months;
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
                this.weeks = weeks;
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
                this.weekdays = weekdays;
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
                @JsonbTypeAdapter(SetMonthAdapter.class)
                public SequencedSet<Month> getMonths() {
                    return months;
                }

                /**
                 * Set target months of scheduling. The empty means all.
                 *
                 * @param months target months of scheduling
                 * @since 3.0.0
                 */
                @JsonbTypeAdapter(SetMonthAdapter.class)
                public void setMonths(SequencedSet<Month> months) {
                    this.months = months;
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                @JsonbTypeAdapter(SetIntegerAdapter.class)
                public SequencedSet<Integer> getWeeks() {
                    return weeks;
                }

                /**
                 * Set target week numbers of scheduling. The empty means all. If it exceeds the maximum, it is treated
                 * as the maximum.
                 *
                 * @param weeks target week numbers of scheduling
                 * @since 3.0.0
                 */
                @JsonbTypeAdapter(SetIntegerAdapter.class)
                public void setWeeks(SequencedSet<Integer> weeks) {
                    this.weeks = weeks;
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                @JsonbTypeAdapter(SetDayOfWeekAdapter.class)
                public SequencedSet<DayOfWeek> getWeekdays() {
                    return weekdays;
                }

                /**
                 * Set target weekdays of scheduling. The empty means all.
                 *
                 * @param weekdays target weekdays of scheduling
                 * @since 3.0.0
                 */
                @JsonbTypeAdapter(SetDayOfWeekAdapter.class)
                public void setWeekdays(SequencedSet<DayOfWeek> weekdays) {
                    this.weekdays = weekdays;
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

    /**
     * Onetime schedule trigger configuration.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface OnceTriggerValue extends ScheduleTriggerValue {

        /**
         * Builder of the {@code OnceTriggerValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        class Builder extends AbstractBuilder<Builder, OnceTriggerValue> {

            /**
             * Constructor.
             *
             * @since 3.0.0
             */
            public Builder() {
                super(Builder.class, ScheduleTriggerKing.ONCE);
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public OnceTriggerValue unsafeBuild() {
                return new Builder.Bean(this);
            }

            protected static class Bean extends AbstractBuilder.AbstractBean implements OnceTriggerValue {

                /**
                 * Constructor. Used only for deserialization from JSON.
                 *
                 * @since 3.0.0
                 */
                protected Bean() {
                }

                private Bean(Builder builder) {
                    super(builder);
                }

                /**
                 * Returns a string representation.
                 *
                 * @return a string representation
                 * @since 3.0.0
                 */
                @Override
                public String toString() {
                    return String.format("OnceTriggerValue{anchorTime=%s}", anchorTime);
                }
            }
        }
    }

    /**
     * Schedule cancelling trigger configuration.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface CancelTriggerValue extends ScheduleTriggerValue {

        /**
         * Get schedule canceling duration.
         *
         * @return schedule canceling duration
         * @since 3.0.0
         */
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
                this.duration = src.getDuration();
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

    /**
     * Abstract builder of the {@code ScheduleTriggerValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends ScheduleTriggerValue> {

        protected final Class<B> builderType;
        protected final ScheduleTriggerKing kind;
        protected OffsetDateTime anchorTime;

        /**
         * Constructor.
         *
         * @param builderType builder type
         * @param kind the {@code ScheduleTriggerKing}
         * @throws NullPointerException if any argument is {@code null}
         * @since 3.0.0
         */
        protected AbstractBuilder(Class<B> builderType, ScheduleTriggerKing kind) {
            this.builderType = Objects.requireNonNull(builderType);
            this.kind = Objects.requireNonNull(kind);
        }

        /**
         * Set all properties from {@code src}.
         *
         * @param src source value
         * @return updated this
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 3.0.0
         */
        public B with(V src) {
            this.anchorTime = Objects.requireNonNull(src).getAnchorTime();
            return builderType.cast(this);
        }

        /**
         * Set anchor time of scheduling.
         *
         * @param anchorTime anchor time of scheduling
         * @return updated this
         * @since 3.0.0
         */
        public B withAnchorTime(OffsetDateTime anchorTime) {
            this.anchorTime = anchorTime;
            return builderType.cast(this);
        }

        /**
         * Build a new inspected instance.
         *
         * @param validator the {@code Validator}
         * @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
         * @return new inspected instance
         * @throws NullPointerException if any argument is {@code null}
         * @throws ConstraintViolationException if occurred constraint violations when building
         * @since 3.0.0
         */
        public V build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
        }

        /**
         * Build a new instance. It instance may not meet that constraint. Use only if the original value is completely
         * reliable.
         *
         * @return new unsafe instance
         * @since 3.0.0
         */
        public abstract V unsafeBuild();

        /**
         * Abstract implements of the {@code ScheduleTriggerValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected abstract static class AbstractBean implements ScheduleTriggerValue {

            protected ScheduleTriggerKing kind;
            protected OffsetDateTime anchorTime;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected AbstractBean() {
            }

            /**
             * Constructor.
             *
             * @param builder the {@code SchedefValue.AbstractBuilder}
             * @since 3.0.0
             */
            protected AbstractBean(AbstractBuilder<?, ?> builder) {
                this.kind = builder.kind;
                this.anchorTime = builder.anchorTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public ScheduleTriggerKing getKind() {
                return kind;
            }

            /**
             * Set schedule trigger configuration kind.
             *
             * @param kind schedule trigger configuration kind
             * @since 3.0.0
             */
            public void setKind(ScheduleTriggerKing kind) {
                this.kind = kind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public OffsetDateTime getAnchorTime() {
                return anchorTime;
            }

            /**
             * Set anchor time of scheduling.
             *
             * @param anchorTime anchor time of scheduling
             * @since 3.0.0
             */
            public void setAnchorTime(OffsetDateTime anchorTime) {
                this.anchorTime = anchorTime;
            }
        }
    }

    /**
     * JSON deserializer for {@code ScheduleTriggerValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<ScheduleTriggerValue> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public ScheduleTriggerValue deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            Bean trigger = ctx.deserialize(Bean.class, parser);

            return switch (trigger.getKind()) {
                case null ->
                    trigger;    // Note: To be ocurrs constraint violation.
                case DAYS ->
                    new DaysTriggerValue.Builder().with(trigger).unsafeBuild();
                case WEEKDAYS ->
                    new WeekdaysTriggerValue.Builder().with(trigger).unsafeBuild();
                case ONCE ->
                    new OnceTriggerValue.Builder().with(trigger).unsafeBuild();
                case CANCEL ->
                    new CancelTriggerValue.Builder().with(trigger).unsafeBuild();
            };
        }

        protected static class Bean implements DaysTriggerValue, WeekdaysTriggerValue, OnceTriggerValue,
            CancelTriggerValue {

            private ScheduleTriggerKing kind;
            private OffsetDateTime anchorTime;
            private Duration duration;
            private SequencedSet<Month> months;
            private SequencedSet<Integer> weeks;
            private SequencedSet<DayOfWeek> weekdays;
            private SequencedSet<Integer> days;

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public ScheduleTriggerKing getKind() {
                return kind;
            }

            /**
             * Set schedule trigger configuration kind.
             *
             * @param kind schedule trigger configuration kind
             * @since 3.0.0
             */
            public void setKind(ScheduleTriggerKing kind) {
                this.kind = kind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public OffsetDateTime getAnchorTime() {
                return anchorTime;
            }

            /**
             * Set anchor time of scheduling.
             *
             * @param anchorTime anchor time of scheduling
             * @since 3.0.0
             */
            public void setAnchorTime(OffsetDateTime anchorTime) {
                this.anchorTime = anchorTime;
            }

            /**
             * {@inheritDoc}
             *
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
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTypeAdapter(SetMonthAdapter.class)
            public SequencedSet<Month> getMonths() {
                return months;
            }

            /**
             * Set target months of scheduling. The empty means all.
             *
             * @param months target months of scheduling
             * @since 3.0.0
             */
            @JsonbTypeAdapter(SetMonthAdapter.class)
            public void setMonths(SequencedSet<Month> months) {
                this.months = months;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTypeAdapter(SetIntegerAdapter.class)
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
            @JsonbTypeAdapter(SetIntegerAdapter.class)
            public void setWeeks(SequencedSet<Integer> weeks) {
                this.weeks = weeks;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTypeAdapter(SetDayOfWeekAdapter.class)
            public SequencedSet<DayOfWeek> getWeekdays() {
                return weekdays;
            }

            /**
             * Set target weekdays of scheduling. The empty means all.
             *
             * @param weekdays target weekdays of scheduling
             * @since 3.0.0
             */
            @JsonbTypeAdapter(SetDayOfWeekAdapter.class)
            public void setWeekdays(SequencedSet<DayOfWeek> weekdays) {
                this.weekdays = weekdays;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTypeAdapter(SetIntegerAdapter.class)
            public SequencedSet<Integer> getDays() {
                return days;
            }

            /**
             * Set target day numbers of scheduling. The empty means all.
             *
             * @param days target day numbers of scheduling
             * @since 3.0.0
             */
            @JsonbTypeAdapter(SetIntegerAdapter.class)
            public void setDays(SequencedSet<Integer> days) {
                this.days = days;
            }
        }
    }
}
