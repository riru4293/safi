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
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.PeriodConfigKind;
import jp.mydns.projectk.safi.entity.PeriodConfigEntity;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * Configuration value whose value is period. It is persisted via {@link PeriodConfigEntity}.
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
@Schema(name = "PeriodConfig", description = "Configuration value whose value is period.")
@JsonbTypeDeserializer(PeriodConfigValue.Deserializer.class)
public interface PeriodConfigValue {

    /**
     * Get id of the configuration.
     *
     * @return the {@code PeriodConfigKind}
     * @since 1.0.0
     */
    @NotNull
    PeriodConfigKind getId();

    /**
     * Get value of the configuration.
     *
     * @return the {@code PeriodDuration}
     * @since 1.0.0
     */
    Optional<PeriodDuration> getValue();

    /**
     * Get the validity-period of the configuration.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    @NotNull
    @Valid
    ValidityPeriod getValidityPeriod();

    /**
     * Get the {@code PersistenceContext}.
     *
     * @return persistence information
     * @since 1.0.0
     */
    @Schema(description = "Persistence information.")
    Optional<@Valid PersistenceContext> getPersistenceContext();

    /**
     * Builder of the {@link PeriodConfigValue}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private PeriodConfigKind id;
        private PeriodDuration value;
        private ValidityPeriod validityPeriod;
        private PersistenceContext persistenceContext;

        /**
         * Constructs a new builder with all properties are {@code null}.
         *
         * @since 1.0.0
         */
        public Builder() {
        }

        /**
         * Constructs a new builder with set all properties by copying them from other value.
         *
         * @param src source value
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 1.0.0
         */
        public Builder(PeriodConfigValue src) {
            Objects.requireNonNull(src);

            this.id = src.getId();
            this.value = src.getValue().orElse(null);
            this.validityPeriod = src.getValidityPeriod();
            this.persistenceContext = src.getPersistenceContext().orElse(null);
        }

        /**
         * Set id of the configuration.
         *
         * @param id the {@code PeriodConfigKind}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withId(PeriodConfigKind id) {
            this.id = id;
            return this;
        }

        /**
         * Set value of the configuration.
         *
         * @param value the {@code PeriodDuration}. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withValue(PeriodDuration value) {
            this.value = value;
            return this;
        }

        /**
         * Set the validity-period of the configuration.
         *
         * @param validityPeriod the {@code ValidityPeriod}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withValidityPeriod(ValidityPeriod validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        /**
         * Set the {@code PersistenceContext}.
         *
         * @param persistenceContext the {@code PersistenceContext}. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public Builder withPersistenceContext(PersistenceContext persistenceContext) {
            this.persistenceContext = persistenceContext;
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
        public PeriodConfigValue build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new Bean(this), validator, groups);
        }

        /**
         * Implements of the {@code PeriodConfigValue} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements PeriodConfigValue {

            private PeriodConfigKind id;
            private PeriodDuration value;
            private ValidityPeriod validityPeriod;
            private PersistenceContext persistenceContext;

            /**
             * Constructor just for JSON deserialization.
             *
             * @since 1.0.0
             */
            protected Bean() {
            }

            /**
             * Construct with set all properties from builder.
             *
             * @param builder the {@code PeriodConfigValue.Builder}
             * @throws NullPointerException if {@code builder} is {@code null}
             * @since 1.0.0
             */
            protected Bean(PeriodConfigValue.Builder builder) {
                Objects.requireNonNull(builder);

                this.id = builder.id;
                this.value = builder.value;
                this.validityPeriod = builder.validityPeriod;
                this.persistenceContext = builder.persistenceContext;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public PeriodConfigKind getId() {
                return id;
            }

            /**
             * Set id of the configuration.
             *
             * @param id the {@code PeriodConfigKind}
             * @since 1.0.0
             */
            public void setId(PeriodConfigKind id) {
                this.id = id;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Optional<PeriodDuration> getValue() {
                return Optional.ofNullable(value);
            }

            /**
             * Set value of the configuration.
             *
             * @param value the {@code PeriodDuration}. It can be set {@code null}.
             * @since 1.0.0
             */
            public void setValue(PeriodDuration value) {
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
             * Set the validity-period of the configuration.
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
             * @param persistenceContext the {@code PersistenceContext}. It can be set {@code null}.
             * @since 1.0.0
             */
            public void setPersistenceContext(PersistenceContext persistenceContext) {
                this.persistenceContext = persistenceContext;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "PeriodConfig{" + "id=" + id + ", value=" + value + ", validityPeriod=" + validityPeriod + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code PeriodConfigValue}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<PeriodConfigValue> {

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
        public PeriodConfigValue deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
