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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.util.Objects;

/**
 * Leaf filtering condition.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/../schemas/filtering-condition.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Schema(name = "FilteringCondition.Leaf", description = "Leaf filtering condition.",
    example = "{\"operation\": \"PARTIAL_MATCH\", \"name\": \"userName\", \"value\": \"ike\"}")
public interface LeafConditionValue extends FilteringConditionValue {

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    @Schema(implementation = FilteringOperationValue.LeafOperation.class)
    FilteringOperationValue getOperation();

    /**
     * Get property name to filter on.
     *
     * @return property name to filter on
     * @since 3.0.0
     */
    @Schema(description = "Property name to filter on.")
    @NotNull(groups = Default.class)
    String getName();

    /**
     * Get value to filter on.
     *
     * @return value to filter on
     * @since 3.0.0
     */
    @Schema(description = "Value to filter on.")
    @NotNull(groups = Default.class)
    String getValue();

    /**
     * Builder of the {@code LeafConditionValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, LeafConditionValue> {

        private String name;
        private String value;

        /**
         * Constructor.
         *
         * @param operation the {@code FilteringOperationValue}
         * @throws NullPointerException if {@code operation} is {@code null}
         * @since 3.0.0
         */
        public Builder(FilteringOperationValue operation) {
            super(Builder.class, Objects.requireNonNull(operation));
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
        public Builder with(LeafConditionValue src) {
            super.with(Objects.requireNonNull(src));

            withName(src.getName());
            withValue(src.getValue());

            return this;
        }

        /**
         * Set property name to filter on.
         *
         * @param name property name to filter on
         * @return updated this
         * @since 3.0.0
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set value to filter on.
         *
         * @param value value to filter on
         * @return updated this
         * @since 3.0.0
         */
        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public LeafConditionValue unsafeBuild() {
            return new Builder.Bean(this);
        }

        protected static class Bean extends AbstractBuilder.AbstractBean implements LeafConditionValue {

            private final String name;
            private final String value;

            private Bean(Builder builder) {
                super(builder);
                this.name = builder.name;
                this.value = builder.value;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getName() {
                return name;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getValue() {
                return value;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return String.format("FilteringCondition.Leaf{operation=%s, name=%s, value=%s}", operation, name, value);
            }
        }
    }
}
