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
package jp.mydns.projectk.safi.dxo;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.validation.Validator;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.trial.AppTimeService;
import jp.mydns.projectk.safi.service.ValidationService;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of class {@code JobdefDxo}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
@ExtendWith(MockitoExtension.class)
class JobdefDxoTest {

    /**
     * Test of toValue method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @param appTimeSvc it is mock
     * @since 3.0.0
     */
    @Test
    void testToValue(Validator validator, Jsonb jsonb, @Mock AppTimeService appTimeSvc) {
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

        var validationSvc = new ValidationService.Impl(validator, appTimeSvc);
        var jsonSvc = new JsonService.Impl(jsonb);
        var instance = new JobdefDxo.Impl(validationSvc, jsonSvc);

        var val = instance.toValue(expect);
        var entity = instance.toEntity(val);
        var result = jsonSvc.toJsonValue(instance.toValue(entity));

        assertThat(result).isEqualTo(expect);
    }
}
