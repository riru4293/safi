// Copyright 2025, Project-K
// SPDX-License-Identifier: BSD-2-Clause
package jp.mydns.projectk.safi.value;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Serializable wrapper for {@link JsonValue} with support for <i>Jakarta JSON
 * Binding</i> serialization and deserialization.
 *
 * This class wraps any {@code JsonValue} and makes it compatible with
 * <i>Jakarta JSON Binding</i> serialization and deserialization. This allows
 * {@code JsonValue} to be used in environments where object serialization is
 * required.
 *
 * Serialization support is provided through custom implementations of
 * {@link JsonbSerializer} and {@link JsonbDeserializer}.
 *
 * Implementation requirements:
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class is serializable.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 */
@JsonbTypeSerializer(JsonValueWrapper.Serializer.class)
@JsonbTypeDeserializer(JsonValueWrapper.Deserializer.class)
public final class JsonValueWrapper implements Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 6337206561334398852L;

    // Note: Mutable definition for deserialization purposes, but immutable.
    private transient JsonValue value;

    /**
    * Creates a wrapper for the specified {@code JsonValue}.
     * 
    * @param any the {@code JsonValue} to wrap
    * @return a {@code JsonValueWrapper} that wraps {@code any}
     * @throws NullPointerException if {@code any} is {@code null}
     */
    public static JsonValueWrapper of(JsonValue any) {
        return new JsonValueWrapper(Objects.requireNonNull(any));
    }

    private JsonValueWrapper(JsonValue value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
    * Returns the {@code JsonValue} wrapped by this class.
     * 
     * @return the unwrapped {@code JsonValue}. Never {@code null}.
     */
    public JsonValue unwrap() {
        return value;
    }

    /**
     * Returns a string representation.
     * 
     * @return a string representation
     */
    @Override
    public String toString() {
        return "JsonValueWrapper{" + "value=" + value.toString() + '}';
    }

    @java.io.Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeUTF(value.toString());
    }

    @java.io.Serial
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        try (var r = Json.createReader(new StringReader(stream.readUTF()))) {
            value = r.readValue();
        }
    }

    /**
     * JSON deserializer for {@code JsonValueWrapper}.
     * 
     * <p>
    * Custom deserializer for {@code JsonValueWrapper} using
    * <i>Jakarta JSON Binding</i>. It reads JSON data, deserializes it as a
    * {@code JsonValue}, and wraps it in a {@code JsonValueWrapper}.
     * 
     * @author riru
     * @version 3.0.0
     */
    public static class Deserializer implements JsonbDeserializer<JsonValueWrapper> {
        /**
         * Default constructor.
         *
         * Required by <i>Jakarta JSON Binding</i> to instantiate the deserializer.
         */
        public Deserializer() {
        }

        /**
         * Deserializes JSON content into a {@code JsonValueWrapper}.
         * 
         * The deserialized {@code JsonValue} is wrapped and returned.
         * 
         * @param jp   the {@code JsonParser} used to read JSON content
         * @param dc   the {@code DeserializationContext} providing context for
         *             deserialization
         * @param type the {@code Type} of the object to deserialize
         * @return a {@code JsonValueWrapper} instance containing the deserialized
         *         {@code JsonValue}
         */
        @Override
        public JsonValueWrapper deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return new JsonValueWrapper(dc.deserialize(JsonValue.class, jp));
        }
    }

    /**
     * JSON serializer for {@code JsonValueWrapper}.
     *
     * <p>
    * Custom serializer for {@code JsonValueWrapper} using
    * <i>Jakarta JSON Binding</i>. It serializes the wrapped
    * {@code JsonValue} to JSON.
     *
     * @author riru
     * @version 3.0.0
     */
    public static class Serializer implements JsonbSerializer<JsonValueWrapper> {
        /**
         * Default constructor.
         *
         * Required by <i>Jakarta JSON Binding</i> to instantiate the serializer.
         */
        public Serializer() {
        }

        /**
         * Serializes a {@code JsonValueWrapper} to JSON.
         * 
         * Unwraps the given {@code JsonValueWrapper} and writes its
         * {@code JsonValue} to the JSON output.
         * 
         * @param obj the {@code JsonValueWrapper} instance to serialize
         * @param jg  the {@code JsonGenerator} used to write JSON content
         * @param ctx the {@code SerializationContext} providing context for
         *            serialization
         */
        @Override
        public void serialize(JsonValueWrapper obj, JsonGenerator jg, SerializationContext ctx) {
            jg.write(obj.unwrap());
        }
    }
}
