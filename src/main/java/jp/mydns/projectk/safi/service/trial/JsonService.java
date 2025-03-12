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
package jp.mydns.projectk.safi.service.trial;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import java.util.Objects;
import jp.mydns.projectk.safi.util.trial.JsonValueUtils;
import jp.mydns.projectk.safi.value.JsonObjectValue;

/**
 * Provides JSON conversion.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class JsonService {

    private final Jsonb jsonb;

    /**
     * Constructor.
     *
     * @param jsonb the {@code Jsonb}
     * @since 3.0.0
     */
    @Inject
    public JsonService(Jsonb jsonb) {
        this.jsonb = jsonb;
    }

    /**
     * Conversion to {@code JsonObject}. It is wrapper method of the
     * {@link JsonValueUtils#toJsonObject(java.lang.Object, jakarta.json.bind.Jsonb)}.
     *
     * @param o source value
     * @return converted value
     * @throws NullPointerException if {@code o} is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 3.0.0
     */
    public JsonObject toJsonObject(Object o) {
        return JsonValueUtils.toJsonObject(Objects.requireNonNull(o), jsonb);
    }

    /**
     * Merge two {@code JsonObject}. It is wrapper method of the
     * {@link JsonValueUtils#merge(jakarta.json.JsonObject, jakarta.json.JsonObject)}.
     *
     * @param base base value
     * @param ow overwrite value
     * @return merged value
     * @throws NullPointerException if any argument is {@code null}
     * @since 3.0.0
     */
    public JsonObject merge(JsonObject base, JsonObject ow) {
        return JsonValueUtils.merge(Objects.requireNonNull(base), Objects.requireNonNull(ow));
    }

    /**
     * Returns a {@code JsonObjectValue} representation of the {@code o}.
     *
     * @param o the {@code JsonObject}
     * @return {@code JsonObjectValue} representation of the {@code o}
     * @throws NullPointerException if {@code o} is {@code null}
     * @since 3.0.0
     */
    public JsonObjectValue toJsonObjectValue(JsonObject o) {
        return new JsonObjectValue(Objects.requireNonNull(o));
    }

    /**
     * Conversion to {@code JsonObjectValue}.
     *
     * @param o source value
     * @return converted value
     * @throws NullPointerException if {@code o} is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @see #toJsonObject(java.lang.Object)
     * @see #toJsonObjectValue(jakarta.json.JsonObject)
     * @since 3.0.0
     */
    public JsonObjectValue toJsonObjectValue(Object o) {
        return toJsonObjectValue(toJsonObject(Objects.requireNonNull(o)));
    }

    /**
     * Deserialize to {@code T} from JSON.
     *
     * @param <T> destination type. It should support deserialization from JSON.
     * @param json source value
     * @param clazz destination type
     * @return deserialized value
     * @throws NullPointerException if any argument is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 3.0.0
     */
    public <T> T fromJsonObject(JsonObject json, Class<T> clazz) {
        return jsonb.fromJson(Objects.requireNonNull(json).toString(), Objects.requireNonNull(clazz));
    }

    /**
     * Convert to type of {@code T} via JSON.
     *
     * @param <T> destination type. It should support deserialization from JSON.
     * @param o source value. It should support serialization to JSON.
     * @param clazz destination type
     * @return converted value
     * @throws NullPointerException if any argument is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 3.0.0
     */
    public <T> T convertViaJson(Object o, Class<T> clazz) {
        return jsonb.fromJson(toJsonObject(Objects.requireNonNull(o)).toString(), Objects.requireNonNull(clazz));
    }
}
