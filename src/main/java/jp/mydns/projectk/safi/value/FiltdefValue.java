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
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.util.Map;
import jp.mydns.projectk.safi.util.ValidationUtils;

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
public interface FiltdefValue {

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
    FilteringCondition getCondition();

    /**
     * Builder of the {@code FiltdefValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Builder {

        private Map<String, String> trnsdef = Map.of();
        private FilteringCondition condition = FilteringCondition.empty();

        /**
         * Set transform definition for filtering.
         *
         * @param trnsdef transform definition for filtering
         * @return updated this
         * @since 3.0.0
         */
        public Builder withTrnsdef(Map<String, String> trnsdef) {
            this.trnsdef = trnsdef;
            return this;
        }

        /**
         * Set filtering condition.
         *
         * @param condition filtering condition
         * @return updated this
         * @since 3.0.0
         */
        public Builder withFilter(FilteringCondition condition) {
            this.condition = condition;
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
        public FiltdefValue build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
        }

        /**
         * Build a new instance. It instance may not meet that constraint. Use only if the original value is completely
         * reliable.
         *
         * @return new unsafe instance
         * @since 3.0.0
         */
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
            private FilteringCondition condition;

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
             * @param builder the {@code FiltdefValue.Builder}
             * @since 3.0.0
             */
            protected Bean(FiltdefValue.Builder builder) {
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
                this.trnsdef = trnsdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public FilteringCondition getCondition() {
                return condition;
            }

            /**
             * Set filtering condition.
             *
             * @param condition filtering condition
             * @since 3.0.0
             */
            public void setCondition(FilteringCondition condition) {
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
