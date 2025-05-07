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
package jp.mydns.projectk.safi.it;

import jakarta.enterprise.context.RequestScoped;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.dao.CommonDao;
import jp.mydns.projectk.safi.dao.JobDao;
import jp.mydns.projectk.safi.dao.JobdefDao;
import jp.mydns.projectk.safi.dxo.JobDxo;
import jp.mydns.projectk.safi.dxo.JobdefDxo;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.producer.JsonbProducer;
import jp.mydns.projectk.safi.resource.JobResource;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.service.IdService;
import jp.mydns.projectk.safi.service.JobService;
import jp.mydns.projectk.safi.service.JobdefService;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.RealTimeService;
import jp.mydns.projectk.safi.service.ValidationService;
import jp.mydns.projectk.safi.test.AppTimeServiceProvider;
import jp.mydns.projectk.safi.test.EntityFooterContextProducer;
import jp.mydns.projectk.safi.test.EntityManagerProducer;
import jp.mydns.projectk.safi.test.JndiServer;
import jp.mydns.projectk.safi.test.RealTimeServiceProvider;
import jp.mydns.projectk.safi.test.RequestContextProvider;
import jp.mydns.projectk.safi.test.ValidatorProducer;
import jp.mydns.projectk.safi.test.Values;
import jp.mydns.projectk.safi.value.FiltdefValue;
import jp.mydns.projectk.safi.value.FilteringOperationValue;
import jp.mydns.projectk.safi.value.JobCreationRequest;
import jp.mydns.projectk.safi.value.JobValue;
import jp.mydns.projectk.safi.value.JobdefValue;
import jp.mydns.projectk.safi.value.LeafConditionValue;
import jp.mydns.projectk.safi.value.RequestContext;
import jp.mydns.projectk.safi.value.SJson;
import static org.assertj.core.api.Assertions.assertThat;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 Test job processing.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@EnableWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JobIT {

private final AppTimeServiceProvider appTimeSvcProvider = new AppTimeServiceProvider();
private final RealTimeServiceProvider realTimeSvcProvider = new RealTimeServiceProvider();
private final RequestContextProvider reqCtxProvider = new RequestContextProvider();

@WeldSetup
WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
    .alternatives(
        EntityManagerProducer.class,
        EntityFooterContextProducer.class,
        ValidatorProducer.class
    )
    .beanClasses(
        JndiServer.class,
        EntityManagerProducer.class,
        EntityFooterContextProducer.class,
        ValidatorProducer.class,
        JsonbProducer.Impl.class,
        CommonDao.Impl.class,
        JobdefDao.Impl.class,
        JobDao.Impl.class,
        JobdefDxo.Impl.class,
        JobDxo.Impl.class,
        ValidationService.Impl.class,
        JsonService.Impl.class,
        IdService.Impl.class,
        JobdefService.Impl.class,
        JobService.Impl.class,
        JobResource.Impl.class
    )
).addBeans(
    reqCtxProvider.getBean(),
    appTimeSvcProvider.getBean(),
    realTimeSvcProvider.getBean()
).activate(RequestScoped.class).build();

@BeforeAll
@SuppressWarnings("unused")
void init(JndiServer jndiSrv, EntityManager em) {
    jndiSrv.bindBeanManager(weld.getBeanManager());
    registerJobdef(em);
}

private void registerJobdef(EntityManager em) {
    var entity = new JobdefEntity();

    entity.setId("jobdef-id");
    entity.setValidityPeriod(Values.defaultValidityPeriodEmb());
    entity.setJobKind(JobKind.REBUILD);
    entity.setJobTarget(JobTarget.ASSET);
    entity.setTimeout(Duration.ofHours(3));
    entity.setJobProperties(SJson.of(JsonValue.EMPTY_JSON_OBJECT));

    em.getTransaction().begin();
    em.persist(entity);
    em.getTransaction().commit();
}

