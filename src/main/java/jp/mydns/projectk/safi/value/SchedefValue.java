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
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.ARRAY;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import static java.util.stream.Collectors.toUnmodifiableSet;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.SchedefKind;
import static jp.mydns.projectk.safi.constant.SchedefKind.*;
import jp.mydns.projectk.safi.entity.SchedefEntity;
import jp.mydns.projectk.safi.util.JsonUtils;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.value.SchedefValue.Builder.TemporalyBean;

/**
 * Job scheduling definition.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(SchedefValue.Deserializer.class)
@Schema(name = "Schedef", description = "Job scheduling definition.")
public interface SchedefValue {

    /**
     * Get scheduling definition id.
     *
     * @return schedule creation definition id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Scheduling definition id.")
    String getId();

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
     * Get the {@code SchedefKind}.
     *
     * @return the {@code SchedefKind}
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Scheduling definition kind.")
    SchedefKind getKind();

    /**
     * Get priority for between the scheduling definitions.
     *
     * @return priority. The size of the character code is the size of the priority. If the priorities are the same,
     * {@link #getId()} is used as the second key.
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 1)
    @Schema(description = "Priority for between the scheduling definitions.")
    String getPriority();

    /**
     * Get scheduling definition name.
     *
     * @return scheduling definition name
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 255)
    @Schema(description = "Scheduling definition name.")
    String getName();

    /**
     * Get scheduling definition value.
     *
     * @return scheduling definition value
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Scheduling definition value.")
    JsonObject getValue();

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    @Valid
    @NotNull
    @Schema(description = "Validity period.")
    ValidityPeriod getValidityPeriod();

    /**
     * Get the {@code PersistenceContext}.
     *
     * @return the {@code PersistenceContext}
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
     * Generate schedules within a specified period.
     *
     * @param from begin of the generate period
     * @param to end of the generate period
     * @return generated schedule times
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    Stream<Entry<LocalDateTime, SchedefValue>> generateSchedules(LocalDate from, LocalDate to);

    /**
     * Builder of the {@link SchedefValue}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private String id;
        private String jobdefId;
        private SchedefKind kind;
        private String priority;
        private String name;
        private JsonObject value;
        private ValidityPeriod validityPeriod;
        private PersistenceContext persistenceContext;
        private String note;

        /**
         * Constructs a new builder with set all properties by copying them from entity.
         *
         * @param entity the {@code SchedefEntity}
         * @throws NullPointerException if {@code entity} is {@code null}
         * @throws IllegalArgumentException If the {@link SchedefKind} of the {@code entity} is {@code null}.
         * @since 1.0.0
         */
        public Builder(SchedefEntity entity) {
            Objects.requireNonNull(entity);

            if (Objects.requireNonNull(entity).getKind() == null) {
                throw new IllegalArgumentException("Unexpected schedule creation kind.");
            }

            this.id = entity.getId();
            this.jobdefId = entity.getJobdefId();
            this.kind = entity.getKind();
            this.priority = entity.getPriority();
            this.name = entity.getName();
            this.value = entity.getValue();
            this.validityPeriod = entity.getValidityPeriod();
            this.persistenceContext = entity.toPersistenceContext();
            this.note = entity.getNote();
        }

        /**
         * Constructs a new builder with set all properties by copying them from other value.
         *
         * @param src source value
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 1.0.0
         */
        private Builder(SchedefValue src) {
            Objects.requireNonNull(src);

            this.id = src.getId();
            this.jobdefId = src.getJobdefId();
            this.kind = src.getKind();
            this.priority = src.getPriority();
            this.name = src.getName();
            this.value = src.getValue();
            this.validityPeriod = src.getValidityPeriod();
            src.getPersistenceContext().ifPresent(v -> this.persistenceContext = v);
            this.note = src.getNote();
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
        public SchedefValue build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
        }

        private SchedefValue unsafeBuild() {
            return switch (kind) {
                case STANDARD ->
                    new Standard(this);
                case BAN ->
                    new Ban(this);
            };
        }

        /**
         * Abstract implements of the {@code SchedefValue} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static abstract class AbstractBean implements SchedefValue {

            private String id;
            private String jobdefId;
            private SchedefKind kind;
            private String priority;
            private String name;
            private JsonObject value;
            private ValidityPeriod validityPeriod;
            private PersistenceContext persistenceContext;
            private String note;

            /**
             * Constructor just for JSON deserialization.
             *
             * @since 1.0.0
             */
            protected AbstractBean() {
            }

            /**
             * Construct with set all properties from builder.
             *
             * @param builder the {@code Jobdef.Builder}
             * @throws NullPointerException if {@code builder} is {@code null}
             * @since 1.0.0
             */
            protected AbstractBean(Builder builder) {
                Objects.requireNonNull(builder);

                this.id = builder.id;
                this.jobdefId = builder.jobdefId;
                this.kind = builder.kind;
                this.priority = builder.priority;
                this.name = builder.name;
                this.value = builder.value;
                this.validityPeriod = builder.validityPeriod;
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
             * Set scheduling definition id.
             *
             * @param id scheduling definition id
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
            public SchedefKind getKind() {
                return kind;
            }

            /**
             * Set the {@code SchedefKind}.
             *
             * @param kind the {@code SchedefKind}
             * @since 1.0.0
             */
            public void setKind(SchedefKind kind) {
                this.kind = kind;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getPriority() {
                return priority;
            }

            /**
             * Set priority for between the scheduling definitions.
             *
             * @param priority priority
             * @since 1.0.0
             */
            public void setPriority(String priority) {
                this.priority = priority;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getName() {
                return name;
            }

            /**
             * Set scheduling definition name.
             *
             * @param name definition name
             * @since 1.0.0
             */
            public void setName(String name) {
                this.name = name;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public JsonObject getValue() {
                return value;
            }

            /**
             * Set scheduling definition value.
             *
             * @param value scheduling definition value
             * @since 1.0.0
             */
            public void setValue(JsonObject value) {
                this.value = value;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public ValidityPeriod getValidityPeriod() {
                return validityPeriod;
            }

            /**
             * Set the {@code ValidityPeriod}.
             *
             * @param validityPeriod the {@code ValidityPeriod}
             * @since 1.0.0
             */
            public void setValidityPeriod(ValidityPeriod validityPeriod) {
                this.validityPeriod = validityPeriod;
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
             * @param persistenceContext the {@code PersistenceContext}
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
             * @param note note
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
                return "SchedefValue{" + "id=" + id + ", kind=" + kind + ", priority=" + priority
                        + ", jobdefId=" + jobdefId + ", name=" + name + ", value=" + value + '}';
            }
        }

        /**
         * Implements of the {@code SchedefValue} as Java Beans. Only used during JSON deserialization.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class TemporalyBean extends AbstractBean {

            /**
             * Constructor just for JSON deserialization.
             *
             * @since 1.0.0
             */
            protected TemporalyBean() {
            }

            /**
             * Unsupported.
             *
             * @param from no use
             * @param to no use
             * @throws UnsupportedOperationException always
             * @return never return
             * @since 1.0.0
             */
            @Override
            public Stream<Entry<LocalDateTime, SchedefValue>> generateSchedules(LocalDate from, LocalDate to) {
                throw new UnsupportedOperationException();
            }
        }

        private class Standard extends AbstractBean {

            @NotNull
            private final LocalTime scheduleTime;

            @NotNull
            private final Set<Month> months;

            @NotNull
            private final Set<DayOfWeek> dayOfWeeks;

            @NotNull
            private final Set<Integer> days;

            private Standard(Builder builder) {
                super(builder);
                this.scheduleTime = extractLocalTime("scheduleTime");
                this.months = extractEnums("months", Month::valueOf);
                this.dayOfWeeks = extractEnums("dayOfWeeks", DayOfWeek::valueOf);
                this.days = extractIntegers("days");
            }

            private LocalTime extractLocalTime(String key) {
                return Optional.ofNullable(getValue()).map(o -> o.get(key)).map(JsonUtils::toString)
                        .flatMap(TimeUtils::tryToLocalTime).orElse(null);
            }

            private <T> Set<T> extractEnums(String key, Function<String, T> resolver) {
                return Optional.ofNullable(getValue()).map(o -> o.get(key))
                        .filter(JsonUtils.typeEquals(ARRAY)).map(JsonValue::asJsonArray)
                        .map(a -> a.stream().map(JsonUtils::toString).map(n -> tryToEnum(n, resolver))
                                .flatMap(Optional::stream).collect(toUnmodifiableSet()))
                        .orElse(null);
            }

            private <T> Optional<T> tryToEnum(String name, Function<String, T> resolver) {
                try {
                    return Optional.of(name).map(resolver);
                } catch (RuntimeException ignore) {
                    return Optional.empty();
                }
            }

            private Set<Integer> extractIntegers(String key) {
                return Optional.ofNullable(getValue()).map(o -> o.get(key))
                        .filter(JsonUtils.typeEquals(ARRAY)).map(JsonValue::asJsonArray)
                        .map(a -> a.stream().map(JsonUtils::tryToInteger)
                                .flatMap(Optional::stream).collect(toUnmodifiableSet()))
                        .orElse(null);
            }

            /**
             * {@inheritDoc}
             *
             * @throws NullPointerException if any argument is {@code null}
             * @since 1.0.0
             */
            @Override
            public Stream<Entry<LocalDateTime, SchedefValue>> generateSchedules(LocalDate from, LocalDate to) {
                LocalDateTime refTime = LocalDateTime.of(from, this.scheduleTime);

                return LongStream.rangeClosed(0, DAYS.between(from, to)).mapToObj(refTime::plusDays)
                        .filter(p(this.months::contains, LocalDateTime::getMonth).or(x -> this.months.isEmpty()))
                        .filter(p(this.dayOfWeeks::contains, LocalDateTime::getDayOfWeek).or(x -> this.dayOfWeeks.isEmpty()))
                        .filter(p(this.days::contains, LocalDateTime::getDayOfMonth).or(x -> this.days.isEmpty()))
                        .map(k -> Map.entry(k, this));
            }
        }

        private class Ban extends AbstractBean {

            @NotNull
            private final LocalDateTime begin;

            @NotNull
            private final LocalDateTime end;

            private Ban(Builder builder) {
                super(builder);
                this.begin = extractLocalDateTime("begin");
                this.end = extractLocalDateTime("end");
            }

            private LocalDateTime extractLocalDateTime(String key) {
                return Optional.ofNullable(getValue()).map(o -> o.get(key)).map(JsonUtils::toString)
                        .flatMap(TimeUtils::tryToLocalDateTime).orElse(null);
            }

            /**
             * Generate period of the scheduling ban. It always returns two elements, the first being the start of the
             * ban period and the second being the end of the ban period.
             *
             * @param from no use
             * @param to no use
             * @return ban period. This stream is sequential.
             * @since 1.0.0
             */
            @Override
            public Stream<Entry<LocalDateTime, SchedefValue>> generateSchedules(LocalDate from, LocalDate to) {
                return Stream.of(Map.entry(this.begin, SchedefValue.class.cast(this)),
                        Map.entry(this.end, SchedefValue.class.cast(this))
                ).sequential();
            }
        }
    }

    /**
     * JSON deserializer for {@code SchedefValue}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<SchedefValue> {

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
        public SchedefValue deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            TemporalyBean tmp = ctx.deserialize(TemporalyBean.class, parser);

            if (tmp == null) {
                return null;
            }

            if (tmp.getKind() == null) {
                return tmp; // Note: To be errored when validation or using.
            }

            return new Builder(tmp).unsafeBuild();
        }
    }
}
