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
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code JobdefValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
class JobdefValueTest {

    /**
     * Test of build method, of class Builder.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testBuild(Validator validator) {
        var vp = new ValidityPeriodValue.Builder().build(validator);
        var filtdef = new FiltdefValue.Builder().build(validator);
        var regTime = OffsetDateTime.of(2001, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
        var updTime = OffsetDateTime.of(2002, 3, 4, 5, 6, 7, 0, ZoneOffset.UTC);

        // Build value
        var val = new JobdefValue.Builder().withId("jobdef-id").withValidityPeriod(vp)
            .withJobKind(JobKind.REBUILD).withJobTarget(JobTarget.ASSET).withTimeout(Duration.ZERO)
            .withName("jobdef-name").withPluginName("plg").withTrnsdef(Map.of()).withFiltdef(filtdef)
            .withJobProperties(JsonValue.EMPTY_JSON_OBJECT).withNote("note").withVersion(3).withRegisterTime(regTime)
            .withRegisterAccountId("reg-id").withRegisterProcessName("reg-name").withUpdateTime(updTime)
            .withUpdateAccountId("upd-id").withUpdateProcessName("upd-name")
            .build(validator);

        // Rebuild same value
        val = new JobdefValue.Builder().with(val).build(validator);

        assertThat(val).returns("jobdef-id", JobdefValue::getId)
            .returns(vp, JobdefValue::getValidityPeriod)
            .returns(JobKind.REBUILD, JobdefValue::getJobKind)
            .returns(JobTarget.ASSET, JobdefValue::getJobTarget)
            .returns(Duration.ZERO, JobdefValue::getTimeout)
            .satisfies(v -> assertThat(v.getName()).hasValue("jobdef-name"))
            .satisfies(v -> assertThat(v.getPluginName()).hasValue("plg"))
            .satisfies(v -> assertThat(v.getTrnsdef()).hasValue(Map.of()))
            .satisfies(v -> assertThat(v.getFiltdef()).hasValue(filtdef))
            .returns(JsonValue.EMPTY_JSON_OBJECT, JobdefValue::getJobProperties)
            .satisfies(v -> assertThat(v.getNote()).hasValue("note"))
            .returns(3, JobdefValue::getVersion)
            .satisfies(v -> assertThat(v.getRegisterTime()).hasValue(regTime))
            .satisfies(v -> assertThat(v.getRegisterAccountId()).hasValue("reg-id"))
            .satisfies(v -> assertThat(v.getRegisterProcessName()).hasValue("reg-name"))
            .satisfies(v -> assertThat(v.getUpdateTime()).hasValue(updTime))
            .satisfies(v -> assertThat(v.getUpdateAccountId()).hasValue("upd-id"))
            .satisfies(v -> assertThat(v.getUpdateProcessName()).hasValue("upd-name"));
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
            .add("id", "jobdef-id")
            .add("validityPeriod", Json.createObjectBuilder()
                .add("from", "2000-01-01T00:00:00Z")
                .add("to", "2999-12-31T23:59:59Z")
                .add("ignored", false))
            .add("jobKind", "REBUILD")
            .add("jobTarget", "ASSET")
            .add("timeout", "PT0S")
            .add("name", "jobdef-name")
            .add("pluginName", "plg")
            .add("trnsdef", JsonValue.EMPTY_JSON_OBJECT)
            .add("filtdef", Json.createObjectBuilder()
                .add("trnsdef", Json.createObjectBuilder().add("k1", "v1"))
                .add("condition", Json.createObjectBuilder()
                    .add("operation", "AND").add("children", JsonValue.EMPTY_JSON_ARRAY)))
            .add("jobProperties", Json.createObjectBuilder().add("p1", "A"))
            .add("note", "note").add("version", 7)
            .add("registerTime", "2222-12-30T00:12:34Z").add("registerAccountId", "rid")
            .add("registerProcessName", "rnm")
            .add("updateTime", "2134-11-20T10:12:11Z").add("updateAccountId", "uid").add("updateProcessName", "unm")
            .build();

        var deserialized = jsonb.fromJson(expect.toString(), JobdefValue.class);

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
        String tmpl
            = "JobdefValue{id=%s, validityPeriod=%s, jobKind=%s, jobTarget=%s, timeout=%s, name=%s, pluginName=%s"
            + ", trnsdef=%s, filtdef=%s, jobProperties=%s, version=%s}";

        var vp = new ValidityPeriodValue.Builder().build(validator);
        var filtdef = new FiltdefValue.Builder().build(validator);
        var regTime = OffsetDateTime.of(2001, 2, 3, 4, 5, 6, 0, ZoneOffset.UTC);
        var updTime = OffsetDateTime.of(2002, 3, 4, 5, 6, 7, 0, ZoneOffset.UTC);

        var val = new JobdefValue.Builder().withId("jobdef-id").withValidityPeriod(vp).withJobKind(JobKind.REBUILD)
            .withJobTarget(JobTarget.ASSET).withTimeout(Duration.ZERO).withName("jobdef-name").withPluginName("plg")
            .withTrnsdef(Map.of()).withFiltdef(filtdef).withJobProperties(JsonValue.EMPTY_JSON_OBJECT)
            .withNote("note").withVersion(3)
            .withRegisterTime(regTime).withRegisterAccountId("reg-id").withRegisterProcessName("reg-name")
            .withUpdateTime(updTime).withUpdateAccountId("upd-id").withUpdateProcessName("upd-name")
            .build(validator);

        assertThat(val).hasToString(tmpl, "jobdef-id", vp, JobKind.REBUILD, JobTarget.ASSET, Duration.ZERO,
            "jobdef-name", "plg", Map.of(), filtdef, JsonValue.EMPTY_JSON_OBJECT, 3);
    }
}
