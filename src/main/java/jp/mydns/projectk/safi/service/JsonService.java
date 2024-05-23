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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.value.JsonObjectVo;

/**
 * JSON conversion service by the <i>Jakarta JSON Processing</i> and <i>Jakarta JSON Binding</i>.
 *
 * @author riru
 * @version 2.0.0
 * @since 2.0.0
 */
public interface JsonService {

    /**
     * Writes the Java object tree with root object object to a String instance as JSON.
     *
     * @param src the root object of the object content tree to be serialized
     * @return string instance with serialized JSON data
     * @throws NullPointerException if {@code src} is {@code null}
     * @since 2.0.0
     */
    String toJson(Object src);

    /**
     * Conversion to the {@code Map<String, String>}.
     *
     * @param src source value. It should support serialization to JSON-Object.
     * @return the {@code Map<String, String>}
     * @throws NullPointerException if {@code src} is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion
     * @since 2.0.0
     */
    Map<String, String> toStringMap(Object src);

    /**
     * Conversion to {@code JsonObject}.
     *
     * @param src source value. It should support serialization to JSON-Object.
     * @return the {@code JsonObject}
     * @throws NullPointerException if {@code src} is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion
     * @since 2.0.0
     */
    JsonObject toJsonObject(Object src);

    /**
     * Conversion to {@code JsonObjectVo}.
     *
     * @param src source value. It should support serialization to JSON-Object.
     * @return the {@code JsonObjectVo}
     * @throws NullPointerException if {@code obj} is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 2.0.0
     */
    JsonObjectVo toJsonObjectVo(Object src);

    /**
     * Convert to type of the {@code T} via JSON.
     *
     * @param <T> destination type. It should support deserialization from JSON.
     * @param src source value. It should support serialization to JSON.
     * @param clazz destination type
     * @return the {@code T}
     * @throws NullPointerException if any argument is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion
     * @since 2.0.0
     */
    <T> T convertViaJson(Object src, Class<T> clazz);

    /**
     * Conversion to {@code List<String>}.
     *
     * @param src the {@code JsonArray}
     * @return the {@code List<String>}
     * @throws NullPointerException if {@code src} is {@code null} or contains {@code null}
     * @since 2.0.0
     */
    List<String> toStringList(JsonArray src);

    /**
     * Conversion to {@code JsonArray}.
     *
     * @param src the {@code List<String>}
     * @return the {@code JsonArray}
     * @throws NullPointerException if {@code src} is {@code null} or contains {@code null}
     * @since 2.0.0
     */
    JsonArray toJsonStringList(List<String> src);

    /**
     * Make a {@code JsonValue} stream from {@code InputStream}. No close {@code jsonArray} if occurs an any
     * {@code RuntimeException}.
     *
     * @param jsonArray JSON array
     * @return {@code JsonValue} stream. If close this then closes the underlying input source.
     * @throws NullPointerException if {@code jsonArray} is {@code null}
     * @throws IllegalArgumentException if {@code jsonArray} is not JSON array
     * @throws JsonException if encoding cannot be determined or I/O error
     * @since 2.0.0
     */
    Stream<JsonValue> toStream(InputStream jsonArray);

    /**
     * Implementation of {@code JsonService}.
     *
     * @author riru
     * @version 2.0.0
     * @since 2.0.0
     */
    @ApplicationScoped
    class Stub implements JsonService {
// ToDo: Must be implement.

        @Override
        public String toJson(Object obj) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Map<String, String> toStringMap(Object src) {
            Jsonb jsonb = JsonbBuilder.create();
            return JsonObject.class.cast(src).entrySet().stream()
                    .collect(toMap(Entry::getKey, e -> e.getValue().toString()));
        }

        @Override
        public JsonObject toJsonObject(Object src) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public JsonObjectVo toJsonObjectVo(Object src) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public <T> T convertViaJson(Object src, Class<T> clazz) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<String> toStringList(JsonArray src) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public JsonArray toJsonStringList(List<String> src) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Stream<JsonValue> toStream(InputStream jsonArray) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
