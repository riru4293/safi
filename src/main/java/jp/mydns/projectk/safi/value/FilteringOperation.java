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
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.Set;
import static java.util.stream.Collectors.toUnmodifiableSet;
import java.util.stream.Stream;

/**
 * Filtering operation. Interface for {@link Single} or {@link Multi}.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(FilteringOperation.Deserializer.class)
@Schema(name = "FilteringOperation", description = "Filtering operation.")
public interface FilteringOperation {

    /**
     * Get filtering operation name.
     *
     * @return filtering operation name
     * @since 3.0.0
     */
    @Schema(description = "Filtering operation name.")
    @NotNull
    String name();

    /**
     * Get this as {@code FilteringOperation.Single}.
     *
     * @return this
     * @throws ClassCastException if not assignable to {@code FilteringOperation.Single}
     * @since 3.0.0
     */
    default Single asSingle() {
        return Single.class.cast(this);
    }

    /**
     * Get this as {@code FilteringOperation.Multi}.
     *
     * @return this
     * @throws ClassCastException if not assignable to {@code FilteringOperation.Multi}
     * @since 3.0.0
     */
    default Multi asMulti() {
        return Multi.class.cast(this);
    }

    /**
     * Represents a match operation for a single value. For example, the {@code EQUAL} operator.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    enum Single implements FilteringOperation {
        /**
         * Indicates a equal.
         *
         * @since 3.0.0
         */
        EQUAL,
        /**
         * Indicates a forward-match.
         *
         * @since 3.0.0
         */
        FORWARD_MATCH,
        /**
         * Indicates a partial-match.
         *
         * @since 3.0.0
         */
        PARTIAL_MATCH,
        /**
         * Indicates a backward-match.
         *
         * @since 3.0.0
         */
        BACKWARD_MATCH,
        /**
         * Indicates a greater-than.
         *
         * @since 3.0.0
         */
        GRATER_THAN,
        /**
         * Indicates a less-than.
         *
         * @since 3.0.0
         */
        LESS_THAN,
        /**
         * Indicates a is-null.
         *
         * @since 3.0.0
         */
        IS_NULL,
    }

    /**
     * Filtering operation that aggregate filtering operations. For example, the {@code AND} operator.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    enum Multi implements FilteringOperation {
        /**
         * Indicates a logical product.
         *
         * @since 3.0.0
         */
        AND,
        /**
         * Indicates a logical sum.
         *
         * @since 3.0.0
         */
        OR,
        /**
         * Indicates the negation of a logical sum.
         *
         * @since 3.0.0
         */
        NOT_OR,
    }

    /**
     * JSON deserializer for {@code FilteringOperation}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<FilteringOperation> {

        private static final Set<String> multiOpNames
                = Stream.of(Multi.values()).map(Enum::name).collect(toUnmodifiableSet());

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FilteringOperation deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            String opName = ctx.deserialize(String.class, parser);

            if (opName == null) {
                return null;
            }

            return multiOpNames.contains(opName) ? Multi.valueOf(opName) : Single.valueOf(opName);
        }
    }
}
