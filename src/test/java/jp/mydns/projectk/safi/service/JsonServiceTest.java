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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code JsonService}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
class JsonServiceTest {

    /**
     * Test of toJsonValue method.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testToJsonValue(Jsonb jsonb) {
        var expect = List.of(Json.createValue(1), JsonValue.NULL, Json.createValue("hello"), JsonValue.TRUE);

        var instance = new JsonService.Impl(jsonb);

        var val = new ArrayList<Object>();
        val.add(1);
        val.add(null);
        val.add("hello");
        val.add(true);

        var result = instance.toJsonValue(val);

        assertThat(result).isInstanceOf(JsonArray.class)
            .asInstanceOf(InstanceOfAssertFactories.collection(JsonValue.class))
            .containsExactlyElementsOf(expect);
    }

    /**
     * Test of fromJsonValue method.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testFromJsonValue(Jsonb jsonb) {
        var expect = new ArrayList<Object>();
        expect.add(BigDecimal.valueOf(1));
        expect.add(null);
        expect.add("hello");
        expect.add(true);

        var instance = new JsonService.Impl(jsonb);

        var val = Json.createArrayBuilder().add(Json.createValue(1)).add(JsonValue.NULL).add(Json.createValue("hello"))
            .add(JsonValue.TRUE).build();

        @SuppressWarnings("unchecked")
        var result = (List<Object>) instance.fromJsonValue(val, List.class);

        assertThat(result).containsExactlyElementsOf(expect);
    }

    /**
     * Test of merge method.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testMerge(Jsonb jsonb) {
        var expect = Map.of("k1", Json.createValue("foo"), "k2", Json.createValue("var"), "k3", Json.createValue("baz"));

        var base = Json.createObjectBuilder().add("k1", "foo").add("k2", "foo").build();
        var ow = Json.createObjectBuilder().add("k2", "var").add("k3", "baz").build();

        var instance = new JsonService.Impl(jsonb);

        var result = instance.merge(base, ow);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(expect);
    }
}
