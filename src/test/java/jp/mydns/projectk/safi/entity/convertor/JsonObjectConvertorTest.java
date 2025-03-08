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
import jakarta.json.JsonValue;
import jp.mydns.projectk.safi.value.JsonObjectValue;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code JsonObjectConvertor}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class JsonObjectConvertorTest {

    /**
     * Test of convertToDatabaseColumn method.
     *
     * @since 3.0.0
     */
    @Test
    void testConvertToDatabaseColumn() {
        var expect = "{\"k\":\"v\"}";

        var result = new JsonObjectConvertor().convertToDatabaseColumn(new JsonObjectValue(Json.createObjectBuilder()
            .add("k", "v").build()));

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of convertToDatabaseColumn method if null.
     *
     * @since 3.0.0
     */
    @Test
    void testConvertToDatabaseColumnIfNull() {
        var result = new JsonObjectConvertor().convertToDatabaseColumn(null);

        assertThat(result).isEqualTo("{}");
    }

    /**
     * Test of convertToEntityAttribute method.
     *
     * @since 3.0.0
     */
    @Test
    void testConvertToEntityAttribute() {
        var expect = JsonValue.EMPTY_JSON_OBJECT;

        var result = new JsonObjectConvertor().convertToEntityAttribute("{}");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of convertToEntityAttribute method if null.
     *
     * @since 3.0.0
     */
    @Test
    void testConvertToEntityAttributeIfNull() {
        var expect = JsonValue.EMPTY_JSON_OBJECT;

        var result = new JsonObjectConvertor().convertToEntityAttribute(null);

        assertThat(result).isEqualTo(expect);
    }
}
