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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toUnmodifiableSet;
import java.util.stream.Stream;

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
 * <li>This class can be converted to JSON Object.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <p>
 * JSON format
 * <pre><code>
 * {
 *     "$schema": "https://json-schema.org/draft/2020-12/schema",
 *     "$id": "https://project-k.mydns.jp/safi/condition.schema.json",
 *     "title": "Filtdef",
 *     "description": "An information for filtering the contents.",
 *     "type": "object",
 *     "properties": {
 *         "operation": {
 *             "description": "Filtering operation.",
 *             "type": "string",
 *             "enum": [
 *                 "EQUAL",
 *                 "FORWARD_MATCH",
 *                 "PARTIAL_MATCH",
 *                 "BACKWARD_MATCH",
 *                 "GRATER_THAN",
 *                 "LESS_THAN",
 *                 "IS_NULL",
 *                 "AND",
 *                 "OR",
 *                 "NOT_OR"
 *             ]
 *         },
 *         "name": {
 *             "description": "Name of element to be filtered.",
 *             "type": "string",
 *             "minLength": 1
 *         },
 *         "value": {
 *             "description": "Value to filter.",
 *             "type": "string"
 *         },
 *         "children": {
 *             "description": "Chile condition collection.",
 *             "$ref": "https://project-k.mydns.jp/safi/condition.schema.json"
 *         }
 *     },
 *     "required": [
 *         "operation"
 *     ],
 *     "allOf": [
 *         {
 *             "if": {
 *                 "properties": {
 *                     "operation": {
 *                         "pattern": "[(AND)|(OR)|(NOT_OR)]"
 *                     }
 *                 }
 *             },
 *             "then": {
 *                 "required": [
 *                     "children"
 *                 ],
 *                 "allOf": [
 *                     {
 *                         "not": {
 *                             "required": [
 *                                 "name",
 *                                 "value"
 *                             ]
 *                         }
 *                     }
 *                 ]
 *             },
 *             "else": {
 *                 "required": [
 *                     "name",
 *                     "value"
 *                 ],
 *                 "allOf": [
 *                     {
 *                         "not": {
 *                             "required": [
 *                                 "children"
 *                             ]
 *                         }
 *                     }
 *                 ]
 *             }
 *         }
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(Condition.Deserializer.class)
@Schema(description = "Filtering condition.")
public interface Condition {

    /**
     * {@code true} if multiple filtering condition.
     *
     * @return {@code true} if multiple filtering condition, otherwise {@code false}.
     * @see Multi
     * @since 3.0.0
     */
    @JsonbTransient
    boolean isMulti();

    /**
     * Get filtering operation.
     *
     * @return filtering operation
     * @since 3.0.0
     */
    @Schema(description = "Filtering operation.")
    @NotNull
    FilteringOperation getOperation();

    /**
     * Get this as {@code Single}.
     *
     * @return single filtering condition
     * @throws ClassCastException if not assignable to {@code Single}
     * @since 3.0.0
     */
    default Single asSingle() {
        return Single.class.cast(this);
    }

    /**
     * Get this as {@code Multi}.
     *
     * @return multiple filtering condition
     * @throws ClassCastException if not assignable to {@code Multi}
     * @since 3.0.0
     */
    default Multi asMulti() {
        return Multi.class.cast(this);
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
    static Single singleOf(FilteringOperation op, String name, String value) {
        Objects.requireNonNull(op);
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        try {
            op.asSingle();
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
    static Multi multiOf(FilteringOperation op, List<Condition> children) {
        Objects.requireNonNull(op);
        Objects.requireNonNull(children);

        try {
            op.asMulti();
        } catch (ClassCastException ignore) {
            throw new IllegalArgumentException("Incorrect filtering operation.");
        }

        return new Deserializer.MultiBean(op, children);
    }

    /**
     * Get the {@code Condition} that have no effect
     *
     * @return empty condition
     * @since 3.0.0
     */
    static Condition emptyCondition() {
        return new Multi() {

            /**
             * {@inheritDoc}
             *
             * @return empty
             * @since 3.0.0
             */
            @Override
            public List<Condition> getChildren() {
                return List.of();
            }

            /**
             * {@inheritDoc}
             *
             * @return {@code true}
             * @since 3.0.0
             */
            @Override
            public boolean isMulti() {
                return true;
            }

            /**
             * {@inheritDoc}
             *
             * @return the {@code FilteringOperation.Multi.AND}
             * @since 3.0.0
             */
            @Override
            public FilteringOperation getOperation() {
                return FilteringOperation.Multi.AND;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "Condition.Empty{}";
            }
        };
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
    @Schema(description = "Combination of filtering conditions.")
    interface Multi extends Condition {

        /**
         * Get child conditions.
         *
         * @return children child conditions
         * @since 3.0.0
         */
        @NotNull
        List<@NotNull @Valid Condition> getChildren();
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
    @Schema(description = "A single filtering condition.")
    public interface Single extends Condition {

        /**
         * Get filtering target name.
         *
         * @return target name
         * @since 3.0.0
         */
        @NotBlank
        String getName();

        /**
         * Get filtering value.
         *
         * @return filtering value
         * @since 3.0.0
         */
        @NotNull
        String getValue();
    }

    /**
     * JSON deserializer for {@code Condition}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<Condition> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public Condition deserialize(JsonParser jp, DeserializationContext dc, Type type) {

            Bean tmp = dc.deserialize(Bean.class, jp);

            if (tmp == null) {
                return null;
            }

            if (tmp.isEmpty()) {
                return emptyCondition();
            }

            return tmp.isMulti()
                ? new MultiBean(tmp.getOperation(), tmp.getChildren())
                : new SingleBean(tmp.getOperation(), tmp.getName(), tmp.getValue());
        }

        /**
         * Implements of the {@code Condition.Single}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class SingleBean implements Single {

            private final FilteringOperation operation;
            private final String name;
            private final String value;

            /**
             * Constructor.
             *
             * @param operation the {@code FilteringOperation}
             * @param name name of element to be filtered.
             * @param value value to filter
             * @since 3.0.0
             */
            public SingleBean(FilteringOperation operation, String name, String value) {
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
             * {@inheritDoc}
             *
             * @return {@code false}
             * @since 3.0.0
             */
            @Override
            public boolean isMulti() {
                return false;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "Condition.Single{" + "operation=" + operation + ", name=" + name + ", value=" + value + '}';
            }
        }

        /**
         * Implements of the {@code Condition.Multi}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class MultiBean implements Multi {

            private final FilteringOperation operation;
            private final List<Condition> children;

            /**
             * Constructor.
             *
             * @param operation the {@code FilteringOperation}
             * @param children child conditions
             */
            public MultiBean(FilteringOperation operation, List<Condition> children) {
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
            public List<Condition> getChildren() {
                return children;
            }

            /**
             * {@inheritDoc}
             *
             * @return {@code true}
             * @since 3.0.0
             */
            @Override
            public boolean isMulti() {
                return true;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 3.0.0
             */
            @Override
            public String toString() {
                return "Condition.Multi{" + "operation=" + operation + ", children=" + children + '}';
            }
        }

        /**
         * Implements of the {@code Condition}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean implements Condition {

            private static final Set<String> multiOpNames = Stream.of(FilteringOperation.Multi.values())
                .map(Enum::name).collect(toUnmodifiableSet());

            private FilteringOperation operation;
            private String name;
            private String value;
            private List<Condition> children;

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
            public String getName() {
                return name;
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
            public String getValue() {
                return value;
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
             * Get child conditions.
             *
             * @return children child conditions
             * @since 3.0.0
             */
            public List<Condition> getChildren() {
                return children;
            }

            /**
             * Set child conditions.
             *
             * @param children child conditions
             * @since 3.0.0
             */
            public void setChildren(List<Condition> children) {
                this.children = children;
            }

            /**
             * Returns a {@code true} if type of the {@link FilteringOperation} obtained with {@link #getOperation()} is
             * {@link Multi}.
             *
             * @return {@code true} if type of the {@link FilteringOperation} obtained with {@link #getOperation()} is
             * {@link Multi}, otherwise {@code false}.
             * @since 3.0.0
             */
            @Override
            public boolean isMulti() {
                return Optional.ofNullable(operation).map(FilteringOperation::name)
                    .filter(multiOpNames::contains).isPresent();
            }

            /**
             * Returns a {@code true} if all elements are {@code null}.
             *
             * @return {@code true} if all elements are {@code null}, otherwise {@code false}.
             * @since 3.0.0
             */
            public boolean isEmpty() {
                return Stream.of(name, operation, value, children).allMatch(Objects::isNull);
            }
        }
    }
}
