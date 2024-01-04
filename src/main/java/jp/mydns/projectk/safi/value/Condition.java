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
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.Collections;
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
 * JSON Format<pre><code>
 * {
 *     "operation": "AND",
 *     "children": [
 *         {
 *             "operation": "FORWARD_MATCH",
 *             "name": "value",
 *             "value": "The"
 *         },
 *         {
 *             "operation": "BACKWARD_MATCH",
 *             "name": "value",
 *             "value": "er"
 *         }
 *     ]
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(Condition.Deserializer.class)
@Schema(description = "Filtering condition.")
public interface Condition {

    /**
     * {@code true} if multiple filtering condition.
     *
     * @return {@code true} if multiple filtering condition, otherwise {@code false}.
     * @see Multi
     * @since 1.0.0
     */
    @JsonbTransient
    boolean isMulti();

    /**
     * Get filtering operation.
     *
     * @return filtering operation
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Filtering operation.")
    FilteringOperation getOperation();

    /**
     * Get this as {@code Single}.
     *
     * @return single filtering condition
     * @throws ClassCastException if not assignable to {@code Single}
     * @since 1.0.0
     */
    default Single asSingle() {
        return Single.class.cast(this);
    }

    /**
     * Get this as {@code Multi}.
     *
     * @return multiple filtering condition
     * @throws ClassCastException if not assignable to {@code Multi}
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
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
     * Combination of filtering conditions.
     *
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * </ul>
     *
     * <pre>JSON Example:
     * <code>
     * {
     *     "operation": "AND",
     *     "children": []
     * }
     * </code></pre>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @Schema(description = "Combination of filtering conditions.")
    interface Multi extends Condition {

        /**
         * Get child conditions.
         *
         * @return children child conditions
         * @since 1.0.0
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
     * <pre>JSON Example:
     * <code>
     * {
     *     "name": "userName",
     *     "operation": "FORWARD_MATCH",
     *     "value": "p"
     * }
     * </code></pre>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @Schema(description = "A single filtering condition.")
    public interface Single extends Condition {

        /**
         * Get filtering target name.
         *
         * @return target name
         * @since 1.0.0
         */
        @NotNull
        String getName();

        /**
         * Get filtering value.
         *
         * @return filtering value
         * @since 1.0.0
         */
        @NotNull
        String getValue();
    }

    /**
     * JSON deserializer for {@code Condition}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<Condition> {

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Condition deserialize(JsonParser jp, DeserializationContext dc, Type type) {

            Bean tmp = dc.deserialize(Bean.class, jp);

            if (tmp == null) {
                return null;
            }

            if (tmp.isEmpty()) {
                return new MultiBean(FilteringOperation.Multi.AND, List.of());
            }

            return tmp.isMulti()
                    ? new MultiBean(tmp.getOperation(), tmp.getChildren())
                    : new SingleBean(tmp.getOperation(), tmp.getName(), tmp.getValue());
        }

        protected static class SingleBean implements Single {

            private final FilteringOperation operation;
            private final String name;
            private final String value;

            public SingleBean(FilteringOperation operation, String name, String value) {
                this.operation = operation;
                this.name = name;
                this.value = value;
            }

            @Override
            public FilteringOperation getOperation() {
                return operation;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public boolean isMulti() {
                return false;
            }

            @Override
            public String toString() {
                return "Condition.Single{" + "operation=" + operation + ", name=" + name + ", value=" + value + '}';
            }
        }

        protected static class MultiBean implements Multi {

            private final FilteringOperation operation;
            private final List<Condition> children;

            public MultiBean(FilteringOperation operation, List<Condition> children) {
                this.operation = operation;
                this.children = children;
            }

            @Override
            public FilteringOperation getOperation() {
                return operation;
            }

            @Override
            public List<Condition> getChildren() {
                return Optional.ofNullable(children).map(Collections::unmodifiableList).orElse(null);
            }

            @Override
            public boolean isMulti() {
                return true;
            }

            @Override
            public String toString() {
                return "Condition.Multi{" + "operation=" + operation + ", children=" + children + '}';
            }
        }

        protected static class Bean implements Condition {

            private static final Set<String> multiOpNames = Stream.of(FilteringOperation.Multi.values())
                    .map(Enum::name).collect(toUnmodifiableSet());

            private FilteringOperation operation;
            private String name;
            private String value;
            private List<Condition> children;

            @Override
            public FilteringOperation getOperation() {
                return operation;
            }

            public void setOperation(FilteringOperation operation) {
                this.operation = operation;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public List<Condition> getChildren() {
                return children;
            }

            public void setChildren(List<Condition> children) {
                this.children = children;
            }

            @Override
            public boolean isMulti() {
                return Optional.ofNullable(operation).map(FilteringOperation::name)
                        .filter(multiOpNames::contains).isPresent();
            }

            public boolean isEmpty() {
                return Stream.of(name, operation, value, children).allMatch(Objects::isNull);
            }
        }
    }
}
