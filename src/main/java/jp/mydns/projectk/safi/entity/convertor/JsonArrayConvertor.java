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
package jp.mydns.projectk.safi.entity.convertor;

import jakarta.json.Json;
import static jakarta.json.JsonValue.EMPTY_JSON_ARRAY;
import jakarta.json.stream.JsonParsingException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.StringReader;
import java.util.Optional;
import jp.mydns.projectk.safi.value.JsonArrayValue;

/**
 * JPA attribute convertor for the {@code JsonArrayValue}. This convertor is applied automatically.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Converter(autoApply = true)
public final class JsonArrayConvertor implements AttributeConverter<JsonArrayValue, String> {

    /**
     * Convert to database column type.
     *
     * @param javaVal the {@code JsonArrayValue}
     * @return {@code javaVal} that converted to string representation of {@code JsonArrayValue}. Returns {@code []} if
     * {@code javaVal} is {@code null}.
     * @since 3.0.0
     */
    @Override
    public String convertToDatabaseColumn(JsonArrayValue javaVal) {
        return Optional.ofNullable(javaVal).map(JsonArrayValue::toString).orElseGet(EMPTY_JSON_ARRAY::toString);
    }

    /**
     * Convert to entity attribute type.
     *
     * @param dbVal value ​​retrieved from database. It must be a string representation of {@code JsonArrayValue}.
     * @return {@code dbVal} as {@code JsonArrayValue}. Returns {@code EMPTY_JSON_ARRAY} if {@code dbVal} is
     * {@code null}.
     * @throws JsonParsingException if {@code dbVal} is malformed as {@code JsonArrayValue}
     * @since 3.0.0
     */
    @Override
    public JsonArrayValue convertToEntityAttribute(String dbVal) {
        if (dbVal == null) {
            return new JsonArrayValue(EMPTY_JSON_ARRAY);
        }

        try (var r = Json.createReader(new StringReader(dbVal))) {
            return new JsonArrayValue(r.readArray());
        }
    }
}
