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
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code FilteringConditionValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
class FilteringConditionValueTest {

    /**
     * Test of getOperation method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testGetOperation(Validator validator) {

        var filter = new LeafConditionValue.Builder(FilteringOperationValue.LeafOperation.IS_NULL)
            .withName("n").withValue("v").build(validator);

        assertThat(filter).isInstanceOf(LeafConditionValue.class)
            .returns(FilteringOperationValue.LeafOperation.IS_NULL, FilteringConditionValue::getOperation);
    }

    /**
     * Test of deserialize method, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserialize(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder().add("operation", "AND")
            .add("children", Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("operation", "EQUAL").add("name", "n1").add("value", "v1"))
                .add(Json.createObjectBuilder().add("operation", "EQUAL").add("name", "n2").add("value", "v2"))
            ).build();

        var deserialized = jsonb.fromJson(expect.toString(), FilteringConditionValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if wmpty, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfEmpty(Jsonb jsonb) {
        JsonObject expect = JsonValue.EMPTY_JSON_OBJECT;

        var deserialized = jsonb.fromJson(expect.toString(), FilteringConditionValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of toString method if leaf.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testToStringIfLeaf(Validator validator) {
        String tmpl = "FilteringCondition.Leaf{operation=%s, name=%s, value=%s}";

        var val = new LeafConditionValue.Builder(FilteringOperationValue.LeafOperation.IS_NULL)
            .withName("name").withValue("value").build(validator);

        assertThat(val).hasToString(tmpl, "IS_NULL", "name", "value");
    }

    /**
     * Test of toString method if node.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testToStringIfNode(Validator validator) {
        String tmpl = "FilteringCondition.Node{operation=%s, children=[%s]}";

        var child = new LeafConditionValue.Builder(FilteringOperationValue.LeafOperation.IS_NULL)
            .withName("name").withValue("value").build(validator);

        var val = new NodeConditionValue.Builder(FilteringOperationValue.NodeOperation.AND)
            .withChildren(List.of(child)).build(validator);

        assertThat(val).hasToString(tmpl, "AND", child);
    }
}
