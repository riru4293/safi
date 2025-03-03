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
import java.util.List;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class Condition.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
class ConditionTest {

    /**
     * Test of isMulti method.
     *
     * @since 3.0.0
     */
    @Test
    void testIsMulti() {
        var single = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");
        var multi = Condition.multiOf(FilteringOperation.Multi.AND, List.of());

        assertThat(single.isMulti()).isFalse();
        assertThat(multi.isMulti()).isTrue();
    }

    /**
     * Test of getOperation method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetOperation() {
        var single = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");

        assertThat(single).isInstanceOf(Condition.Single.class)
                .returns(FilteringOperation.Single.IS_NULL, Condition::getOperation);
    }

    /**
     * Test of asSingle method.
     *
     * @since 3.0.0
     */
    @Test
    void testAsSingle() {
        var single = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");
        var multi = Condition.multiOf(FilteringOperation.Multi.AND, List.of());

        assertThatCode(() -> single.asSingle()).doesNotThrowAnyException();
        assertThat(single.asSingle()).isInstanceOf(Condition.Single.class);

        assertThatThrownBy(() -> multi.asSingle()).isInstanceOf(ClassCastException.class);
    }

    /**
     * Test of asMulti method.
     *
     * @since 3.0.0
     */
    @Test
    void testAsMulti() {
        var single = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");
        var multi = Condition.multiOf(FilteringOperation.Multi.AND, List.of());

        assertThatCode(() -> multi.asMulti()).doesNotThrowAnyException();
        assertThat(multi.asMulti()).isInstanceOf(Condition.Multi.class);

        assertThatThrownBy(() -> single.asMulti()).isInstanceOf(ClassCastException.class);
    }

    /**
     * Test of singleOf method.
     *
     * @since 3.0.0
     */
    @Test
    void testSingleOf() {
        var single = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");

        assertThat(single).isInstanceOf(Condition.Single.class)
                .returns(FilteringOperation.Single.IS_NULL, Condition::getOperation)
                .returns("name", Condition.Single::getName)
                .returns("value", Condition.Single::getValue);

        assertThatIllegalArgumentException().isThrownBy(() -> Condition.singleOf(FilteringOperation.Multi.OR, "", ""));
    }

    /**
     * Test of multiOf method.
     *
     * @since 3.0.0
     */
    @Test
    void testMultiOf() {
        var single1 = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");
        var single2 = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");

        var multi = Condition.multiOf(FilteringOperation.Multi.AND, List.of(single1, single2));

        assertThat(multi).isInstanceOf(Condition.Multi.class)
                .returns(FilteringOperation.Multi.AND, Condition::getOperation)
                .extracting(Condition.Multi::getChildren).asInstanceOf(InstanceOfAssertFactories.LIST)
                .containsExactly(single1, single2);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> Condition.multiOf(FilteringOperation.Single.IS_NULL, List.of()));
    }

    /**
     * Test of emptyCondition method.
     *
     * @since 3.0.0
     */
    @Test
    void testEmptyCondition() {
        var val = Condition.emptyCondition();

        assertThat(val).isInstanceOf(Condition.Multi.class)
                .returns(FilteringOperation.Multi.AND, Condition::getOperation)
                .returns(true, Condition::isMulti)
                .hasToString("Condition.Empty{}")
                .asInstanceOf(InstanceOfAssertFactories.type(Condition.Multi.class))
                .extracting(Condition.Multi::getChildren).asInstanceOf(InstanceOfAssertFactories.LIST).isEmpty();
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

        var deserialized = jsonb.fromJson(expect.toString(), Condition.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if empty JSON, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfEmptyJson(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder().add("operation", "AND")
                .add("children", JsonValue.EMPTY_JSON_ARRAY).build();

        var deserialized = jsonb.fromJson("{}", Condition.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

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
        var deserialized = jsonb.fromJson("null", Condition.class);

        assertThat(deserialized).isNull();
    }
    
    /**
     * Test of toString method if single.
     *
     * @since 3.0.0
     */
    @Test
    void testToStringIfSingle() {
        String tmpl = "Condition.Single{operation=%s, name=%s, value=%s}";

        var val = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");

        assertThat(val).hasToString(tmpl, "IS_NULL", "name", "value");
    }
    
    /**
     * Test of toString method if multi.
     *
     * @since 3.0.0
     */
    @Test
    void testToStringIfMulti() {
        String tmpl = "Condition.Multi{operation=%s, children=[%s]}";

        var child = Condition.singleOf(FilteringOperation.Single.IS_NULL, "name", "value");

        var val = Condition.multiOf(FilteringOperation.Multi.AND, List.of(child));

        assertThat(val).hasToString(tmpl, "AND", child);
    }
}
