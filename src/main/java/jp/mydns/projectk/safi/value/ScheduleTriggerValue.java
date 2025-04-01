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
import jp.mydns.projectk.safi.util.CollectionUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedDayOfWeekSetAdapter;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedIntegerSetAdapter;
import jp.mydns.projectk.safi.value.adapter.SequencedSetAdapter.SequencedMonthSetAdapter;

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
@Schema(name = "ScheduleTrigger", description = "Job schedule trigger configuration.",
        example = "{\"kind\": \"ONCE\", \"anchorTime\": \"2030-10-10T07:02:00Z\"}",
        examples = {
            "{\"kind\": \"DAYS\", \"anchorTime\": \"2700-10-10T07:09:42Z\", \"months\": [1, 7], \"days\": [5, 25]}",
            "{\"kind\": \"WEEKDAYS\", \"anchorTime\": \"2700-10-10T07:09:42Z\", \"months\": [], \"weeks\": [2, 5]"
            + ", \"weekdays\": [\"SATURDAY\", \"SUNDAY\"]}",
            "{\"kind\": \"ONCE\", \"anchorTime\": \"2700-10-10T07:09:42Z\"}",
            "{\"kind\": \"CANCEL\", \"anchorTime\": \"2700-10-10T07:09:42Z\", \"duration\": \"PT24H\"}"},
        oneOf = {DaysTriggerValue.class, WeekdaysTriggerValue.class, OnceTriggerValue.class, CancelTriggerValue.class})
public interface ScheduleTriggerValue extends ValueTemplate {

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
    @Schema(description
        = "Anchor time of scheduling. Values from 2000-01-01T00:00:00Z to 2999-12-31T23:59:59Z can be specified.")
    OffsetDateTime getAnchorTime();

    /**
     * Abstract builder of the {@code ScheduleTriggerValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends ScheduleTriggerValue>
        extends ValueTemplate.AbstractBuilder<B, V> {

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
            super(builderType);
            this.kind = Objects.requireNonNull(kind);
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 3.0.0
         */
        @Override
        public B with(V src) {
            super.with(Objects.requireNonNull(src));

            withAnchorTime(src.getAnchorTime());

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
             * Get target months of scheduling.
             *
             * @return target months of scheduling
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
             * Get target week numbers of scheduling.
             *
             * @return target week numbers of scheduling
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
             * Get target weekdays of scheduling.
             *
             * @return target weekdays of scheduling
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
             * Get target days of scheduling.
             *
             * @return target days of scheduling
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
        }
    }
}
