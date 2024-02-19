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
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import java.util.Map;
import java.util.Objects;
import static jp.mydns.projectk.safi.util.EntryUtils.compute;
import jp.mydns.projectk.safi.util.JsonUtils;
import static jp.mydns.projectk.safi.util.LambdaUtils.toLinkedHashMap;
import jp.mydns.projectk.safi.value.JsonObjectVo;

/**
 * Utilities for <i>JSON</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ApplicationScoped
public class JsonService {

    @Inject
    private Jsonb jsonb;

    /**
     * Writes the Java object tree with root object object to a String instance as JSON.
     *
     * @param obj the root object of the object content tree to be serialized
     * @return String instance with serialized JSON data
     * @throws NullPointerException if {@code obj} is {@code null}
     * @since 1.0.0
     */
    public String toJson(Object obj) {
        return jsonb.toJson(Objects.requireNonNull(obj));
    }

    /**
     * Conversion to the {@code Map<String, String>}.
     *
     * @param obj object that convertible to {@code JsonObject}
     * @return the value converted to {@code Map<String, String>}
     * @throws NullPointerException if {@code obj} is {@code null}
     * @throws ClassCastException if {@code obj} is {@code JsonValue} but not {@code JsonObject}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 1.0.0
     */
    public Map<String, String> toStringMap(Object obj) {
        return toJsonObject(Objects.requireNonNull(obj)).entrySet().stream()
                .map(compute(JsonUtils::toString)).collect(toLinkedHashMap());
    }

    /**
     * Conversion to {@code JsonObject}.
     *
     * @param obj source value
     * @return the value converted to the {@code JsonObject}
     * @throws NullPointerException if {@code obj} is {@code null}
     * @throws ClassCastException if {@code obj} is {@code JsonValue} but not {@code JsonObject}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 1.0.0
     */
    public JsonObject toJsonObject(Object obj) {
        return convertViaJson(Objects.requireNonNull(obj), JsonObject.class);
    }

    /**
     * Conversion to {@code JsonObjectVo}.
     *
     * @param obj source value
     * @return the value converted to the {@code JsonObjectVo}
     * @throws NullPointerException if {@code obj} is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @see #toJsonObject(java.lang.Object)
     * @see #toJsonObjectVo(jakarta.json.JsonObject)
     * @since 1.0.0
     */
    public JsonObjectVo toJsonObjectVo(Object obj) {
        return new JsonObjectVo(toJsonObject(Objects.requireNonNull(obj)));
    }

    /**
     * Convert to type of the {@code T} via JSON.
     *
     * @param <T> destination type. It should support deserialization from JSON.
     * @param obj source value. It should support serialization to JSON.
     * @param clazz destination type
     * @return the value converted to the {@code T}
     * @throws NullPointerException if any argument is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 1.0.0
     */
    public <T> T convertViaJson(Object obj, Class<T> clazz) {
        Objects.requireNonNull(obj);
        Objects.requireNonNull(clazz);

        if (obj.getClass() == clazz) {
            return clazz.cast(obj);
        }

        return switch (obj) {
            case String s ->
                jsonb.fromJson(s, clazz);

            case JsonValue v ->
                jsonb.fromJson(v.toString(), clazz);

            default ->
                jsonb.fromJson(jsonb.toJson(obj), clazz);
        };
    }
}
