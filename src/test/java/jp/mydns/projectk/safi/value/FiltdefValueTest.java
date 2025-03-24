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
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Map;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code FiltdefValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
class FiltdefValueTest {

    /**
     * Test of deserialize method, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserialize(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder()
            .add("trnsdef", Json.createObjectBuilder().add("k1", "v1").add("k2", "v2").add("k3", "v3"))
            .add("condition", Json.createObjectBuilder()
                .add("operation", "AND").add("children", JsonValue.EMPTY_JSON_ARRAY)).build();

        var deserialized = jsonb.fromJson(expect.toString(), FiltdefValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of toString method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testToString(Validator validator) {
        String tmpl = "FiltdefValue{trnsdef=%s, condition=%s}";

        var filter = FilteringConditionValue.multiOf(FilteringOperationValue.NodeOperation.NOT_OR, List.of());

        var val = new FiltdefValue.Builder().withTrnsdef(Map.of()).withFilter(filter).build(validator);

        assertThat(val).hasToString(tmpl, Map.of(), filter);
    }
}
