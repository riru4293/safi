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
import jakarta.json.JsonString;
import jakarta.json.bind.Jsonb;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code FilteringOperation}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
class FilteringOperationTest {

    /**
     * Test of asSingle method.
     *
     * @since 3.0.0
     */
    @Test
    void testAsSingle() {
        var expect = FilteringOperation.Single.EQUAL;

        var result = expect.asSingle();

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of asSingle method if illegal class.
     *
     * @since 3.0.0
     */
    @Test
    void testAsSingleIfIllegalClass() {
        assertThatThrownBy(FilteringOperation.Multi.AND::asSingle).isInstanceOf(ClassCastException.class);
    }

    /**
     * Test of asMulti method.
     *
     * @since 3.0.0
     */
    @Test
    void testAsMulti() {
        var expect = FilteringOperation.Multi.AND;

        var result = expect.asMulti();

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of asMulti method if illegal class.
     *
     * @since 3.0.0
     */
    @Test
    void testAsMultiIfIllegalClass() {
        assertThatThrownBy(FilteringOperation.Single.EQUAL::asMulti).isInstanceOf(ClassCastException.class);
    }

    /**
     * Test of deserialize method if single operation, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfSingle(Jsonb jsonb) {
        JsonString expect = Json.createValue("EQUAL");

        var deserialized = jsonb.fromJson(expect.toString(), FilteringOperation.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonString.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if multi operation, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfMulti(Jsonb jsonb) {
        JsonString expect = Json.createValue("AND");

        var deserialized = jsonb.fromJson(expect.toString(), FilteringOperation.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonString.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if null, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfNull(Jsonb jsonb) {
        var deserialized = jsonb.fromJson("null", FilteringOperation.class);

        assertThat(deserialized).isNull();
    }
}
