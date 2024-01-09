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
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.function.Predicate;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.validator.Strict;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Valid period.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <p>
 * JSON Format<pre><code>
 * {
 *     "from": "2000-01-01T00:00:00Z",
 *     "to": "2999-12-31T23:59:59Z",
 *     "ban": false
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(ValidityPeriod.Deserializer.class)
@Schema(name = "ValidityPeriod", description = "Validity period.")
public interface ValidityPeriod {

    /**
     * Get begin date time of valid period.
     *
     * @return begin date time of valid period
     * @since 1.0.0
     */
    @Schema(example = "2000-01-01T00:00:00Z", description = "Begin date time of valid period.")
    @NotNull(groups = {Strict.class})
    @TimeRange(groups = {Strict.class})
    @TimeAccuracy(groups = {Strict.class})
    OffsetDateTime getFrom();

    /**
     * Get end date time of valid period.
     *
     * @return end date time of valid period
     * @since 1.0.0
     */
    @Schema(example = "2999-12-31T23:59:59Z", description = "End date time of valid period.")
    @NotNull(groups = {Strict.class})
    @TimeRange(groups = {Strict.class})
    @TimeAccuracy(groups = {Strict.class})
    OffsetDateTime getTo();

    /**
     * Returns {@code true} if forbidden to be valid.
     *
     * @return {@code true} if forbidden to be valid, otherwise {@code false}.
     * @since 1.0.0
     */
    @Schema(example = "false", description = "true if forbidden to be valid, otherwise false.")
    boolean isBan();

    /**
     * Returns {@code true} if {@code refTime} is within the valid period and not forbidden to be valid.
     *
     * @param refTime reference date time. It timezone is UTC.
     * @return {@code true} if {@code refTime} is within the valid period and not forbidden to be valid, otherwise
     * {@code false}.
     * @throws NullPointerException if {@code refTime} is {@code null}
     * @since 1.0.0
     */
    @JsonbTransient
    default boolean isEnabled(LocalDateTime refTime) {

        Objects.requireNonNull(refTime);

        OffsetDateTime r = OffsetDateTime.of(refTime, ZoneOffset.UTC);

        return !isBan() && !r.isBefore(getFrom()) && !r.isAfter(getTo());

    }

    /**
     * Returns a {@code Predicate} that test whether {@code ValidityPeriod} contains {@code refTime}.
     *
     * @param refTime reference date time. It timezone is UTC.
     * @return a {@code Predicate} that test whether {@code ValidityPeriod} contains {@code refTime}
     * @throws NullPointerException if {@code refTime} is {@code null}
     * @since 1.0.0
     */
    static Predicate<ValidityPeriod> containsWith(LocalDateTime refTime) {

        Objects.requireNonNull(refTime);

        return v -> v.isEnabled(refTime);

    }

    /**
     * Get default value of begin date time of valid period.
     *
     * @return {@code 2000-01-01T00:00:00Z}
     * @since 1.0.0
     */
    static OffsetDateTime defaultFrom() {
        return OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    /**
     * Get default value of end date time of valid period.
     *
     * @return {@code 2999-12-31T23:59:59Z}
     * @since 1.0.0
     */
    static OffsetDateTime defaultTo() {
        return OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);
    }

    /**
     * Get default value of flag that forbidden to be valid.
     *
     * @return {@code false}
     * @since 1.0.0
     */
    static boolean defaultBan() {
        return false;
    }

    /**
     * Builder of the {@link ValidityPeriod}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private OffsetDateTime from = defaultFrom();
        private OffsetDateTime to = defaultTo();
        private boolean ban = false;

        /**
         * Set all properties from {@code src}.
         *
         * @param src source value
         * @return updated this
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 1.0.0
         */
        public Builder with(ValidityPeriod src) {

            Objects.requireNonNull(src);

            this.from = src.getFrom();
            this.to = src.getTo();
            this.ban = src.isBan();

            return this;

        }

        /**
         * Set begin date time of valid period.
         *
         * @param from begin date time of valid period
         * @return updated this
         * @since 1.0.0
         */
        public Builder withFrom(OffsetDateTime from) {
            this.from = from;
            return this;
        }

        /**
         * Set end date time of valid period.
         *
         * @param to end date time of valid period
         * @return updated this
         * @since 1.0.0
         */
        public Builder withTo(OffsetDateTime to) {
            this.to = to;
            return this;
        }

        /**
         * Sets a flag that indicates ban of valid.
         *
         * @param ban {@code true} if forbidden to be valid, otherwise {@code false}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withBan(boolean ban) {
            this.ban = ban;
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
         * @since 1.0.0
         */
        public ValidityPeriod build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        protected static class Bean implements ValidityPeriod {

            private OffsetDateTime from;
            private OffsetDateTime to;
            private boolean ban;

            protected Bean() {
            }

            protected Bean(Builder builder) {
                this.from = builder.from;
                this.to = builder.to;
                this.ban = builder.ban;
            }

            @Override
            public OffsetDateTime getFrom() {
                return from;
            }

            public void setFrom(OffsetDateTime from) {
                this.from = from;
            }

            @Override
            public OffsetDateTime getTo() {
                return to;
            }

            public void setTo(OffsetDateTime to) {
                this.to = to;
            }

            @Override
            public boolean isBan() {
                return ban;
            }

            public void setBan(boolean ban) {
                this.ban = ban;
            }

            @Override
            public int hashCode() {
                return Objects.hash(from, to, ban);
            }

            @Override
            public boolean equals(Object other) {
                return this == other || other instanceof ValidityPeriod o && Objects.equals(from, o.getFrom())
                        && Objects.equals(to, o.getTo()) && Objects.equals(ban, o.isBan());
            }

            @Override
            public String toString() {
                return "ValidityPeriod{" + "from=" + from + ", to=" + to + ", ban=" + ban + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code ValidityPeriod}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<ValidityPeriod> {

        @Override
        public ValidityPeriod deserialize(JsonParser jp, DeserializationContext dc, Type type) {

            var bean = dc.deserialize(Builder.Bean.class, jp);

            if (bean.getFrom() == null) {
                bean.setFrom(ValidityPeriod.defaultFrom());
            }

            if (bean.getTo() == null) {
                bean.setTo(ValidityPeriod.defaultTo());
            }

            return bean;
        }
    }
}
