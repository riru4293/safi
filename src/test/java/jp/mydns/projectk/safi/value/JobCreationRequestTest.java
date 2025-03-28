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
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code JobCreationRequest}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
class JobCreationRequestTest {

    /**
     * Test of build method, of class Builder.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testBuild(Validator validator) {
        var scheduleTime = OffsetDateTime.of(2001, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
        var trnsdef = Map.<String, String>of();
        var condition = new LeafConditionValue.Builder(FilteringOperationValue.LeafOperation.IS_NULL)
            .withName("n").withValue("v").build(validator);
        var filtdef = new FiltdefValue.Builder().withTrnsdef(Map.of()).withCondition(condition).build(validator);

        // Build value
        var val = new JobCreationRequest.Builder().with(
            new JobCreationRequest.Builder().withJobdefId("jobdef-id").withScheduleTime(scheduleTime)
                .withTimeout(Duration.ZERO).withPluginName("plg").withTrnsdef(trnsdef).withFiltdef(filtdef)
                .withJobProperties(JsonValue.EMPTY_JSON_OBJECT).build(validator)).build(validator);

        assertThat(val).returns("jobdef-id", JobCreationRequest::getJobdefId)
            .satisfies(v -> assertThat(v.getScheduleTime()).hasValue(scheduleTime))
            .satisfies(v -> assertThat(v.getTimeout()).hasValue(Duration.ZERO))
            .satisfies(v -> assertThat(v.getPluginName()).hasValue("plg"))
            .satisfies(v -> assertThat(v.getTrnsdef()).hasValue(trnsdef))
            .satisfies(v -> assertThat(v.getFiltdef()).hasValue(filtdef))
            .satisfies(v -> assertThat(v.getJobProperties()).hasValue(JsonValue.EMPTY_JSON_OBJECT));
    }

    /**
     * Test of deserialize method, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserialize(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder()
            .add("jobdefId", "jobdef-id")
            .add("scheduleTime", "2000-01-01T12:00:30Z")
            .add("timeout", "PT0S")
            .add("pluginName", "plg")
            .add("trnsdef", JsonValue.EMPTY_JSON_OBJECT)
            .add("filtdef", Json.createObjectBuilder()
                .add("trnsdef", Json.createObjectBuilder().add("k1", "v1"))
                .add("condition", Json.createObjectBuilder()
                    .add("operation", "AND").add("children", JsonValue.EMPTY_JSON_ARRAY)))
            .add("jobProperties", Json.createObjectBuilder().add("p1", "A"))
            .build();

        var deserialized = jsonb.fromJson(expect.toString(), JobCreationRequest.class);

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
        String tmpl = "JobCreationRequest{jobdefId=%s, scheduleTime=%s, timeout=%s, pluginName=%s, trnsdef=%s"
            + ", filtdef=%s, jobProperties=%s}";

        var scheduleTime = OffsetDateTime.of(2000, 1, 1, 12, 33, 55, 0, ZoneOffset.UTC);
        var conditiion = new LeafConditionValue.Builder(FilteringOperationValue.LeafOperation.IS_NULL)
            .withName("n").withValue("v").build(validator);
        var filtdef = new FiltdefValue.Builder().withTrnsdef(Map.of()).withCondition(conditiion).build(validator);

        var val = new JobCreationRequest.Builder().withJobdefId("jobdef-id").withScheduleTime(scheduleTime)
            .withTimeout(Duration.ZERO).withPluginName("plg").withTrnsdef(Map.of()).withFiltdef(filtdef)
            .withJobProperties(JsonValue.EMPTY_JSON_OBJECT)
            .build(validator);

        assertThat(val).hasToString(tmpl, "jobdef-id", scheduleTime, Duration.ZERO, "plg", Map.of(), filtdef,
            JsonValue.EMPTY_JSON_OBJECT, 3);
    }
}
