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

import jakarta.json.JsonValue;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import static java.time.ZoneOffset.UTC;
import java.util.List;
import java.util.function.Function;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.value.JobCreationContext;
import jp.mydns.projectk.safi.value.JobValue;
import jp.mydns.projectk.safi.value.JobdefValue;
import jp.mydns.projectk.safi.value.SJson;
import jp.mydns.projectk.safi.value.SchedefValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 Test of class {@code JobDxo}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(JsonbParameterResolver.class)
class JobDxoTest {

/**
 Test of default constructor is not supported.

 @since 3.0.0
 */
@Test
void testDefaultConstructor() {

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(JobDxo.Impl::new);
}

/**
 Test of newEntity method.

 @param jsonSvc the {@code JsonService}. It provides by Mockito.
 @since 3.0.0
 */
@Test
void testNewEntity(@Mock JsonService jsonSvc) {

    var jobdef = mock(JobdefValue.class);

    var jobdefJson = mock(SJson.class);
    var propsJson = mock(SJson.class);

    // Limit is 2003-03-03T03:03:03
    doReturn(Duration.ofDays(365).plusDays(29).plusHours(1).plusMinutes(1).plusSeconds(1)).when(
        jobdef).getTimeout();

    doReturn("jobdef-id").when(jobdef).getId();
    doReturn(JobKind.IMPORT).when(jobdef).getJobKind();
    doReturn(JobTarget.USER).when(jobdef).getJobTarget();
    doReturn(JsonValue.EMPTY_JSON_OBJECT).when(jobdef).getJobProperties();

    // Conversion job definition
    doReturn(jobdefJson).when(jsonSvc).toSJson(jobdef);

    // Conversion properties
    doReturn(propsJson).when(jsonSvc).toSJson(JsonValue.EMPTY_JSON_OBJECT);

    var ctx = new JobCreationContext("job-id", OffsetDateTime.of(2002, 2, 2, 2, 2, 2, 0, UTC), jobdef);

    var instance = new JobDxo.Impl(jsonSvc);

    var result = instance.newEntity(ctx);

    assertThat(result).describedAs("Verify properties of the job entity that created new.")
        .returns("job-id", JobEntity::getId)
        .returns(JobStatus.SCHEDULE, JobEntity::getStatus)
        .returns(JobKind.IMPORT, JobEntity::getKind)
        .returns(JobTarget.USER, JobEntity::getTarget)
        .returns(LocalDateTime.of(2002, 2, 2, 2, 2, 2), JobEntity::getScheduleTime)
        .returns(LocalDateTime.of(2003, 3, 3, 3, 3, 3), JobEntity::getLimitTime)
        .returns(null, JobEntity::getBeginTime)
        .returns(null, JobEntity::getEndTime)
        .returns(propsJson, JobEntity::getProperties)
        .returns("jobdef-id", JobEntity::getJobdefId)
        .returns(jobdefJson, JobEntity::getJobdef)
        .returns(null, JobEntity::getSchedefId)
        .returns(null, JobEntity::getSchedef)
        .returns(null, JobEntity::getResultMessages)
        .returns(null, JobEntity::getNote)
        .returns(0, JobEntity::getVersion)
        .returns(null, JobEntity::getRegTime)
        .returns(null, JobEntity::getRegId)
        .returns(null, JobEntity::getRegName)
        .returns(null, JobEntity::getUpdTime)
        .returns(null, JobEntity::getUpdId)
        .returns(null, JobEntity::getUpdName);
}

/**
 Test of toEntity method.

 @param jsonSvc the {@code JsonService}. It provides by Mockito.
 @since 3.0.0
 */
@Test
void testToEntity(@Mock JsonService jsonSvc) {

    var jobdef = mock(JobdefValue.class);
    var schedef = mock(SchedefValue.class);

    var jobdefJson = mock(SJson.class);
    var schedefJson = mock(SJson.class);
    var propsJson = mock(SJson.class);
    var msgsJson = mock(SJson.class);

    // Conversion job definition
    doReturn(jobdefJson).when(jsonSvc).toSJson(jobdef);

    // Conversion schedule definition
    doReturn(schedefJson).when(jsonSvc).toSJson(schedef);

    // Conversion properties
    doReturn(propsJson).when(jsonSvc).toSJson(JsonValue.EMPTY_JSON_OBJECT);

    // Conversion result messages
    doReturn(msgsJson).when(jsonSvc).toSJson(any(List.class));

    var value = new JobValue.Builder()
        .withId("job-id")
        .withStatus(JobStatus.SUCCESS)
        .withKind(JobKind.REBUILD)
        .withTarget(JobTarget.ASSET)
        .withScheduleTime(OffsetDateTime.of(2001, 1, 1, 1, 1, 1, 0, UTC))
        .withLimitTime(OffsetDateTime.of(2002, 2, 2, 2, 2, 2, 0, UTC))
        .withBeginTime(OffsetDateTime.of(2003, 3, 3, 3, 3, 3, 0, UTC))
        .withEndTime(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, UTC))
        .withProperties(JsonValue.EMPTY_JSON_OBJECT)
        .withJobdefId("jobdef-id")
        .withJobdef(jobdef)
        .withSchedefId("schedef-id")
        .withSchedef(schedef)
        .withResultMessages(List.of())
        .withNote("note")
        .withVersion(3)
        .withRegisterTime(OffsetDateTime.of(2099, 9, 9, 9, 9, 9, 0, UTC))
        .withRegisterAccountId("reg-id")
        .withRegisterProcessName("reg-name")
        .withUpdateTime(OffsetDateTime.of(2088, 8, 8, 8, 8, 8, 0, UTC))
        .withUpdateAccountId("upd-id")
        .withUpdateProcessName("upd-name")
        .unsafeBuild();

