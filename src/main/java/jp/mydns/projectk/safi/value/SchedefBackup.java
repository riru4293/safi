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
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.SchedefKind;
import jp.mydns.projectk.safi.entity.SchedefEntity;
import jp.mydns.projectk.safi.service.ValidationService;
import jp.mydns.projectk.safi.util.JsonUtils;

/**
 * Schedule creation definition.
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
@JsonbTypeDeserializer(SchedefBackup.Deserializer.class)
@Schema(name = "Schedef", description = "Definition for creates a schedule.")
public interface SchedefBackup {

    /**
     * Get schedule creation definition id.
     *
     * @return schedule creation definition id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Schema(description = "Schedule creation definiton id.")
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
    @Schema(description = "Kind of schedule creation definition.")
    SchedefKind getKind();

    /**
     * Get priority for between the schedule creation definitions.
     *
     * @return priority
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 1)
    @Schema(description = "Job definiton id.")
    String getPriority();

    /**
     * Get schedule creation definition name.
     *
     * @return definition name
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 255)
    @Schema(description = "Schedule creation definition name.")
    String getName();

    /**
     * Get schedule creation definition value.
     *
     * @return definition value
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Schedule creation definition value.")
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
     * @param from begin of the generate period. It is inclusive.
     * @param to end of the generate period. It is exclusive.
     * @return generated schedule times
     * @since 1.0.0
     */
    Stream<Entry<LocalDateTime, SchedefBackup>> generateSchedules(LocalDateTime from, LocalDateTime to);

    /**
     * Cast type to specified.
     *
     * @param <T> type of inherit the {@code Schedef}
     * @param clazz type of inherit the {@code Schedef}
     * @return value as is with specified type
     * @throws NullPointerException if {@code clazz} is {@code null}
     * @throws UnsupportedOperationException if {@code clazz} is not supported
     * @throws ClassCastException if the type of the wrapped one does not match the specified type
     * @since 1.0.0
     */
    default <T extends SchedefBackup> T unwrap(Class<T> clazz) {
        return Objects.requireNonNull(clazz).cast(this);
    }

    /**
     * Construct a {@code Schedef} from {@code SchedefEntity}.
     *
     * @param entity the {@code SchedefEntity}
     * @return the {@code Schedef}
     * @since 1.0.0
     */
    static SchedefBackup of(SchedefEntity entity, Supplier<ValidationService> validSvcSupplier) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(validSvcSupplier);

        ValidationService validSvc = Objects.requireNonNull(validSvcSupplier.get());

        return validSvc.requireValid(SchedefBackup.class.cast(switch (entity.getKind()) {
            case DAILY ->
                new Daily(entity);
            default ->
                null;
        }));
    }

    /**
     * JSON deserializer for {@code Schedef}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<SchedefBackup> {

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
        public SchedefBackup deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

            Bean tmp = ctx.deserialize(Bean.class, parser);

            if (tmp == null) {
                return null;
            }

            if (tmp.isEmpty()) {
                return emptyCondition();
            }

            return tmp.isMulti()
                    ? new MultiBean(tmp.getOperation(), tmp.getChildren())
                    : new SingleBean(tmp.getOperation(), tmp.getName(), tmp.getValue());
        }

        /**
         * Implements of the {@code Schedef} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements SchedefBackup {

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
            protected Bean() {
            }

            /**
             * Construct with set all properties by copying them from other value.
             *
             * @param src source value
             * @throws NullPointerException if {@code src} is {@code null}
             * @since 1.0.0
             */
            protected Bean(Bean src) {
                Objects.requireNonNull(src);

                this.id = src.id;
                this.jobdefId = src.jobdefId;
                this.kind = src.kind;
                this.priority = src.priority;
                this.name = src.name;
                this.value = src.value;
                this.validityPeriod = src.validityPeriod;
                this.persistenceContext = src.persistenceContext;
                this.note = src.note;
            }

            /**
             * Construct from the {@code SchedefEntity}.
             *
             * @param entity the {@code SchedefEntity}
             * @throws NullPointerException if {@code entity} is {@code null}
             */
            protected Bean(SchedefEntity entity) {
                Objects.requireNonNull(entity);

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
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getId() {
                return id;
            }

            /**
             * Set schedule creation definition id.
             *
             * @param id schedule creation definition id
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
             * Set priority for between the schedule creation definitions.
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
             * Set schedule creation definition name.
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
             * Set schedule creation definition value.
             *
             * @param name definition value
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
             * @param validityPeriod the {@code PersistenceContext}
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
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Stream<Entry<LocalDateTime, SchedefBackup>> generateSchedules(LocalDateTime from, LocalDateTime to) {
                throw new UnsupportedOperationException();
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "Schedef{" + "id=" + id + ", jobdefId=" + jobdefId + ", kind=" + kind + ", priority=" + priority
                        + ", name=" + name + ", value=" + value + '}';
            }
        }

        private class Daily extends Bean {

            @Min(1)
            @JsonbTransient
            private final int interval;

            @NotNull
            @JsonbTransient
            private final LocalDateTime datetime;

            private Daily(Bean bean) {
                super(bean);

                Optional<JsonObject> optVal = Optional.ofNullable(bean.getValue());
                interval = optVal.flatMap(o -> JsonUtils.tryExtractInteger(o, "interval")).orElse(0);
                datetime = optVal.flatMap(o -> JsonUtils.tryExtractLocalDateTime(o, "datetime")).orElse(null);
            }

            private Daily(SchedefEntity entity) {
                super(entity);

                Optional<JsonObject> optVal = Optional.ofNullable(entity.getValue());
                interval = optVal.flatMap(o -> JsonUtils.tryExtractInteger(o, "interval")).orElse(0);
                datetime = optVal.flatMap(o -> JsonUtils.tryExtractLocalDateTime(o, "datetime")).orElse(null);
            }
        }

        private class Weekly extends Bean {

            private final int interval;
            private final List<DayOfWeek> weekdays;
            private final LocalDateTime datetime;

            private Weekly(JsonObject value) {

            }
        }

        private class MonthlyDays extends Bean {

            private final int interval;
            private final List<Month> months;
            private final LocalDateTime datetime;

            private MonthlyDays(JsonObject value) {

            }
        }

        private class MonthlyWeekdays extends Bean {

            private final int interval;
            private final List<Month> months;
            private final List<DayOfWeek> weekdays;
            private final LocalDateTime datetime;

            private MonthlyWeekdays(JsonObject value) {

            }
        }

        private class Ban extends Bean {

            private final LocalDateTime from;
            private final LocalDateTime to;

            private Ban(JsonObject value) {

            }
        }
    }

    /**
     * Schedule creation definition type {@link SchedefKind#DAILY}.
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
    class Daily implements SchedefBackup {

        private final String id;
        private final SchedefKind kind;
        private final String priority;
        private final String jobdefId;
        @JsonbTransient
        private final JsonObject value;
        @NotNull
        private final Integer interval;
        @NotNull
        private final OffsetDateTime datetime;
        private final ValidityPeriod validityPeriod;
        private final PersistenceContext persistenceContext;
        private final String note;

        private Daily(SchedefEntity entity) {
            this.id = entity.getId();
            this.kind = entity.getKind();
            this.priority = entity.getPriority();
            this.jobdefId = entity.getJobdefId();
            this.value = entity.getValue();
            this.interval = JsonUtils.tryExtractInteger(entity.getValue(), "interval").orElse(null);
            this.datetime = JsonUtils.tryExtractOffsetTimeViaLocalDateTime(entity.getValue(), "datetime").orElse(null);
            this.validityPeriod = entity.getValidityPeriod();
            this.persistenceContext = entity.toPersistenceContext();
            this.note = entity.getNote();
        }
    }
}
