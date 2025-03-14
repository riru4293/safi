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
package jp.mydns.projectk.safi.util;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonCollectors;
import jakarta.json.stream.JsonParser;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import static java.util.function.Function.identity;
import java.util.function.Predicate;
import static java.util.function.Predicate.not;
import java.util.stream.Stream;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;

/**
 * Utilities for {@link JsonValue}.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class has not variable field member and it has all method is static.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class JsonValueUtils {

    private JsonValueUtils() {
    }

    /**
     * Returns a predicate that judges that the JSON value type and one of the specified types will match.
     *
     * @param valueTypes the types of JSON value you want to judging the match
     * @return {@code true} if match otherwise {@code false}
     *
     * <p>
     * {@code true} case examples      <pre>
     * - <code>typeEquals(ValueType.STRING).test(value of JsonString)</code>
     * - <code>typeEquals(ValueType.OBJECT, ValueType.STRING).test(value of JsonString)</code>
     * - <code>typeEquals(ValueType.OBJECT, ValueType.STRING).test(value of JsonObject)</code>
     * </pre>
     *
     * <p>
     * {@code false} case examples
     * <pre>
     * - <code>typeEquals(ValueType.OBJECT).test(value of JsonString)</code>
     * </pre>
     *
     * Usage example.
     * <pre>
     * {@code // processing JsonString only
     * List<JsonValue> values;
     * values.stream()
     *     .filter(typeEquals(ValueType.STRING)
     *     .forEach(...);
     * }
     * </pre>
     *
     * @throws NullPointerException if {@code valueTypes} is {@code null}
     * @since 3.0.0
     */
    public static Predicate<JsonValue> typeEquals(ValueType... valueTypes) {
        Objects.requireNonNull(valueTypes);

        return v -> Stream.of(valueTypes).anyMatch(v.getValueType()::equals);
    }

    /**
     * Returns the result of interpreting {@code JsonValue} as a {@code String}.
     *
     * @param jsonValue the {@code JsonValue}
     * @return the {@code String}
     * <p>
     * Note that JSON's null becomes java {@code null}, so be careful. It is recommended to use it in combination with
     * {@link Objects#nonNull} when used in stream processing.
     * <p>
     * Examples<pre>
     *   In case string : {@literal "oem"      -> oem}
     *   In case boolean: {@literal true       -> true}
     *   In case number : {@literal 33195      -> 33195}
     *   In case object : {@literal {"k": "v"} -> {"k": "v"}}
     *   In case array  : {@literal [1, 2]     -> [1, 2]}
     *   In case null   : {@literal null       -> }{@code null}
     * </pre>
     *
     * @throws NullPointerException if {@code jsonValue} is {@code null}
     * @since 3.0.0
     */
    public static String toString(JsonValue jsonValue) {
        return switch (jsonValue.getValueType()) {
            case NULL ->
                null;
            case STRING ->
                JsonString.class.cast(jsonValue).getString();
            default ->
                jsonValue.toString();
        };
    }

    /**
     * Returns a JSON string representation of time.
     *
     * @param time the {@code OffsetDateTime}. It's can be {@code null}.
     * @return a JSON string representation of time. That format is ISO8601. For example
     * {@literal 2000-01-01T12:34:56Z}. Return {@link JsonValue#NULL} if {@code time} is {@code null}.
     * @since 3.0.0
     */
    public static JsonValue toJsonValue(OffsetDateTime time) {
        return Optional.ofNullable(time).map(JsonValueUtils::asJsonValue).orElse(JsonValue.NULL);
    }

    private static JsonValue asJsonValue(OffsetDateTime time) {
        return Json.createValue(time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    /**
     * Returns a JSON object representation of file path.
     *
     * @param path the {@code Path}. It's can be {@code null}.
     * @return a JSON string representation of path. For example {@literal {"filename": "file:///tmp/file.extension"}}.
     * Return {@link JsonValue#NULL} if {@code path} is {@code null}.
     * @since 3.0.0
     */
    public static JsonValue toJsonValue(Path path) {
        return Optional.ofNullable(path).map(JsonValueUtils::asJsonValue).orElse(JsonValue.NULL);
    }

    private static JsonValue asJsonValue(Path path) {
        return Json.createObjectBuilder().add("filename", Json.createValue(path.toUri().toString())).build();
    }

    /**
     * Conversion to {@code JsonValue}.
     *
     * @param value conversion source
     * @param jsonb the {@code Jsonb}
     * @return converted value
     * @throws NullPointerException if any argument is {@code null}
     * @throws JsonbException if any unexpected error(s) occur(s) during conversion.
     * @since 3.0.0
     */
    public static JsonValue toJsonValue(Object value, Jsonb jsonb) {
        return jsonb.fromJson(Objects.requireNonNull(jsonb).toJson(Objects.requireNonNull(value)), JsonValue.class);
    }

    /**
     * Make a value stream from JSON array.
     *
     * @param jsonArray JSON array
     * @return array values. If close this then closes the underlying input source.
     * @throws NullPointerException if {@code jsonArray} is {@code null}
     * @throws IllegalArgumentException if {@code jsonArray} is not JSON array
     * @throws JsonException if encoding cannot be determined or I/O error
     * @since 3.0.0
     */
    public static Stream<JsonValue> toStream(InputStream jsonArray) {
        JsonParser jp = newJsonParser(Objects.requireNonNull(jsonArray));

        if (!jp.hasNext() || jp.next() != JsonParser.Event.START_ARRAY) {
            jp.close();
            throw new IllegalArgumentException("Malformed as JSON array.");
        }

        return jp.getArrayStream().onClose(jp::close);
    }

    private static JsonParser newJsonParser(InputStream is) {
        return Json.createParser(is);
    }

    /**
     * Merge two {@code JsonObject}. Do a deep merge if the keys are the same and both data types are object, otherwise
     * overwrite with the value of {@code ow}. If the value of {@code ow} is {@link JsonValue#NULL}, the key will be
     * removed.
     *
     * @param base base value
     * @param ow overwrite value
     * @return merged value
     * @throws NullPointerException if any argument is {@code null}
     * @since 3.0.0
     */
    public static JsonObject merge(JsonObject base, JsonObject ow) {
        Objects.requireNonNull(base);
        Objects.requireNonNull(ow);

        Predicate<String> canMerge = k -> Stream.of(base, ow)
            .map(m -> m.get(k)).allMatch(JsonObject.class::isInstance);

        Function<String, Entry<String, JsonValue>> toMerged = k -> Map.entry(
            k, merge(base.getJsonObject(k), ow.getJsonObject(k)));

        Stream<Entry<String, JsonValue>> originEntries = base.entrySet().stream()
            .filter(not(p(ow::containsKey, Entry::getKey)));

        Stream<Entry<String, JsonValue>> mergedEntries = ow.keySet().stream()
            .filter(canMerge).map(toMerged);

        Stream<Entry<String, JsonValue>> owEntries = ow.entrySet().stream()
            .filter(not(p(canMerge, Entry::getKey)))
            .filter(not(p(JsonValue.NULL::equals, Entry::getValue)));

        return Stream.of(originEntries, mergedEntries, owEntries).flatMap(identity())
            .collect(JsonCollectors.toJsonObject());
    }
}