/**
 Test create Job, if minimal request.

 @param appTimeSvc the {@code AppTimeService}. This parameter resolved by CDI.
 @param jobRsc the {@code JobResource}. This parameter resolved by CDI.
 @param em the {@code EntityManager}. This parameter resolved by CDI.
 @param reqCtx the {@code RequestContext}. This parameter resolved by CDI.
 @param realTimeSvc the {@code RealTimeService}. This parameter resolved by CDI.
 @since 3.0.0
 */
@Test
void testCreateJobMinimum(AppTimeService appTimeSvc, JobResource jobRsc, EntityManager em,
    RequestContext reqCtx, RealTimeService realTimeSvc) {

    // Current app-now is 2004-04-04T04:04:04Z
    doReturn(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, ZoneOffset.UTC)).when(appTimeSvc).getOffsetNow();
    doReturn(LocalDateTime.of(2004, 4, 4, 4, 4, 4)).when(appTimeSvc).getLocalNow();

    doReturn(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, ZoneOffset.UTC)).when(realTimeSvc).getOffsetNow();
    doReturn(LocalDateTime.of(2004, 4, 4, 4, 4, 4)).when(realTimeSvc).getLocalNow();

    // Resource URI
    ArgumentCaptor<String> jobIdCaptor = ArgumentCaptor.forClass(String.class);
    var apiBaseUri = spy(URI.create("http://hostname/api/"));
    doReturn(apiBaseUri).when(reqCtx).getRestApiPath();

    // Job creation request(Minimum)
    var req = new JobCreationRequest.Builder().withJobdefId("jobdef-id").unsafeBuild();

    // Create a Job
    em.getTransaction().begin();
    var result = jobRsc.createJob(req);
    em.getTransaction().commit();

    // Get job id
    verify(apiBaseUri).resolve(jobIdCaptor.capture());
    String jobId = jobIdCaptor.getValue();

    // Expect values
    URI expectUri = URI.create("http://hostname/api/%s".formatted(jobId));

    JobValue expectValue = new JobValue.Builder().withId(jobId)
        .withStatus(JobStatus.SCHEDULE).withKind(JobKind.REBUILD).withTarget(JobTarget.ASSET)
        .withScheduleTime(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, ZoneOffset.UTC))
        .withLimitTime(OffsetDateTime.of(2004, 4, 4, 7, 4, 4, 0, ZoneOffset.UTC))
        .withProperties(JsonValue.EMPTY_JSON_OBJECT)
        .withJobdefId("jobdef-id")
        .withJobdef(new JobdefValue.Builder().withId("jobdef-id")
            .withValidityPeriod(Values.defaultValidityPeriodValue())
            .withJobKind(JobKind.REBUILD).withJobTarget(JobTarget.ASSET).withTimeout(
            Duration.ofHours(3))
            .withJobProperties(JsonValue.EMPTY_JSON_OBJECT)
            .withVersion(1)
            .withRegisterTime(OffsetDateTime.of(2000, 4, 27, 20, 34, 56, 0, ZoneOffset.UTC))
            .withRegisterAccountId("accountId").withRegisterProcessName("processName").unsafeBuild())
        .withVersion(1)
        .withRegisterTime(OffsetDateTime.of(2000, 4, 27, 20, 34, 56, 0, ZoneOffset.UTC))
        .withRegisterAccountId("accountId").withRegisterProcessName("processName").unsafeBuild();

    // Verify results
    assertThat(result).describedAs("Verify response of create Job.")
        .returns(expectUri, Response::getLocation)
        .extracting(Response::getEntity).usingRecursiveComparison().isEqualTo(expectValue);
}

/**
 Test create Job, if maximum request.

 @param appTimeSvc the {@code AppTimeService}. This parameter resolved by CDI.
 @param jobRsc the {@code JobResource}. This parameter resolved by CDI.
 @param em the {@code EntityManager}. This parameter resolved by CDI.
 @param reqCtx the {@code RequestContext}. This parameter resolved by CDI.
 @param realTimeSvc the {@code RealTimeService}. This parameter resolved by CDI.
 @since 3.0.0
 */