    var instance = new JobDxo.Impl(jsonSvc);

    var result = instance.toEntity(value);

    assertThat(result).describedAs(
        "Verify properties of the job entity that exchange form the job value.")
        .returns("job-id", JobEntity::getId)
        .returns(JobStatus.SUCCESS, JobEntity::getStatus)
        .returns(JobKind.REBUILD, JobEntity::getKind)
        .returns(JobTarget.ASSET, JobEntity::getTarget)
        .returns(LocalDateTime.of(2001, 1, 1, 1, 1, 1), JobEntity::getScheduleTime)
        .returns(LocalDateTime.of(2002, 2, 2, 2, 2, 2), JobEntity::getLimitTime)
        .returns(LocalDateTime.of(2003, 3, 3, 3, 3, 3), JobEntity::getBeginTime)
        .returns(LocalDateTime.of(2004, 4, 4, 4, 4, 4), JobEntity::getEndTime)
        .returns(propsJson, JobEntity::getProperties)
        .returns("jobdef-id", JobEntity::getJobdefId)
        .returns(jobdefJson, JobEntity::getJobdef)
        .returns("schedef-id", JobEntity::getSchedefId)
        .returns(schedefJson, JobEntity::getSchedef)
        .returns(msgsJson, JobEntity::getResultMessages)
        .returns("note", JobEntity::getNote)
        .returns(3, JobEntity::getVersion)
        .returns(LocalDateTime.of(2099, 9, 9, 9, 9, 9), JobEntity::getRegTime)
        .returns("reg-id", JobEntity::getRegId)
        .returns("reg-name", JobEntity::getRegName)
        .returns(LocalDateTime.of(2088, 8, 8, 8, 8, 8), JobEntity::getUpdTime)
        .returns("upd-id", JobEntity::getUpdId)
        .returns("upd-name", JobEntity::getUpdName);
}

/**
 Test of toValue method.

 @param jsonSvc the {@code JsonService}. It provides by Mockito.
 @since 3.0.0
 */
