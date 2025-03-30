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
import jakarta.validation.groups.Default;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.util.CollectionUtils;

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
@JsonbTypeDeserializer(FilteringConditionValue.Deserializer.class)
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
        example = "{\"operation\": \"AND\", \"children\": [{\"operation\": \"EQUAL\", \"name\": \"kind\","
        + " \"value\": \"A\"}, {\"operation\": \"LESS_THAN\", \"name\": \"version\", \"value\": \"3\"}]}",
        oneOf = {LeafConditionValue.class, NodeConditionValue.class})
public interface FilteringConditionValue extends ValueTemplate {

    /**
     * Get filtering operation.
     *
     * @return the {@code FilteringOperationValue}
     * @since 3.0.0
     */
    @NotNull(groups = Default.class)
    @Schema(description = "Filtering operation kind.")
    FilteringOperationValue getOperation();

    /**
     * Abstract builder of the {@code FilteringConditionValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends FilteringConditionValue>
        extends ValueTemplate.AbstractBuilder<B, V> {

        protected final FilteringOperationValue operation;

        /**
         * Constructor.
         *
         * @param builderType builder type
         * @param operation the {@code FilteringOperationValue}
         * @throws NullPointerException if any argument is {@code null}
         * @since 3.0.0
         */
        protected AbstractBuilder(Class<B> builderType, FilteringOperationValue operation) {
            super(Objects.requireNonNull(builderType));
            this.operation = Objects.requireNonNull(operation);
        }

        /**
         * Abstract implements of the {@code FilteringConditionValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected abstract static class AbstractBean implements FilteringConditionValue {

            protected final FilteringOperationValue operation;

            /**
             * Constructor.
             *
             * @param builder the {@code SchedefValue.AbstractBuilder}
             * @since 3.0.0
             */
            protected AbstractBean(AbstractBuilder<?, ?> builder) {
                this.operation = builder.operation;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public FilteringOperationValue getOperation() {
                return operation;
            }
        }
    }

    /**
     * JSON deserializer for {@code FilteringConditionValue}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<FilteringConditionValue> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public FilteringConditionValue deserialize(JsonParser jp, DeserializationContext dc, Type type) {

            Bean tmp = dc.deserialize(Bean.class, jp);

            return Optional.ofNullable(tmp).map(FilteringConditionValue::getOperation).map(o -> switch (o.getKind()) {
                case NODE ->
                    new NodeConditionValue.Builder(o).with(tmp).unsafeBuild();
                case LEAF ->
                    new LeafConditionValue.Builder(o).with(tmp).unsafeBuild();
            }).orElse(tmp);
        }

        /**
         * A {@code FilteringConditionValue} implementation specifically for JSON deserialization.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected static class Bean implements NodeConditionValue, LeafConditionValue {

            private FilteringOperationValue operation;
            private String name;
            private String value;
            private List<FilteringConditionValue> children;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected Bean() {
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public FilteringOperationValue getOperation() {
                return operation;
            }

            /**
             * Set filtering operation.
             *
             * @param operation the {@code FilteringOperationValue}
             * @since 3.0.0
             */
            public void setOperation(FilteringOperationValue operation) {
                this.operation = operation;
            }

            /**
             * Get property name to filter on.
             *
             * @return property name to filter on
             * @since 3.0.0
             */
            @Override
            public String getName() {
                return name;
            }

            /**
             * Set property name to filter on.
             *
             * @param name property name to filter on
             * @since 3.0.0
             */
            public void setName(String name) {
                this.name = name;
            }

            /**
             * Get value to filter on.
             *
             * @return value to filter on
             * @since 3.0.0
             */
            @Override
            public String getValue() {
                return value;
            }

            /**
             * Set value to filter on.
             *
             * @param value value to filter on
             * @since 3.0.0
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Get inner conditions.
             *
             * @return children inner conditions
             * @since 3.0.0
             */
            @Override
            public List<FilteringConditionValue> getChildren() {
                return children;
            }

            /**
             * Set child filtering conditions.
             *
             * @param children the {@code FilteringConditionValue} collection
             * @since 3.0.0
             */
            public void setChildren(List<FilteringConditionValue> children) {
                this.children = CollectionUtils.toUnmodifiable(children);
            }
        }
    }
}
