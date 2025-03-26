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
import jakarta.validation.groups.Default;
import java.util.List;
import java.util.Objects;
import jp.mydns.projectk.safi.util.CollectionUtils;

/**
 * Node filtering condition.
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
@Schema(name = "FilteringCondition.Node", description = "Node filtering condition.")
interface NodeConditionValue extends FilteringConditionValue {

    /**
     * Get inner conditions.
     *
     * @return children inner conditions
     * @since 3.0.0
     */
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "Inner filtering conditions.")
    @NotNull(groups = Default.class)
    List<@NotNull(groups = Default.class) @Valid FilteringConditionValue> getChildren();

    /**
     * Builder of the {@code NodeConditionValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, NodeConditionValue> {

        private List<FilteringConditionValue> children;

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
        public Builder with(NodeConditionValue src) {
            super.with(Objects.requireNonNull(src));
            withChildren(src.getChildren());
            return this;
        }

        /**
         * Set inner conditions.
         *
         * @param children inner conditions
         * @return updated this
         * @since 3.0.0
         */
        public Builder withChildren(List<FilteringConditionValue> children) {
            this.children = CollectionUtils.toUnmodifiable(children);
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public NodeConditionValue unsafeBuild() {
            return new Builder.Bean(this);
        }

        protected static class Bean extends AbstractBuilder.AbstractBean implements NodeConditionValue {

            private List<FilteringConditionValue> children;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected Bean() {
            }

            private Bean(Builder builder) {
                super(builder);
                this.children = builder.children;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public List<FilteringConditionValue> getChildren() {
                return children;
            }

            /**
             * Set inner conditions.
             *
             * @param children inner conditions
             * @since 3.0.0
             */
            public void setChildren(List<FilteringConditionValue> children) {
                this.children = CollectionUtils.toUnmodifiable(children);
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return String.format("FilteringCondition.Node{operation=%s, children=%s}", operation, children);
            }
        }
    }
}
