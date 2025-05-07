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
import java.time.OffsetDateTime;
import static java.time.ZoneOffset.UTC;
import java.util.List;
import java.util.Set;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of class {@code JobValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(MockitoExtension.class)
class JobValueTest {

    /**
     * Test of build method, of class Builder.
     *
     * @param validator the {@code Validator}. It provides by Mockito.
     * @since 3.0.0
     */
    @Test
    void testBuild(@Mock Validator validator) {

        var jobdef = mock(JobdefValue.class);
        var schedef = mock(SchedefValue.class);

        var scheduleTime = OffsetDateTime.of(2001, 1, 1, 1, 1, 1, 0, UTC);
        var limitTime = OffsetDateTime.of(2002, 2, 2, 2, 2, 2, 0, UTC);
        var beginTime = OffsetDateTime.of(2003, 3, 3, 3, 3, 3, 0, UTC);
        var endTime = OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, UTC);
        var regTime = OffsetDateTime.of(2099, 9, 9, 9, 9, 9, 0, UTC);
        var updTime = OffsetDateTime.of(2088, 8, 8, 8, 8, 8, 0, UTC);

        doReturn(Set.of()).when(validator).validate(any());

        // Build value
        var val = new JobValue.Builder().withId("job-id").withStatus(JobStatus.SUCCESS)
            .withKind(JobKind.EXPORT).withTarget(JobTarget.GRP).withScheduleTime(scheduleTime)
            .withLimitTime(limitTime).withBeginTime(beginTime).withEndTime(endTime).withProperties(JsonValue.EMPTY_JSON_OBJECT)
            .withJobdefId("jobdef-id").withJobdef(jobdef).withSchedefId("schedef-id").withSchedef(schedef)
            .withResultMessages(List.of()).withNote("note").withVersion(2).withRegisterTime(regTime)
            .withRegisterAccountId("reg-id").withRegisterProcessName("reg-name").withUpdateTime(updTime)
            .withUpdateAccountId("upd-id").withUpdateProcessName("upd-name").build(validator);

        // Rebuild same value
        val = new JobValue.Builder().with(val).build(validator);

        assertThat(val).describedAs("Verify properties of the job value that built.")
            .returns("job-id", JobValue::getId)
            .returns(JobStatus.SUCCESS, JobValue::getStatus)
            .returns(JobKind.EXPORT, JobValue::getKind)
            .returns(JobTarget.GRP, JobValue::getTarget)
            .returns(OffsetDateTime.of(2001, 1, 1, 1, 1, 1, 0, UTC), JobValue::getScheduleTime)
            .returns(OffsetDateTime.of(2002, 2, 2, 2, 2, 2, 0, UTC), JobValue::getLimitTime)
            .satisfies(v -> assertThat(v.getBeginTime()).hasValue(OffsetDateTime.of(2003, 3, 3, 3, 3, 3, 0, UTC)))
            .satisfies(v -> assertThat(v.getEndTime()).hasValue(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, UTC)))
            .returns(JsonValue.EMPTY_JSON_OBJECT, JobValue::getProperties)
            .returns("jobdef-id", JobValue::getJobdefId)
            .returns(jobdef, JobValue::getJobdef)
            .satisfies(v -> assertThat(v.getSchedefId()).hasValue("schedef-id"))
            .satisfies(v -> assertThat(v.getSchedef()).hasValue(schedef))
            .satisfies(v -> assertThat(v.getResultMessages()).hasValue(List.of()))
            .satisfies(v -> assertThat(v.getNote()).hasValue("note"))
            .returns(2, JobValue::getVersion)
            .satisfies(v -> assertThat(v.getRegisterTime())
            .hasValue(OffsetDateTime.of(2099, 9, 9, 9, 9, 9, 0, UTC)))
            .satisfies(v -> assertThat(v.getRegisterAccountId()).hasValue("reg-id"))
            .satisfies(v -> assertThat(v.getRegisterProcessName()).hasValue("reg-name"))
            .satisfies(v -> assertThat(v.getUpdateTime())
            .hasValue(OffsetDateTime.of(2088, 8, 8, 8, 8, 8, 0, UTC)))
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
            .add("id", "job-id")
            .add("status", "FAILURE")
            .add("kind", "REBUILD")
            .add("target", "ASSET")
            .add("scheduleTime", "2001-01-01T01:01:01Z")
            .add("limitTime", "2002-02-02T02:02:02Z")
            .add("beginTime", "2003-03-03T03:03:03Z")
            .add("endTime", "2004-04-04T04:04:04Z")
            .add("properties", Json.createObjectBuilder().add("p1", "A"))
            .add("jobdefId", "jobdef-id")
            .add("jobdef", Json.createObjectBuilder()
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
                .add("updateTime", "2134-11-20T10:12:11Z").add("updateAccountId", "uid").add("updateProcessName", "unm"))
            .add("schedefId", "schedef-id")
            .add("schedef", Json.createObjectBuilder()
                .add("id", "schedef-id")
                .add("jobdefId", "jobdef-id")
                .add("validityPeriod", Json.createObjectBuilder()
                    .add("from", "2000-01-01T00:00:00Z")
                    .add("to", "2999-12-31T23:59:59Z")
                    .add("ignored", false))
                .add("priority", "A")
                .add("name", "jobdef-name")
                .add("trigger", Json.createObjectBuilder()
                    .add("kind", "ONCE")
                    .add("anchorTime", "2009-12-30T00:00:01Z"))
                .add("note", "note").add("version", 7)
                .add("registerTime", "2222-12-30T00:12:34Z").add("registerAccountId", "rid")
                .add("registerProcessName", "rnm")
                .add("updateTime", "2134-11-20T10:12:11Z").add("updateAccountId", "uid").add("updateProcessName", "unm"))
            .add("resultMessages", Json.createArrayBuilder().add("m1").add("m2"))
            .add("note", "note").add("version", 7)
            .add("registerTime", "2222-12-30T00:12:34Z").add("registerAccountId", "rid")
            .add("registerProcessName", "rnm")
            .add("updateTime", "2134-11-20T10:12:11Z").add("updateAccountId", "uid").add("updateProcessName", "unm")
            .build();

        var deserialized = jsonb.fromJson(expect.toString(), JobValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).describedAs("Verify equals job as object and job as JSON.").isEqualTo(expect);
    }

    /**
     * Test of toString method.
     *
     * @param validator the {@code Validator}. It provides by Mockito.
     * @since 3.0.0
     */
    @Test
    void testToString(@Mock Validator validator) {

        var jobdef = mock(JobdefValue.class);
        var schedef = mock(SchedefValue.class);

        doReturn("jobdef").when(jobdef).toString();
        doReturn("schedef").when(schedef).toString();

        String tmpl
            = "JobValue{id=%s, status=%s, kind=%s, target=%s, scheduleTime=%s, limitTime=%s, beginTime=%s"
            + ", endTime=%s, properties=%s, jobdefId=%s, jobdef=%s, schedefId=%s, schedef=%s, resultMessages=%s, version=%s}";

        var val = new JobValue.Builder().withId("job-id").withStatus(JobStatus.RUNNING)
            .withKind(JobKind.EXPORT).withTarget(JobTarget.PER_USER)
            .withScheduleTime(OffsetDateTime.of(2001, 1, 1, 1, 1, 1, 0, UTC))
            .withLimitTime(OffsetDateTime.of(2002, 2, 2, 2, 2, 2, 0, UTC))
            .withBeginTime(OffsetDateTime.of(2003, 3, 3, 3, 3, 3, 0, UTC))
            .withEndTime(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, UTC))
            .withProperties(JsonValue.EMPTY_JSON_OBJECT)
            .withJobdefId("jobdef-id").withJobdef(jobdef).withSchedefId("schedef-id").withSchedef(schedef)
            .withResultMessages(List.of("m", "s", "g")).withVersion(7)
            .build(validator);

        assertThat(val).hasToString(tmpl, "job-id", JobStatus.RUNNING, JobKind.EXPORT, JobTarget.PER_USER,
            OffsetDateTime.of(2001, 1, 1, 1, 1, 1, 0, UTC), OffsetDateTime.of(2002, 2, 2, 2, 2, 2, 0, UTC),
            OffsetDateTime.of(2003, 3, 3, 3, 3, 3, 0, UTC), OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, UTC),
            JsonValue.EMPTY_JSON_OBJECT, "jobdef-id", "jobdef", "schedef-id", "schedef", List.of("m", "s", "g"), 7);
    }
}
