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
import java.util.AbstractMap;
import java.util.Objects;
import java.util.Set;

/**
 * The {@link JsonObject} that can be serialized. Exactly the same as the original except that it can be serialized.
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
public class JsonObjectValue extends AbstractMap<String, JsonValue> implements JsonObject, Serializable {

    private static final long serialVersionUID = 6337206561334398852L;

    private transient JsonObject value; // Note: immutable

    /**
     * Construct with {@code JsonObject}.
     *
     * @param value an any JSON object
     * @throws NullPointerException if {@code value} is {@code null}
     * @since 3.0.0
     */
    public JsonObjectValue(JsonObject value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return value.entrySet();
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public JsonArray getJsonArray(String name) {
        return value.getJsonArray(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public JsonObject getJsonObject(String name) {
        return value.getJsonObject(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public JsonNumber getJsonNumber(String name) {
        return value.getJsonNumber(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public JsonString getJsonString(String name) {
        return value.getJsonString(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public String getString(String name) {
        return value.getString(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public String getString(String name, String defaultValue) {
        return value.getString(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public int getInt(String name) {
        return value.getInt(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public int getInt(String name, int defaultValue) {
        return value.getInt(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public boolean getBoolean(String name) {
        return value.getBoolean(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public boolean getBoolean(String name, boolean defaultValue) {
        return value.getBoolean(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public boolean isNull(String name) {
        return value.isNull(name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public ValueType getValueType() {
        return value.getValueType();
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 3.0.0
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
     * @since 3.0.0
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof JsonObject o && value.equals(o);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
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
     * @since 3.0.0
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
     * @since 3.0.0
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        try (var r = Json.createReader(new StringReader(stream.readUTF()))) {
            value = r.readObject();
        }
    }
}
