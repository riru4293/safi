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
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import jp.mydns.projectk.safi.util.CollectionUtils;

/**
 * An information for filtering the content values. It has a transform definition and filtering condition, and is used
 * to determine whether the transform result matches the filtering condition. If transform definition is {@code null},
 * no transformation is performed and the input is passed to the filtering process as is.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/../schemas/filtdef.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(FiltdefValue.Deserializer.class)
@Schema(name = "Filtdef", description = "An information for filtering the content values.",
    example = "{\"condition\":{\"operation\":\"AND\",\"children\":[{\"operation\":\"EQUAL\",\"name\":\"kind\","
    + "\"value\":\"2\"},{\"operation\":\"PARTIAL_MATCH\",\"name\":\"name\",\"value\":\"taro\"}]},"
    + "\"trnsdef\":{\"name\":\"[userName]\",\"kind\":\"[userType]\",\"id\":\"[userId]\"}}")
public interface FiltdefValue extends ValueTemplate {

    /**
     * Get the transform definition for filtering.
     *
     * @return transform definition for filtering
     * @since 3.0.0
     */
    @NotNull(groups = Default.class)
    @Schema(ref = "#/components/schemas/Jobdef/properties/trnsdef")
    Map<String, String> getTrnsdef();

    /**
     * Get filtering condition.
     *
     * @return filtering condition
     * @since 3.0.0
     */
    @NotNull(groups = Default.class)
    @Valid
    FilteringConditionValue getCondition();

    /**
     * Builder of the {@code FiltdefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder extends AbstractBuilder<Builder, FiltdefValue> {

        private Map<String, String> trnsdef;
        private FilteringConditionValue condition;

        /**
         * Constructor.
         *
         * @since 3.0.0
         */
        public Builder() {
            super(Builder.class);
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 3.0.0
         */
        @Override
        public Builder with(FiltdefValue src) {
            super.with(Objects.requireNonNull(src));

            withTrnsdef(src.getTrnsdef());
            withCondition(src.getCondition());

            return this;
        }

        /**
         * Set transform definition for filtering.
         *
         * @param trnsdef transform definition for filtering
         * @return updated this
         * @since 3.0.0
         */
        public Builder withTrnsdef(Map<String, String> trnsdef) {
            this.trnsdef = CollectionUtils.toUnmodifiable(trnsdef);
            return this;
        }

        /**
         * Set filtering condition.
         *
         * @param condition filtering condition
         * @return updated this
         * @since 3.0.0
         */
        public Builder withCondition(FilteringConditionValue condition) {
            this.condition = condition;
            return this;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FiltdefValue unsafeBuild() {
            return new Bean(this);
        }

        /**
         * Implements of the {@code FiltdefValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean implements FiltdefValue {

            private Map<String, String> trnsdef;
            private FilteringConditionValue condition;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected Bean() {
            }

            private Bean(Builder builder) {
                this.trnsdef = builder.trnsdef;
                this.condition = builder.condition;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Map<String, String> getTrnsdef() {
                return trnsdef;
            }

            /**
             * Set transform definition for filtering.
             *
             * @param trnsdef transform definition for filtering
             * @since 3.0.0
             */
            public void setTrnsdef(Map<String, String> trnsdef) {
                this.trnsdef = CollectionUtils.toUnmodifiable(trnsdef);
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public FilteringConditionValue getCondition() {
                return condition;
            }

            /**
             * Set filtering condition.
             *
             * @param condition filtering condition
             * @since 3.0.0
             */
            public void setCondition(FilteringConditionValue condition) {
                this.condition = condition;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "FiltdefValue{" + "trnsdef=" + trnsdef + ", condition=" + condition + '}';
            }
        }
    }

    /**
     * JSON deserializer for {@code FiltdefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<FiltdefValue> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FiltdefValue deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return dc.deserialize(Builder.Bean.class, jp);
        }
    }
}
