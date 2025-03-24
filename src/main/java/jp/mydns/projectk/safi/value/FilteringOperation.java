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
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.FilteringOperationKing;
import jp.mydns.projectk.safi.value.FilteringOperation.LeafOperation;
import jp.mydns.projectk.safi.value.FilteringOperation.NodeOperation;

/**
 * Filtering operation.
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
@Schema(name = "FilteringOperation", description = "Filtering operation.",
        oneOf = {LeafOperation.class, NodeOperation.class})
public interface FilteringOperation {

    /**
     * Returns the filtering operation with the specified name.
     *
     * @param name filtering operation name
     * @return the {@code FilteringOperation}
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws IllegalArgumentException if {@code name} is malformed as filtering operation name
     * @since 3.0.0
     */
    static FilteringOperation valueOf(String name) {
        Objects.requireNonNull(name);

        return Stream.of(NodeOperation.values()).map(Enum::name).anyMatch(name::equals)
            ? NodeOperation.valueOf(name)
            : LeafOperation.valueOf(name);
    }

    /**
     * Get kind of the filtering operation.
     *
     * @return kind of the filtering operation
     * @since 3.0.0
     */
    @JsonbTransient
    @Schema(hidden = true)
    @NotNull(groups = Default.class)
    FilteringOperationKing getKind();

    /**
     * Get filtering operation name.
     *
     * @return filtering operation name
     * @since 3.0.0
     */
    @Schema(description = "Filtering operation name.")
    @NotNull(groups = Default.class)
    String name();

    /**
     * Leaf filtering operation. For example, the {@code EQUAL} operator.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Schema(name = "FilteringOperation.Leaf", description = "Leaf filtering operation.")
    enum LeafOperation implements FilteringOperation {
        /**
         * Indicates a equal.
         *
         * @since 3.0.0
         */
        EQUAL(FilteringOperationKing.LEAF),
        /**
         * Indicates a forward-match.
         *
         * @since 3.0.0
         */
        FORWARD_MATCH(FilteringOperationKing.LEAF),
        /**
         * Indicates a partial-match.
         *
         * @since 3.0.0
         */
        PARTIAL_MATCH(FilteringOperationKing.LEAF),
        /**
         * Indicates a backward-match.
         *
         * @since 3.0.0
         */
        BACKWARD_MATCH(FilteringOperationKing.LEAF),
        /**
         * Indicates a greater-than.
         *
         * @since 3.0.0
         */
        GRATER_THAN(FilteringOperationKing.LEAF),
        /**
         * Indicates a less-than.
         *
         * @since 3.0.0
         */
        LESS_THAN(FilteringOperationKing.LEAF),
        /**
         * Indicates a is-null.
         *
         * @since 3.0.0
         */
        IS_NULL(FilteringOperationKing.LEAF);

        private final FilteringOperationKing kind;

        private LeafOperation(FilteringOperationKing kind) {
            this.kind = Objects.requireNonNull(kind);
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FilteringOperationKing getKind() {
            return kind;
        }
    }

    /**
     * Node filtering operation. For example, the {@code AND} operator.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Schema(name = "FilteringOperation.Node", description = "Node filtering operation.")
    enum NodeOperation implements FilteringOperation {
        /**
         * Indicates a logical product.
         *
         * @since 3.0.0
         */
        AND(FilteringOperationKing.NODE),
        /**
         * Indicates a logical sum.
         *
         * @since 3.0.0
         */
        OR(FilteringOperationKing.NODE),
        /**
         * Indicates the negation of a logical sum.
         *
         * @since 3.0.0
         */
        NOT_OR(FilteringOperationKing.NODE);

        private final FilteringOperationKing kind;

        private NodeOperation(FilteringOperationKing kind) {
            this.kind = Objects.requireNonNull(kind);
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FilteringOperationKing getKind() {
            return kind;
        }
    }

    /**
     * JSON deserializer for {@code FilteringOperation}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<FilteringOperation> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FilteringOperation deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return FilteringOperation.valueOf(ctx.deserialize(String.class, parser));
        }
    }
}
