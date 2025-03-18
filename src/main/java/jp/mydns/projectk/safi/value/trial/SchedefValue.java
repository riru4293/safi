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
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import jp.mydns.projectk.safi.constant.SchedefKing;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.value.NamedValue;

/**
 * Definition for <i>Job</i> scheduling.
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
//@JsonbTypeDeserializer(SchedefValue.Deserializer.class)
@Schema(name = "Schedef", description = "Definition for Job scheduling.")
public interface SchedefValue extends NamedValue {

    @JsonbTypeDeserializer(Trigger.Deserializer.class)
    interface Trigger {

        /**
         * Get schedule definition kind.
         *
         * @return the {@code SchedefKing}
         * @since 3.0.0
         */
        SchedefKing getKind();

        /**
         * Daily schedule trigger.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        interface Daily extends Trigger {

            /**
             * Get scheduling anchor time.
             *
             * @return scheduling anchor time
             * @since 3.0.0
             */
            OffsetDateTime getAnchorTime();

            /**
             * Get scheduling interval.
             *
             * @return scheduling interval
             * @since 3.0.0
             */
            Integer getInterval();

            /**
             * Builder of the {@code Daily}.
             *
             * @author riru
             * @version 3.0.0
             * @since 3.0.0
             */
            class Builder {

                private OffsetDateTime anchorTime;
                private Integer interval;

                /**
                 * Set scheduling anchor time.
                 *
                 * @param dateTime scheduling anchor time
                 * @return updated this
                 * @since 3.0.0
                 */
                public Builder withDateTime(OffsetDateTime dateTime) {
                    this.anchorTime = dateTime;
                    return this;
                }

                /**
                 * Set scheduling interval.
                 *
                 * @param interval scheduling interval
                 * @return updated this
                 * @since 3.0.0
                 */
                public Builder withInterval(Integer interval) {
                    this.interval = interval;
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
                 * @since 3.0.0
                 */
                public Daily build(Validator validator, Class<?>... groups) {
                    return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
                }

                /**
                 * Build a new instance. It instance may not meet that constraint. Use only if the original value is
                 * completely reliable.
                 *
                 * @return new unsafe instance
                 * @since 3.0.0
                 */
                public Daily unsafeBuild() {
                    return new Builder.Bean(this);
                }

                /**
                 * Implements of the {@code Daily}.
                 *
                 * @author riru
                 * @version 3.0.0
                 * @since 3.0.0
                 */
                protected static class Bean implements Daily {

                    private OffsetDateTime anchorTime;
                    private Integer interval;

                    /**
                     * Constructor. Used only for deserialization from JSON.
                     *
                     * @since 3.0.0
                     */
                    protected Bean() {
                    }

                    /**
                     * Constructor.
                     *
                     * @param builder the {@code Builder}
                     * @since 3.0.0
                     */
                    protected Bean(Builder builder) {
                        this.anchorTime = builder.anchorTime;
                        this.interval = builder.interval;
                    }

                    @Override
                    public SchedefKing getKind() {
                        return SchedefKing.DAILY;
                    }

                    /**
                     * Get scheduling anchor time.
                     *
                     * @return scheduling anchor time
                     * @since 3.0.0
                     */
                    @Override
                    public OffsetDateTime getAnchorTime() {
                        return anchorTime;
                    }

                    /**
                     * Set scheduling anchor time.
                     *
                     * @param anchorTime scheduling anchor time
                     * @since 3.0.0
                     */
                    public void setAnchorTime(OffsetDateTime anchorTime) {
                        this.anchorTime = anchorTime;
                    }

                    /**
                     * Get scheduling interval.
                     *
                     * @return scheduling interval
                     * @since 3.0.0
                     */
                    @Override
                    public Integer getInterval() {
                        return interval;
                    }

                    /**
                     * Set scheduling interval.
                     *
                     * @param interval scheduling interval
                     * @since 3.0.0
                     */
                    public void setInterval(Integer interval) {
                        this.interval = interval;
                    }

                    /**
                     * Returns a string representation.
                     *
                     * @return a string representation
                     * @since 3.0.0
                     */
                    @Override
                    public String toString() {
                        return "Daily{" + "dateTime=" + anchorTime + ", interval=" + interval + '}';
                    }

                }
            }
        }

        interface Weekly extends Trigger {

            List<DayOfWeek> getWeekDays();

            OffsetDateTime getAnchorTime();

            Integer getInterval();
        }

        interface MonthlyDays extends Trigger {

            List<Month> getMonths();

            List<Integer> getDays();

            OffsetDateTime getAnchorTime();
        }

        interface MonthlyWeekDays extends Trigger {

            List<Month> getMonths();

            List<Integer> getWeeks();

            List<DayOfWeek> getWeekDays();

            OffsetDateTime getAnchorTime();
        }

        interface Once extends Trigger {

            OffsetDateTime getAnchorTime();
        }

        interface Cancel extends Trigger {

            OffsetDateTime getFrom();

            OffsetDateTime getTo();
        }

        /**
         * JSON deserializer for {@code FilteringOperation}.
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
                        new Daily.Builder().withDateTime(tmp.getAnchorTime()).withInterval(tmp.getInterval()).unsafeBuild();
                    default ->
                        throw new UnsupportedOperationException();
                };
            }

            protected class Bean implements Daily, Weekly, MonthlyDays, MonthlyWeekDays, Once, Cancel {

                private SchedefKing kind;
                private OffsetDateTime from;
                private OffsetDateTime to;
                private List<Month> months;
                private List<Integer> weeks;
                private List<DayOfWeek> weekDays;
                private List<Integer> days;
                private OffsetDateTime anchorTime;
                private Integer interval;

                @Override
                public SchedefKing getKind() {
                    return kind;
                }

                public void setKind(SchedefKing kind) {
                    this.kind = kind;
                }

                public OffsetDateTime getFrom() {
                    return from;
                }

                public void setFrom(OffsetDateTime from) {
                    this.from = from;
                }

                public OffsetDateTime getTo() {
                    return to;
                }

                public void setTo(OffsetDateTime to) {
                    this.to = to;
                }

                public List<Month> getMonths() {
                    return months;
                }

                public void setMonths(List<Month> months) {
                    this.months = months;
                }

                public List<Integer> getWeeks() {
                    return weeks;
                }

                public void setWeeks(List<Integer> weeks) {
                    this.weeks = weeks;
                }

                public List<DayOfWeek> getWeekDays() {
                    return weekDays;
                }

                public void setWeekDays(List<DayOfWeek> weekDays) {
                    this.weekDays = weekDays;
                }

                public List<Integer> getDays() {
                    return days;
                }

                public void setDays(List<Integer> days) {
                    this.days = days;
                }

                @Override
                public OffsetDateTime getAnchorTime() {
                    return anchorTime;
                }

                public void setAnchorTime(OffsetDateTime anchorTime) {
                    this.anchorTime = anchorTime;
                }

                @Override
                public Integer getInterval() {
                    return interval;
                }

                public void setInterval(Integer interval) {
                    this.interval = interval;
                }
            }
        }
    }

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
     * Get schedule configuration.
     *
     * @return schedule configuration
     * @since 3.0.0
     */
    @Schema(description = "Schedule configuration.", example
        = "{\"type\": \"DAILY\", \"interval\": 1, \"dateTime\": \"2700-10-10T07:09:42Z\"}")
    @NotNull(groups = {Default.class})
    Trigger getTrigger();
}
