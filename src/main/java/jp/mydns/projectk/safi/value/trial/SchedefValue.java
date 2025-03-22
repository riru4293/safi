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
package jp.mydns.projectk.safi.value.trial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.ScheduleTriggerKing;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.NamedValue;

/**
 * Definition for <i>Job</i> scheduling. With this definition, the schedule that can be created is limited to the period
 * between {@literal 2000-01-01T00:00:00Z} and {@literal 2999-12-31T23:59:59Z}.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/../schemas/schedef.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(SchedefValue.Deserializer.class)
@Schema(name = "Schedef", description = "Definition for Job scheduling.")
public interface SchedefValue extends NamedValue {

    /**
     * Get schedule definition id.
     *
     * @return schedule definition id
     * @since 3.0.0
     */
    @NotNull
    @Size(min = 1, max = 36)
    @Schema(description = "Schedule definition id.", example = "test")
    String getId();

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Size(min = 1, max = 36, groups = {Default.class})
    @Schema(description = "Job definition id.", example = "test")
    String getJobdefId();

    /**
     * Get scheduling priority. The larger the number, the higher the priority.
     *
     * @return scheduling priority
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Size(min = 1, max = 1, groups = {Default.class})
    @Schema(description = "Scheduling priority. The larger the number, the higher the priority.", example = "F")
    String getPriority();

    /**
     * Get schedule trigger configuration.
     *
     * @return schedule trigger configuration
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Valid
    Trigger getTrigger();

    /**
     * Schedule trigger configuration.
     *
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * <li>This and JSON can be converted bidirectionally.</li>
     * </ul>
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @JsonbTypeDeserializer(SchedefValue.Trigger.Deserializer.class)
    @Schema(name = "ScheduleTrigger", description = "Schedule trigger configuration.",
            example = "{\"kind\": \"ONCE\", \"anchorTime\": \"2700-10-10T07:09:42Z\"}",
            oneOf = {Trigger.DailyTrigger.class, Trigger.WeeklyTrigger.class, Trigger.MonthlyDaysTrigger.class,
                Trigger.MonthlyWeekDaysTrigger.class, Trigger.OnceTrigger.class, Trigger.CancelTrigger.class})
    interface Trigger {

        /**
         * Get schedule definition kind.
         *
         * @return the {@code ScheduleTriggerKing}
         * @since 3.0.0
         */
        @NotNull(groups = {Default.class})
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
        OffsetDateTime getAnchorTime();

        /**
         * Daily schedule trigger configuration.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        interface DailyTrigger extends Trigger {

            /**
             * Builder of the {@code DailyTrigger}.
             *
             * @author riru
             * @version 3.0.0
             * @since 3.0.0
             */
            class Builder extends AbstractBuilder<Builder, DailyTrigger> {

                /**
                 * Constructor.
                 *
                 * @since 3.0.0
                 */
                public Builder() {
                    super(Builder.class, ScheduleTriggerKing.DAILY);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @throws NullPointerException if any argument is {@code null}
                 * @throws ConstraintViolationException if occurred constraint violations when building
                 * @since 3.0.0
                 */
                @Override
                public DailyTrigger build(Validator validator, Class<?>... groups) {
                    return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public DailyTrigger unsafeBuild() {
                    return new Builder.Bean(this);
                }

                protected static class Bean extends AbstractBuilder.AbstractBean implements DailyTrigger {

                    protected Bean() {
                    }

                    protected Bean(Builder builder) {
                        super(builder);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(anchorTime);
                    }

                    @Override
                    public boolean equals(Object other) {
                        return other instanceof DailyTrigger o && Objects.equals(anchorTime, o.getAnchorTime());
                    }

                    @Override
                    public String toString() {
                        return String.format("DailyTrigger{anchorTime=%s}", anchorTime);
                    }
                }
            }
        }

        /**
         * Weekly schedule trigger configuration.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        interface WeeklyTrigger extends Trigger {

            /**
             * Get target weekDays of scheduling.
             *
             * @return target weekDays of scheduling
             * @since 3.0.0
             */
            @NotNull(groups = {Default.class})
            Set<@NotNull(groups = {Default.class}) DayOfWeek> getWeekDays();

            /**
             * Builder of the {@code WeeklyTrigger}.
             *
             * @author riru
             * @version 3.0.0
             * @since 3.0.0
             */
            class Builder extends AbstractBuilder<Builder, WeeklyTrigger> {

                private Set<DayOfWeek> weekDays;

                /**
                 * Constructor.
                 *
                 * @since 3.0.0
                 */
                public Builder() {
                    super(Builder.class, ScheduleTriggerKing.WEEKLY);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public Builder with(WeeklyTrigger src) {
                    super.with(src);
                    this.weekDays = src.getWeekDays();
                    return this;
                }

                /**
                 * Set target weekDays of scheduling.
                 *
                 * @param weekDays target weekDays of scheduling
                 * @return updated this
                 * @since 3.0.0
                 */
                public Builder withWeekDays(Set<DayOfWeek> weekDays) {
                    this.weekDays = weekDays;
                    return this;
                }

                /**
                 * {@inheritDoc}
                 *
                 * @throws NullPointerException if any argument is {@code null}
                 * @throws ConstraintViolationException if occurred constraint violations when building
                 * @since 3.0.0
                 */
                @Override
                public WeeklyTrigger build(Validator validator, Class<?>... groups) {
                    return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public WeeklyTrigger unsafeBuild() {
                    return new Builder.Bean(this);
                }

                protected static class Bean extends AbstractBuilder.AbstractBean implements WeeklyTrigger {

                    private Set<DayOfWeek> weekDays;

                    protected Bean() {
                    }

                    protected Bean(Builder builder) {
                        super(builder);
                        this.weekDays = builder.weekDays;
                    }

                    @Override
                    public Set<DayOfWeek> getWeekDays() {
                        return weekDays;
                    }

                    public void setWeekDays(Set<DayOfWeek> weekDays) {
                        this.weekDays = weekDays;
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(weekDays, anchorTime);
                    }

                    @Override
                    public boolean equals(Object other) {
                        return other instanceof WeeklyTrigger o && Objects.equals(weekDays, o.getWeekDays())
                            && Objects.equals(anchorTime, o.getAnchorTime());
                    }

                    @Override
                    public String toString() {
                        return String.format("WeeklyTrigger{weekDays=%s, anchorTime=%s}", weekDays, anchorTime);
                    }
                }
            }
        }

        /**
         * Monthly schedule trigger configuration.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        interface MonthlyTrigger extends Trigger {

            /**
             * Get target months of scheduling.
             *
             * @return target months of scheduling
             * @since 3.0.0
             */
            @NotNull(groups = {Default.class})
            Set<@NotNull(groups = {Default.class}) Month> getMonths();

            /**
             * Abstract builder of the {@code MonthlyTrigger}.
             *
             * @param <B> builder type
             * @param <V> value type
             * @author riru
             * @version 3.0.0
             * @since 3.0.0
             */
            abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends MonthlyTrigger>
                extends Trigger.AbstractBuilder<B, V> {

                protected Set<Month> months;

                protected AbstractBuilder(Class<B> builderType, ScheduleTriggerKing kind) {
                    super(builderType, kind);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @throws NullPointerException if {@code src} is {@code null}
                 * @since 3.0.0
                 */
                @Override
                public B with(V src) {
                    super.with(src);
                    this.months = src.getMonths();
                    return builderType.cast(this);
                }

                /**
                 * Set target months of scheduling.
                 *
                 * @param months target weekDays of scheduling
                 * @return updated this
                 * @since 3.0.0
                 */
                public B withMonths(Set<Month> months) {
                    this.months = months;
                    return builderType.cast(this);
                }

                protected abstract static class AbstractBean extends Trigger.AbstractBuilder.AbstractBean
                    implements MonthlyTrigger {

                    protected Set<Month> months;

                    protected AbstractBean() {
                    }

                    protected AbstractBean(MonthlyTrigger.AbstractBuilder<?, ?> builder) {
                        super(builder);
                        this.months = builder.months;
                    }

                    @Override
                    public Set<Month> getMonths() {
                        return months;
                    }

                    public void setMonths(Set<Month> months) {
                        this.months = months;
                    }
                }
            }
        }

        /**
         * Monthly days schedule trigger configuration.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        interface MonthlyDaysTrigger extends MonthlyTrigger {

            /**
             * Get target days of scheduling.
             *
             * @return target days of scheduling
             * @since 3.0.0
             */
            Set<@NotNull(groups = {Default.class}) @Min(value = 1, groups = {Default.class})
            @Max(value = 31, groups = {Default.class}) Integer> getDays();

            /**
             * Builder of the {@code MonthlyDaysTrigger}.
             *
             * @author riru
             * @version 3.0.0
             * @since 3.0.0
             */
            class Builder extends AbstractBuilder<Builder, MonthlyDaysTrigger> {

                private Set<Integer> days;

                /**
                 * Constructor.
                 *
                 * @since 3.0.0
                 */
                public Builder() {
                    super(Builder.class, ScheduleTriggerKing.MONTHLY_DAYS);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public Builder with(MonthlyDaysTrigger src) {
                    super.with(src);
                    this.days = src.getDays();
                    return this;
                }

                /**
                 * Set target days of scheduling.
                 *
                 * @param days target weekDays of scheduling
                 * @return updated this
                 * @since 3.0.0
                 */
                public Builder withDays(Set<Integer> days) {
                    this.days = days;
                    return this;
                }

                /**
                 * {@inheritDoc}
                 *
                 * @throws NullPointerException if any argument is {@code null}
                 * @throws ConstraintViolationException if occurred constraint violations when building
                 * @since 3.0.0
                 */
                @Override
                public MonthlyDaysTrigger build(Validator validator, Class<?>... groups) {
                    return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public MonthlyDaysTrigger unsafeBuild() {
                    return new Builder.Bean(this);
                }

                protected static class Bean extends AbstractBuilder.AbstractBean implements MonthlyDaysTrigger {

                    private Set<Integer> days;

                    protected Bean() {
                    }

                    protected Bean(Builder builder) {
                        super(builder);
                        this.days = builder.days;
                    }

                    /**
                     * {@inheritDoc}
                     *
                     * @since 3.0.0
                     */
                    @Override
                    public Set<Integer> getDays() {
                        return days;
                    }

                    /**
                     * Set target days of scheduling.
                     *
                     * @param days target days of scheduling
                     * @since 3.0.0
                     */
                    public void setDays(Set<Integer> days) {
                        this.days = days;
                    }

                    /**
                     * Returns a hash code value.
                     *
                     * @return a hash code value
                     * @since 3.0.0
                     */
                    @Override
                    public int hashCode() {
                        return Objects.hash(months, days, anchorTime);
                    }

                    /**
                     * Indicates that specified object is equal duration this one.
                     *
                     * @param other an any object
                     * @return {@code true} if matches otherwise {@code false}.
                     * @since 3.0.0
                     */
                    @Override
                    public boolean equals(Object other) {
                        return other instanceof MonthlyDaysTrigger o && Objects.equals(months, o.getMonths())
                            && Objects.equals(days, o.getDays()) && Objects.equals(anchorTime, o.getAnchorTime());
                    }

                    /**
                     * Returns a string representation.
                     *
                     * @return a string representation
                     * @since 3.0.0
                     */
                    @Override
                    public String toString() {
                        return String.format("MonthlyDaysTrigger{months=%s, days=%s, anchorTime=%s}",
                            months, days, anchorTime);
                    }
                }
            }
        }

        /**
         * Monthly weekdays schedule trigger configuration.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        interface MonthlyWeekDaysTrigger extends MonthlyTrigger {

            /**
             * Get target week numbers of scheduling.
             *
             * @return target week days of scheduling
             * @since 3.0.0
             */
            @NotNull(groups = {Default.class})
            Set<@NotNull(groups = {Default.class}) @Min(value = 1, groups = {Default.class})
            @Max(value = 5, groups = {Default.class}) Integer> getWeeks();

            /**
             * Get target week days of scheduling.
             *
             * @return target week days of scheduling
             * @since 3.0.0
             */
            Set<DayOfWeek> getWeekDays();

            /**
             * Builder of the {@code MonthlyWeekDaysTrigger}.
             *
             * @author riru
             * @version 3.0.0
             * @since 3.0.0
             */
            class Builder extends AbstractBuilder<Builder, MonthlyWeekDaysTrigger> {

                private Set<Integer> weeks;
                private Set<DayOfWeek> weekDays;

                /**
                 * Constructor.
                 *
                 * @since 3.0.0
                 */
                public Builder() {
                    super(Builder.class, ScheduleTriggerKing.MONTHLY_WEEKDAYS);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public Builder with(MonthlyWeekDaysTrigger src) {
                    super.with(src);
                    this.weeks = src.getWeeks();
                    this.weekDays = src.getWeekDays();
                    return this;
                }

                /**
                 * Set target weeks of scheduling.
                 *
                 * @param weeks target weeks of scheduling
                 * @return updated this
                 * @since 3.0.0
                 */
                public Builder withWeeks(Set<Integer> weeks) {
                    this.weeks = weeks;
                    return this;
                }

                /**
                 * Set target week days of scheduling.
                 *
                 * @param weekDays target week days of scheduling
                 * @return updated this
                 * @since 3.0.0
                 */
                public Builder withWeekDays(Set<DayOfWeek> weekDays) {
                    this.weekDays = weekDays;
                    return this;
                }

                /**
                 * {@inheritDoc}
                 *
                 * @throws NullPointerException if any argument is {@code null}
                 * @throws ConstraintViolationException if occurred constraint violations when building
                 * @since 3.0.0
                 */
                @Override
                public MonthlyWeekDaysTrigger build(Validator validator, Class<?>... groups) {
                    return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public MonthlyWeekDaysTrigger unsafeBuild() {
                    return new Builder.Bean(this);
                }

                protected static class Bean extends AbstractBuilder.AbstractBean implements MonthlyWeekDaysTrigger {

                    private Set<Integer> weeks;
                    private Set<DayOfWeek> weekDays;

                    protected Bean() {
                    }

                    protected Bean(Builder builder) {
                        super(builder);
                        this.weeks = builder.weeks;
                        this.weekDays = builder.weekDays;
                    }

                    @Override
                    public Set<Integer> getWeeks() {
                        return weeks;
                    }

                    public void setWeeks(Set<Integer> weeks) {
                        this.weeks = weeks;
                    }

                    @Override
                    public Set<DayOfWeek> getWeekDays() {
                        return weekDays;
                    }

                    public void setWeekDays(Set<DayOfWeek> weekDays) {
                        this.weekDays = weekDays;
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(months, weeks, weekDays, anchorTime);
                    }

                    @Override
                    public boolean equals(Object other) {
                        return other instanceof MonthlyWeekDaysTrigger o && Objects.equals(months, o.getMonths())
                            && Objects.equals(weeks, o.getWeeks()) && Objects.equals(weekDays, o.getWeekDays())
                            && Objects.equals(anchorTime, o.getAnchorTime());
                    }

                    @Override
                    public String toString() {
                        return "MonthlyWeekDaysTrigger{months=" + months + "weeks=" + weeks + "weekDays=" + weekDays
                            + "anchorTime=" + anchorTime + '}';
                    }
                }
            }
        }

        interface OnceTrigger extends Trigger {

        }

        interface CancelTrigger extends Trigger {

            Duration getDuration();
        }

        /**
         * Abstract builder of the {@code Trigger}.
         *
         * @param <B> builder type
         * @param <V> value type
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends Trigger> {

            protected final Class<B> builderType;
            protected final ScheduleTriggerKing kind;
            protected OffsetDateTime anchorTime;

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
            public abstract V build(Validator validator, Class<?>... groups);

            /**
             * Build a new instance. It instance may not meet that constraint. Use only if the original value is
             * completely reliable.
             *
             * @return new unsafe instance
             * @since 3.0.0
             */
            public abstract V unsafeBuild();

            /**
             * Abstract implements of the {@code Trigger}.
             *
             * @author riru
             * @version 3.0.0
             * @since 3.0.0
             */
            protected abstract static class AbstractBean implements Trigger {

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
                 * Set schedule definition kind.
                 *
                 * @param kind schedule definition kind
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
         * JSON deserializer for {@code Trigger}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        class Deserializer implements JsonbDeserializer<Trigger> {

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Trigger deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
                Bean tmp = ctx.deserialize(Bean.class, parser);

                return switch (tmp.getKind()) {
                    case null ->
                        tmp;
                    case DAILY ->
                        new DailyTrigger.Builder().withAnchorTime(tmp.getAnchorTime()).unsafeBuild();
                    default ->
                        throw new UnsupportedOperationException();
                };
            }

            protected class Bean implements DailyTrigger, WeeklyTrigger, MonthlyDaysTrigger, MonthlyWeekDaysTrigger,
                OnceTrigger, CancelTrigger {

                private ScheduleTriggerKing kind;
                private Duration duration;
                private Set<Month> months;
                private Set<Integer> weeks;
                private Set<DayOfWeek> weekDays;
                private Set<Integer> days;
                private OffsetDateTime anchorTime;

                @Override
                public ScheduleTriggerKing getKind() {
                    return kind;
                }

                public void setKind(ScheduleTriggerKing kind) {
                    this.kind = kind;
                }

                @Override
                public Duration getDuration() {
                    return duration;
                }

                public void setDuration(Duration duration) {
                    this.duration = duration;
                }

                @Override
                public Set<Month> getMonths() {
                    return months;
                }

                public void setMonths(Set<Month> months) {
                    this.months = months;
                }

                @Override
                public Set<Integer> getWeeks() {
                    return weeks;
                }

                public void setWeeks(Set<Integer> weeks) {
                    this.weeks = weeks;
                }

                @Override
                public Set<DayOfWeek> getWeekDays() {
                    return weekDays;
                }

                public void setWeekDays(Set<DayOfWeek> weekDays) {
                    this.weekDays = weekDays;
                }

                @Override
                public Set<Integer> getDays() {
                    return days;
                }

                public void setDays(Set<Integer> days) {
                    this.days = days;
                }

                @Override
                public OffsetDateTime getAnchorTime() {
                    return anchorTime;
                }

                public void setAnchorTime(OffsetDateTime anchorTime) {
                    this.anchorTime = anchorTime;
                }
            }
        }
    }

    /**
     * JSON deserializer for {@code SchedefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<SchedefValue> {

        @Override
        public SchedefValue deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
