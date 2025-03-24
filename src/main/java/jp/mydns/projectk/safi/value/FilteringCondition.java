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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toUnmodifiableSet;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.FilteringOperationKing;
import jp.mydns.projectk.safi.validator.OptionalNotEmpty;
import jp.mydns.projectk.safi.value.FilteringCondition.LeafCondition;
import jp.mydns.projectk.safi.value.FilteringCondition.NodeCondition;

/**
 * Filtering condition. Combines some conditions is also possible.
 * <p>
 * This class represents structured filtering condition. It is a format that allows content analysis, and can also be
 * converted bidirectionally to and from plain text.
 * <p>
 * A common use is to dynamically generate conditional parts of SQL. This can be achieved by separately preparing a
 * parser and SQL extraction condition generator specifically for this class. The ability to convert to a plain text
 * representation increases availability. It is easy to include in communications or store in a database or file.
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
@JsonbTypeDeserializer(FilteringCondition.Deserializer.class)
@Schema(description
    = """
Filtering condition.

# Examples

## Case userName property is equal to taro
```
{"operation": "EQUAL", "name": "userName", "value": "taro"}
```

## Case userName property is equal to taro or jiro
```
{"operation": "OR", "children": [
    {"operation": "EQUAL", "name": "userName", "value": "taro"},
    {"operation": "EQUAL", "name": "userName", "value": "jiro"}]}
```""",
        oneOf = {LeafCondition.class, NodeCondition.class})
public interface FilteringCondition {

    /**
     * Get filtering operation.
     *
     * @return filtering operation
     * @since 3.0.0
     */
    @NotNull(groups = Default.class)
    FilteringOperation getOperation();

    /**
     * Get filtering target name.
     *
     * @return target name
     * @since 3.0.0
     */
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull(groups = Default.class)
    Optional<String> getName();

    /**
     * Get filtering value.
     *
     * @return filtering value
     * @since 3.0.0
     */
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull(groups = Default.class)
    Optional<String> getValue();

    /**
     * Get child conditions.
     *
     * @return children child conditions
     * @since 3.0.0
     */
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull
    Optional<List<@NotNull(groups = Default.class) @Valid FilteringCondition>> getChildren();

    /**
     * Get this as {@code LeafOperation}.
     *
     * @return single filtering condition
     * @throws ClassCastException if not assignable to {@code LeafOperation}
     * @since 3.0.0
     */
    default LeafCondition asSingle() {
        return LeafCondition.class.cast(this);
    }

    /**
     * Get this as {@code NodeOperation}.
     *
     * @return multiple filtering condition
     * @throws ClassCastException if not assignable to {@code NodeOperation}
     * @since 3.0.0
     */
    default NodeCondition asMulti() {
        return NodeCondition.class.cast(this);
    }

    /**
     * Build a new single filtering condition.
     *
     * @param op single filtering operation
     * @param name item name
     * @param value item value
     * @return a new single filtering condition
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code op} is not a single filtering operation
     * @since 3.0.0
     */
    static LeafCondition singleOf(FilteringOperation op, String name, String value) {
        Objects.requireNonNull(op);
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        try {
            FilteringOperation.LeafOperation.class.cast(op);
        } catch (ClassCastException ignore) {
            throw new IllegalArgumentException("Incorrect filtering operation.");
        }

        return new Deserializer.SingleBean(op, name, value);
    }

    /**
     * Build a new multiple filtering condition.
     *
     * @param op multi filtering operation
     * @param children child filtering conditions
     * @return a new multiple filtering condition
     * @throws NullPointerException if any argument is {@code null} or contains {@code null} in {@code children}
     * @throws IllegalArgumentException if {@code op} is not a multiple filtering operation
     * @since 3.0.0
     */
    static NodeCondition multiOf(FilteringOperation op, List<FilteringCondition> children) {
        Objects.requireNonNull(op);
        Objects.requireNonNull(children);

        try {
            FilteringOperation.NodeOperation.class.cast(op);
        } catch (ClassCastException ignore) {
            throw new IllegalArgumentException("Incorrect filtering operation.");
        }

        return new Deserializer.MultiBean(op, children);
    }

    /**
     * Combination of filtering conditions.
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
    @Schema(name = "FilteringCondition.Multi", description = "Combination of filtering conditions.")
    interface NodeCondition extends FilteringCondition {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        @Schema(implementation = FilteringOperation.NodeOperation.class)
        public FilteringOperation getOperation();

        /**
         * Get child conditions.
         *
         * @return children child conditions
         * @since 3.0.0
         */
        @Override
        @OptionalNotEmpty(groups = Default.class)
        Optional<List<FilteringCondition>> getChildren();
    }

    /**
     * A single filtering condition.
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
    @Schema(name = "FilteringCondition.Single", description = "A single filtering condition.")
    public interface LeafCondition extends FilteringCondition {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        @Schema(implementation = FilteringOperation.LeafOperation.class)
        public FilteringOperation getOperation();

        /**
         * Get filtering target name.
         *
         * @return target name
         * @since 3.0.0
         */
        @Override
        @OptionalNotEmpty(groups = Default.class)
        Optional<String> getName();

        /**
         * Get filtering value.
         *
         * @return filtering value
         * @since 3.0.0
         */
        @Override
        @OptionalNotEmpty(groups = Default.class)
        Optional<String> getValue();
    }

    /**
     * JSON deserializer for {@code FilteringCondition}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<FilteringCondition> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FilteringCondition deserialize(JsonParser jp, DeserializationContext dc, Type type) {

            Bean tmp = dc.deserialize(Bean.class, jp);

            return Optional.ofNullable(tmp).map(FilteringCondition::getOperation).map(FilteringOperation::getKind)
                .filter(FilteringOperationKing.NODE::equals).isPresent()
                ? new MultiBean(tmp.getOperation(), tmp.getChildren().orElse(null))
                : new SingleBean(tmp.getOperation(), tmp.getName().orElse(null), tmp.getValue().orElse(null));
        }

        /**
         * Implements of the {@code FilteringCondition.LeafOperation}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class SingleBean implements LeafCondition {

            private final FilteringOperation operation;
            private final String name;
            private final String value;

            private SingleBean(FilteringOperation operation, String name, String value) {
                this.operation = operation;
                this.name = name;
                this.value = value;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public FilteringOperation getOperation() {
                return operation;
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
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<String> getValue() {
                return Optional.ofNullable(value);
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTransient
            public Optional<List<FilteringCondition>> getChildren() {
                return Optional.empty();
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "FilteringCondition.Single{" + "operation=" + operation + ", name=" + name + ", value=" + value
                    + '}';
            }
        }

        /**
         * Implements of the {@code FilteringCondition.NodeOperation}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class MultiBean implements NodeCondition {

            private final FilteringOperation operation;
            private final List<FilteringCondition> children;

            private MultiBean(FilteringOperation operation, List<FilteringCondition> children) {
                this.operation = operation;
                this.children = children;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public FilteringOperation getOperation() {
                return operation;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTransient
            public Optional<String> getName() {
                return Optional.empty();
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @JsonbTransient
            public Optional<String> getValue() {
                return Optional.empty();
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<List<FilteringCondition>> getChildren() {
                return Optional.ofNullable(children);
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "FilteringCondition.Multi{" + "operation=" + operation + ", children=" + children + '}';
            }
        }

        /**
         * Implements of the {@code FilteringCondition}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean implements FilteringCondition {

            private static final Set<String> multiOpNames = Stream.of(FilteringOperation.NodeOperation.values())
                .map(Enum::name).collect(toUnmodifiableSet());

            private FilteringOperation operation;
            private String name;
            private String value;
            private List<FilteringCondition> children;

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public FilteringOperation getOperation() {
                return operation;
            }

            /**
             * Set filtering operation.
             *
             * @param operation filtering operation
             * @since 3.0.0
             */
            public void setOperation(FilteringOperation operation) {
                this.operation = operation;
            }

            /**
             * Get filtering target name.
             *
             * @return target name
             * @since 3.0.0
             */
            @Override
            public Optional<String> getName() {
                return Optional.ofNullable(name);
            }

            /**
             * Set filtering target name.
             *
             * @param name target name
             * @since 3.0.0
             */
            public void setName(String name) {
                this.name = name;
            }

            /**
             * Get filtering value.
             *
             * @return filtering value
             * @since 3.0.0
             */
            @Override
            public Optional<String> getValue() {
                return Optional.ofNullable(value);
            }

            /**
             * Set filtering value.
             *
             * @param value filtering value
             * @since 3.0.0
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Get child filtering conditions.
             *
             * @return children child filtering conditions
             * @since 3.0.0
             */
            @Override
            public Optional<List<FilteringCondition>> getChildren() {
                return Optional.ofNullable(children);
            }

            /**
             * Set child filtering conditions.
             *
             * @param children child filtering conditions
             * @since 3.0.0
             */
            public void setChildren(List<FilteringCondition> children) {
                this.children = children;
            }
        }
    }
}
