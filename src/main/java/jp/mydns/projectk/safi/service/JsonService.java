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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import java.util.Objects;
import java.util.function.Function;
import jp.mydns.projectk.safi.util.JsonValueUtils;
import jp.mydns.projectk.safi.value.SJson;

/**
 Provides JSON conversion.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JsonService {

/**
 Conversion to {@code JsonValue}.

 @param value source value
 @return converted value
 @throws NullPointerException if {@code value} is {@code null}
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
JsonValue toJsonValue(Object value);

/**
 Deserialize to {@code T} from JSON.

 @param <T> destination type. It should support deserialization from JSON.
 @param json source value
 @param clazz destination type
 @return deserialized value
 @throws NullPointerException if any argument is {@code null}
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
<T> T fromJsonValue(JsonValue json, Class<T> clazz);

/**
 Merge two {@code JsonObject}.

 @param base base value
 @param ow overwrite value
 @return merged value
 @throws NullPointerException if any argument is {@code null}
 @since 3.0.0
 */
JsonObject merge(JsonObject base, JsonObject ow);

/**
 Conversion to {@code SJson}.

 @param value source value
 @return converted value
 @throws NullPointerException if {@code value} is {@code null}
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
SJson toSJson(Object value);

/**
 Returns a deserialize function to {@code T} from JSON.

 @param <T> destination type. It should support deserialization from JSON.
 @param clazz destination type
 @return deserialized value
 @throws NullPointerException if {@code clazz} is {@code null}
 @since 3.0.0
 */
<T> Function<JsonValue, T> fromJsonValue(Class<T> clazz);

/**
 Implements the {@code JsonService}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(JsonService.class)
@ApplicationScoped
class Impl implements JsonService {

private final Jsonb jsonb;

@SuppressWarnings("unused")
Impl() {
    // Note: The default constructor exists only to allow NetBeans to recognize the CDI bean.
    throw new UnsupportedOperationException();
}

@Inject
@SuppressWarnings("unused")
Impl(Jsonb jsonb) {
    this.jsonb = jsonb;
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code value} is {@code null}
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
@Override
public JsonValue toJsonValue(Object value) {
    return JsonValueUtils.toJsonValue(Objects.requireNonNull(value), jsonb);
}

/**
 {@inheritDoc}

 @throws NullPointerException if any argument is {@code null}
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
@Override
public <T> T fromJsonValue(JsonValue json, Class<T> clazz) {
    return jsonb.fromJson(Objects.requireNonNull(json).toString(), Objects.requireNonNull(clazz));
}

/**
 {@inheritDoc}

 @throws NullPointerException if any argument is {@code null}
 @since 3.0.0
 */
@Override
public JsonObject merge(JsonObject base, JsonObject ow) {
    return JsonValueUtils.merge(Objects.requireNonNull(base), Objects.requireNonNull(ow));
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code value} is {@code null}
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
@Override
public SJson toSJson(Object value) {
    return SJson.of(toJsonValue(value));
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code clazz} is {@code null}
 @since 3.0.0
 */
@Override
public <T> Function<JsonValue, T> fromJsonValue(Class<T> clazz) {
    Objects.requireNonNull(clazz);
    return j -> fromJsonValue(j, clazz);
}

}

}
