/*
 * Copyright 2025, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
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
 Serializable JSON value. It is wrapper of the {@link JsonValue}.

 <p>
 Implementation requirements.
 <ul>
     <li>This class is immutable and thread-safe.</li>
     <li>This class is serializable.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@JsonbTypeDeserializer(JsonValueWrapper.Deserializer.class)
public interface JsonValueWrapper extends Serializable
{
    /**
     Construct with {@code JsonValue}.

     @param value an any {@code JsonValue}
     @return the {@code JsonValueWrapper} wrapped {@code value}
     @throws NullPointerException if {@code value} is {@code null}
     @since 3.0.0
     */
    static JsonValueWrapper of(JsonValue value)
    {
        return new Deserializer.Impl(Objects.requireNonNull(value));
    }

    /**
     Get unwrapped value.

     @return unwrapped value
     @since 3.0.0
     */
    JsonValue unwrap();

    /**
     JSON deserializer for {@code JsonValueWrapper}.

     @author riru
     @version 3.0.0
     @since 3.0.0
     */
    class Deserializer implements JsonbDeserializer<JsonValueWrapper>
    {
        /**
         {@inheritDoc}

         @since 3.0.0
         */
        @Override
        public JsonValueWrapper deserialize(JsonParser jp, DeserializationContext dc, Type type)
        {
            return new Impl(dc.deserialize(JsonValue.class, jp));
        }

        @JsonbTypeSerializer(JsonValueWrapper.Serializer.class)
        private static class Impl implements JsonValueWrapper
        {
            @java.io.Serial
            private static final long serialVersionUID = 6337206561334398852L;

            private transient JsonValue value; // Note: Mutable definition for deserialization purposes, but immutable.

            private Impl(JsonValue value)
            {
                this.value = Objects.requireNonNull(value);
            }

            @Override
            public JsonValue unwrap()
            {
                return value;
            }

            @Override
            public int hashCode()
            {
                return value.hashCode();
            }

            @Override
            public boolean equals(Object other)
            {
                return other instanceof JsonValueWrapper o && value.equals(o.unwrap());
            }

            @Override
            public String toString()
            {
                return value.toString();
            }

            @java.io.Serial
            private void writeObject(ObjectOutputStream stream) throws IOException
            {
                stream.defaultWriteObject();
                stream.writeUTF(value.toString());
            }

            @java.io.Serial
            private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
            {
                stream.defaultReadObject();

                try (var r = Json.createReader(new StringReader(stream.readUTF())))
                {
                    value = r.readValue();
                }
            }
        }
    }

    /**
     JSON serializer for {@code JsonValueWrapper}.

     @author riru
     @version 3.0.0
     @since 3.0.0
     */
    class Serializer implements JsonbSerializer<JsonValueWrapper>
    {
        /**
         {@inheritDoc}

         @since 3.0.0
         */
        @Override
        public void serialize(JsonValueWrapper obj, JsonGenerator generator, SerializationContext ctx)
        {
            generator.write(obj.unwrap());
        }
    }
}