@Test
void testToValue(@Mock JsonService jsonSvc) {

    var jobdefJson = mock(SJson.class);
    var schedefJson = mock(SJson.class);
    var propsJson = mock(SJson.class);
    var msgsJson = mock(SJson.class);

    var jobdef = mock(JobdefValue.class);
    var schedef = mock(SchedefValue.class);

    // Conversion job definition
    doReturn(jobdef).when(jsonSvc).fromJsonValue(any(), eq(JobdefValue.class));

    // Conversion schedule definition
    doReturn(JsonValue.EMPTY_JSON_OBJECT).when(schedefJson).unwrap();
    doReturn((Function<JsonValue, SchedefValue>) z -> schedef).when(jsonSvc).fromJsonValue(
        SchedefValue.class);

    // Conversion properties
    doReturn(JsonValue.EMPTY_JSON_OBJECT).when(propsJson).unwrap();

    // Conversion result messages
    doReturn(JsonValue.EMPTY_JSON_ARRAY).when(msgsJson).unwrap();

    var entity = new JobEntity();
    entity.setId("job-id");
    entity.setStatus(JobStatus.SUCCESS);
    entity.setKind(JobKind.REBUILD);
    entity.setTarget(JobTarget.ASSET);
    entity.setScheduleTime(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
    entity.setLimitTime(LocalDateTime.of(2002, 2, 2, 2, 2, 2));
    entity.setBeginTime(LocalDateTime.of(2003, 3, 3, 3, 3, 3));
    entity.setEndTime(LocalDateTime.of(2004, 4, 4, 4, 4, 4));
    entity.setProperties(propsJson);
    entity.setJobdefId("jobdef-id");
    entity.setJobdef(jobdefJson);
    entity.setSchedefId("schedef-id");
    entity.setSchedef(schedefJson);
    entity.setResultMessages(msgsJson);
    entity.setNote("note");
    entity.setRegTime(LocalDateTime.of(2099, 9, 9, 9, 9, 9));
    entity.setRegId("reg-id");
    entity.setRegName("reg-name");
    entity.setUpdTime(LocalDateTime.of(2088, 8, 8, 8, 8, 8));
    entity.setUpdId("upd-id");
    entity.setUpdName("upd-name");

    var instance = new JobDxo.Impl(jsonSvc);

    var result = instance.toValue(entity);

    assertThat(result).describedAs(
        "Verify properties of the job value that exchange form the job entity.")
        .returns("job-id", JobValue::getId)
        .returns(JobStatus.SUCCESS, JobValue::getStatus)
        .returns(JobKind.REBUILD, JobValue::getKind)
        .returns(JobTarget.ASSET, JobValue::getTarget)
        .returns(OffsetDateTime.of(2001, 1, 1, 1, 1, 1, 0, UTC), JobValue::getScheduleTime)
        .returns(OffsetDateTime.of(2002, 2, 2, 2, 2, 2, 0, UTC), JobValue::getLimitTime)
        .satisfies(v -> assertThat(v.getBeginTime()).hasValue(OffsetDateTime.of(2003, 3, 3, 3, 3,
            3, 0, UTC)))
        .satisfies(v -> assertThat(v.getEndTime()).hasValue(OffsetDateTime.of(2004, 4, 4, 4, 4, 4,
            0, UTC)))
        .returns(JsonValue.EMPTY_JSON_OBJECT, JobValue::getProperties)
        .returns("jobdef-id", JobValue::getJobdefId)
        .returns(jobdef, JobValue::getJobdef)
        .satisfies(v -> assertThat(v.getSchedefId()).hasValue("schedef-id"))
        .satisfies(v -> assertThat(v.getSchedef()).hasValue(schedef))
        .satisfies(v -> assertThat(v.getResultMessages()).hasValue(List.of()))
        .satisfies(v -> assertThat(v.getNote()).hasValue("note"))
        .returns(0, JobValue::getVersion)
        .satisfies(v -> assertThat(v.getRegisterTime())
            .hasValue(OffsetDateTime.of(2099, 9, 9, 9, 9, 9, 0, UTC)))
        .satisfies(v -> assertThat(v.getRegisterAccountId()).hasValue("reg-id"))
        .satisfies(v -> assertThat(v.getRegisterProcessName()).hasValue("reg-name"))
        .satisfies(v -> assertThat(v.getUpdateTime())
            .hasValue(OffsetDateTime.of(2088, 8, 8, 8, 8, 8, 0, UTC)))
        .satisfies(v -> assertThat(v.getUpdateAccountId()).hasValue("upd-id"))
        .satisfies(v -> assertThat(v.getUpdateProcessName()).hasValue("upd-name"));
}

}
