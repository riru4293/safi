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
import jakarta.json.JsonValue;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Wrapper of the {@link JsonValue}. This can be serialized.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class is serializable.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@JsonbTypeDeserializer(JsonWrapper.Deserializer.class)
public interface JsonWrapper extends Serializable {

    /**
     * Build with {@code JsonValue}.
     *
     * @param value an any JSON object
     * @return received as is
     * @throws NullPointerException if {@code value} is {@code null}
     * @since 3.0.0
     */
    static JsonWrapper of(JsonValue value) {
        return new Deserializer.Impl(Objects.requireNonNull(value));
    }

    /**
     * Get wrapped value.
     *
     * @return wrapped value
     * @since 3.0.0
     */
    JsonValue unwrap();

    /**
     * JSON deserializer for {@code JsonWrapper}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    public static class Deserializer implements JsonbDeserializer<JsonWrapper> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public JsonWrapper deserialize(JsonParser jp, DeserializationContext dc, Type type) {
            return new Impl(jp.getObject());
        }

        protected static class Impl implements JsonWrapper {

            private static final long serialVersionUID = 6337206561334398852L;

            private transient JsonValue value; // Note: immutable

            /**
             * Construct with {@code JsonValue}.
             *
             * @param value an any JSON object
             * @throws NullPointerException if {@code value} is {@code null}
             * @since 3.0.0
             */
            public Impl(JsonValue value) {
                this.value = Objects.requireNonNull(value);
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public JsonValue unwrap() {
                return value;
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
                return other instanceof JsonWrapper o && value.equals(o.unwrap());
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
                    value = r.readValue();
                }
            }
        }
    }
}
