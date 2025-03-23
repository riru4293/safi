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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.util.Optional;

/**
 * Common named value. This class has name and validity period.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface NamedValue extends PersistableValue {

    /**
     * Get the {@code ValidityPeriodValue}.
     *
     * @return the {@code ValidityPeriodValue}
     * @since 3.0.0
     */
    @NotNull(groups = {Default.class})
    @Valid
    ValidityPeriodValue getValidityPeriod();

    /**
     * Get name of value.
     *
     * @return name
     * @since 3.0.0
     */
    @Schema(description = "Name of value.", maxLength = 250)
    @NotNull(groups = Default.class)
    Optional<@Size(max = 250, groups = {Default.class}) String> getName();

    /**
     * Abstract builder of the {@code NamedValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends NamedValue>
        extends PersistableValue.AbstractBuilder<B, V> {

        protected ValidityPeriodValue validityPeriod;
        protected String name;

        protected AbstractBuilder(Class<B> builderType) {
            super(builderType);
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public B with(V src) {
            super.with(src);

            this.validityPeriod = src.getValidityPeriod();
            this.name = src.getName().orElse(null);

            return builderType.cast(this);
        }

        /**
         * Set the {@code ValidityPeriodValue}.
         *
         * @param validityPeriod the {@code ValidityPeriodValue}
         * @return updated this
         * @since 3.0.0
         */
        public B withValidityPeriod(ValidityPeriodValue validityPeriod) {
            this.validityPeriod = validityPeriod;
            return builderType.cast(this);
        }

        /**
         * Set name of value.
         *
         * @param name name of value
         * @return updated this
         * @since 3.0.0
         */
        public B withName(String name) {
            this.name = name;
            return builderType.cast(this);
        }

        protected abstract static class AbstractBean extends PersistableValue.AbstractBuilder.AbstractBean
            implements NamedValue {

            protected ValidityPeriodValue validityPeriod;
            protected String name;

            protected AbstractBean() {
            }

            protected AbstractBean(NamedValue.AbstractBuilder<?, ?> builder) {
                super(builder);
                this.validityPeriod = builder.validityPeriod;
                this.name = builder.name;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public ValidityPeriodValue getValidityPeriod() {
                return validityPeriod;
            }

            /**
             * Set the {@code ValidityPeriodValue}.
             *
             * @param validityPeriod the {@code ValidityPeriodValue}
             * @since 3.0.0
             */
            public void setValidityPeriod(ValidityPeriodValue validityPeriod) {
                this.validityPeriod = validityPeriod;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<String> getName() {
                return Optional.ofNullable(name);
            }

            /**
             * Set name of value.
             *
             * @param name name of value
             * @since 3.0.0
             */
            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