@Test
void testCreateJobMaximum(AppTimeService appTimeSvc, JobResource jobRsc, EntityManager em,
    RequestContext reqCtx, RealTimeService realTimeSvc) {

    // Current app-now is 2004-04-04T04:04:04Z
    doReturn(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, ZoneOffset.UTC)).when(appTimeSvc).getOffsetNow();
    doReturn(LocalDateTime.of(2004, 4, 4, 4, 4, 4)).when(appTimeSvc).getLocalNow();

    doReturn(OffsetDateTime.of(2004, 4, 4, 4, 4, 4, 0, ZoneOffset.UTC)).when(realTimeSvc).getOffsetNow();
    doReturn(LocalDateTime.of(2004, 4, 4, 4, 4, 4)).when(realTimeSvc).getLocalNow();

    // Resource URI
    ArgumentCaptor<String> jobIdCaptor = ArgumentCaptor.forClass(String.class);
    var apiBaseUri = spy(URI.create("http://hostname/api/"));
    doReturn(apiBaseUri).when(reqCtx).getRestApiPath();

    // Filtering definition
    var filter = new LeafConditionValue.Builder(FilteringOperationValue.LeafOperation.IS_NULL)
        .withName("n").withValue("v").unsafeBuild();
    FiltdefValue filtdef = new FiltdefValue.Builder().withCondition(filter).withTrnsdef(Map.of()).unsafeBuild();

    // Job creation request(Maximum)
    var req = new JobCreationRequest.Builder().withJobdefId("jobdef-id").withPluginName("PluginName")
        .withJobProperties(Json.createObjectBuilder().add("k", true).build())
        .withTimeout(Duration.ZERO)
        .withScheduleTime(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .withFiltdef(filtdef).withTrnsdef(Map.of("p", "f"))
        .unsafeBuild();

    // Create a Job
    em.getTransaction().begin();
    var result = jobRsc.createJob(req);
    em.getTransaction().commit();

    // Get job id
    verify(apiBaseUri).resolve(jobIdCaptor.capture());
    String jobId = jobIdCaptor.getValue();

    // Expect values
    URI expectUri = URI.create("http://hostname/api/%s".formatted(jobId));

    JobValue expectValue = new JobValue.Builder().withId(jobId)
        .withStatus(JobStatus.SCHEDULE).withKind(JobKind.REBUILD).withTarget(JobTarget.ASSET)
        .withScheduleTime(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .withLimitTime(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        .withProperties(Json.createObjectBuilder().add("k", true).build())
        .withJobdefId("jobdef-id")
        .withJobdef(new JobdefValue.Builder().withId("jobdef-id")
            .withValidityPeriod(Values.defaultValidityPeriodValue())
            .withJobKind(JobKind.REBUILD).withJobTarget(JobTarget.ASSET).withTimeout(Duration.ZERO)
            .withPluginName("PluginName")
            .withTrnsdef(Map.of("p", "f"))
            .withFiltdef(filtdef)
            .withJobProperties(Json.createObjectBuilder().add("k", true).build())
            .withVersion(1)
            .withRegisterTime(OffsetDateTime.of(2000, 4, 27, 20, 34, 56, 0, ZoneOffset.UTC))
            .withRegisterAccountId("accountId").withRegisterProcessName("processName").unsafeBuild())
        .withVersion(1)
        .withRegisterTime(OffsetDateTime.of(2000, 4, 27, 20, 34, 56, 0, ZoneOffset.UTC))
        .withRegisterAccountId("accountId").withRegisterProcessName("processName").unsafeBuild();

    // Verify results
    assertThat(result).describedAs("Verify response of create Job.")
        .returns(expectUri, Response::getLocation)
        .extracting(Response::getEntity).usingRecursiveComparison().isEqualTo(expectValue);
}

}
