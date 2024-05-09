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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * The {@link JsonArray} that can be serialized. Exactly the same as the original except that it can be serialized.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class JsonArrayVo extends AbstractList<JsonValue> implements JsonArray, Serializable {

    private static final long serialVersionUID = 1349564549742480960L;

    private transient JsonArray value; // Note: immutable

    /**
     * Construct with {@code JsonArray}.
     *
     * @param value an any JSON array
     * @throws NullPointerException if {@code value} is {@code null}
     * @since 1.0.0
     */
    public JsonArrayVo(JsonArray value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonValue get(int index) {
        return value.get(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public int size() {
        return value.size();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonObject getJsonObject(int index) {
        return value.getJsonObject(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonArray getJsonArray(int index) {
        return value.getJsonArray(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonNumber getJsonNumber(int index) {
        return value.getJsonNumber(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public JsonString getJsonString(int index) {
        return value.getJsonString(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
        return value.getValuesAs(clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public String getString(int index) {
        return value.getString(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public String getString(int index, String defaultValue) {
        return value.getString(index, defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public int getInt(int index) {
        return value.getInt(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public int getInt(int index, int defaultValue) {
        return value.getInt(index, defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public boolean getBoolean(int index) {
        return value.getBoolean(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public boolean getBoolean(int index, boolean defaultValue) {
        return value.getBoolean(index, defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public boolean isNull(int index) {
        return value.isNull(index);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public ValueType getValueType() {
        return value.getValueType();
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Indicates that specified object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if matches otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof JsonArray && Objects.equals(value, other);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Serialize this instance.
     *
     * @param stream the {@code ObjectOutputStream}
     * @throws IOException if occurs I/O error
     * @since 1.0.0
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeUTF(value.toString());
    }

    /**
     * Deserialize this instance.
     *
     * @param stream the {@code ObjectInputStream}
     * @throws IOException if occurs I/O error
     * @throws ClassNotFoundException if the class of a serialized object could not be found
     * @since 1.0.0
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        try (var reader = Json.createReader(new StringReader(stream.readUTF()))) {
            value = reader.readArray();
        }
    }
}
