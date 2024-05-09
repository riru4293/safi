/*
 * Copyright (c) 2024, Project-K
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

import io.azam.ulidj.ULID;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import static jakarta.json.stream.JsonCollectors.toJsonArray;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.function.Predicate.not;
import java.util.function.Supplier;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.dao.CommonDao;
import jp.mydns.projectk.safi.dao.JobDao;
import jp.mydns.projectk.safi.dao.JobdefDao;
import jp.mydns.projectk.safi.dxo.JobDxo;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.RealTimeService;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import jp.mydns.projectk.safi.util.StreamUtils;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.ContentRecord;
import jp.mydns.projectk.safi.value.Job;
import jp.mydns.projectk.safi.value.JobCreationContext;
import jp.mydns.projectk.safi.value.JsonArrayVo;

/**
 *
 * @author riru
 */
@RequestScoped
public class JobService {

    @Inject
    private RealTimeService realTimeSvc;

    @Inject
    private AppTimeService appTimeSvc;

    @Inject
    private JsonService jsonSvc;

    @Inject
    private JobDao jobDao;

    @Inject
    private JobDxo jobDxo;

    @Inject
    private CommonDao comDao;

    @Inject
    private JobdefDao jobdefDao;

    @Inject
    private Random rand;

    /**
     * Append result messages to Job. Finally call the {@link CommonDao#flushAndClear()}.
     *
     * @param jobId job id
     * @param messages result messages. Ignore {@code null} or blank elements.
     * @throws NullPointerException if any argument is {@code null}
     * @throws JobIOException if no found a Job specified with {@code jobId}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void appendMessages(String jobId, Stream<String> messages) throws JobIOException {
        Objects.requireNonNull(jobId);
        Objects.requireNonNull(messages);

        JobEntity entity = jobDao.lockJob(jobId).orElseThrow(JobIOException.noFoundJob(jobId));

        Stream<JsonValue> existingMsgs = Stream.ofNullable(entity.getMessages()).flatMap(JsonArrayVo::stream);
        Stream<JsonValue> newMsgs = messages.filter(Objects::nonNull).filter(not(String::isBlank)).map(Json::createValue);
        JsonArrayVo combinedMsgs = new JsonArrayVo(Stream.concat(existingMsgs, newMsgs).collect(toJsonArray()));

        entity.setMessages(combinedMsgs);
        comDao.flushAndClear();
    }

    /**
     * Append content records to Job. Finally call the {@link CommonDao#flushAndClear()}.
     *
     * @param jobId job id
     * @param records content records
     * @throws NullPointerException if any argument is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void appendRecords(String jobId, Stream<ContentRecord> records) {
        Objects.requireNonNull(jobId);
        Objects.requireNonNull(records);

        try (var h = StreamUtils.toChunkedStream(records);) {
            h.forEach(chunk -> {
                chunk.stream().map(r -> jobDxo.toRecoedEntity(jobId, r)).forEach(comDao::persist);
                comDao.flushAndClear();
            });
        }
    }

    /**
     * Update job status to {@link JobStatus#SUCCESS}.
     *
     * @param jobId job id
     * @throws NullPointerException if {@code jobId} is {@code null}
     * @throws JobIOException if no found a Job specified with {@code jobId}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void updateToSuccessState(String jobId) throws JobIOException {
        updateJobStatus(Objects.requireNonNull(jobId), JobStatus.SUCCESS);
    }

    /**
     * Update job status to {@link JobStatus#ABORT}.
     *
     * @param jobId job id
     * @throws NullPointerException if {@code jobId} is {@code null}
     * @throws JobIOException if no found a Job specified with {@code jobId}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void updateToAbortState(String jobId) throws JobIOException {
        updateJobStatus(Objects.requireNonNull(jobId), JobStatus.ABORT);
    }

    /**
     * Update job status to {@link JobStatus#FAILURE}.
     *
     * @param jobId job id
     * @throws NullPointerException if {@code jobId} is {@code null}
     * @throws JobIOException if no found a Job specified with {@code jobId}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void updateToFailureState(String jobId) throws JobIOException {
        updateJobStatus(Objects.requireNonNull(jobId), JobStatus.FAILURE);
    }

    private void updateJobStatus(String jobId, JobStatus status) throws JobIOException {
        JobEntity entity = jobDao.lockJob(jobId).orElseThrow(JobIOException.noFoundJob(jobId));
        entity.setStatus(status);
    }

    /**
     * Create a new job as execution schedule. Finally call the {@link CommonDao#flush()}.
     *
     * @param ctx the {@code JobCreationContext}
     * @return new job
     *
     * @throws NullPointerException if {@code jobdefId} is {@code null}
     * @throws JobIOException if not found job definition.
     * @since 1.0.0
     */
    @Transactional
    public Job createSchedule(JobCreationContext ctx) throws JobIOException {
        Objects.requireNonNull(ctx);

        JobdefEntity jobdef = jobdefDao.getJobdef(ctx.getJobdefId())
                .filter(e -> e.getValidityPeriod().isEnabled(appTimeSvc.getLocalNow()))
                .orElseThrow(() -> new JobIOException("Not found job definition."));

        LocalDateTime scheduleTime = ctx.getScheduleTime().map(TimeUtils::toLocalDateTime).orElseGet(realTimeSvc::getLocalNow);
        LocalDateTime limitTime = scheduleTime.plus(ctx.getTimeout().orElseGet(jobdef::getTimeout));

        final JobEntity newJob = new JobEntity();

        newJob.setId(ULID.random(rand));
        newJob.setStatus(JobStatus.SCHEDULE);
        newJob.setScheduleTime(scheduleTime);
        newJob.setLimitTime(limitTime);
        newJob.setKind(jobdef.getJobKind());
        newJob.setContentKind(jobdef.getContentKind());
        newJob.setJobdefId(jobdef.getId());

        ctx.getPlugdef().map(jsonSvc::toJsonObjectVo).ifPresentOrElse(newJob::setPlugdef,
                () -> Optional.ofNullable(jobdef.getPlugdef()).ifPresent(newJob::setPlugdef));

        ctx.getFiltdef().map(jsonSvc::toJsonObjectVo).ifPresentOrElse(newJob::setFiltdef,
                () -> Optional.ofNullable(jobdef.getFiltdef()).ifPresent(newJob::setFiltdef));

        ctx.getTrnsdef().map(jsonSvc::toJsonObjectVo).ifPresentOrElse(newJob::setTrnsdef,
                () -> Optional.ofNullable(jobdef.getTrnsdef()).ifPresent(newJob::setTrnsdef));

        ctx.getOptions().map(jsonSvc::toJsonObjectVo).ifPresentOrElse(newJob::setOptions,
                () -> Optional.ofNullable(jobdef.getOptions()).ifPresent(newJob::setOptions));

        comDao.persist(newJob);
        comDao.flush();

        return jobDxo.toValue(newJob);
    }

    /**
     * Update expired jobs that have not run to the aborted state.
     *
     * @param excludionJobIds running job ids. The specified id is not processed by this function.
     * @throws NullPointerException if {@code jobIds} is {@code null} or contains {@code null}.
     * @since 1.0.0
     */
    @Transactional(TxType.REQUIRES_NEW)
    public void exterminateGhosts(Set<String> excludionJobIds) {
        Set<String> excludions = Set.copyOf(excludionJobIds);

        jobDao.lockExpiredJobs(realTimeSvc.getExactlyLocalNow())
                .filter(p(not(excludions::contains), JobEntity::getId))
                .forEach(e -> {
                    e.setStatus(JobStatus.ABORT);
                    e.setEndTime(realTimeSvc.getLocalNow());
                });
    }

    /**
     * Get a next runnable Job, update it to running state. Finally call the {@link CommonDao#flush()}.
     *
     * When the most nearest schedule satisfies the execution begin conditions, it is the next runnable Job.
     *
     * @return a Job that updated to running state
     * @since 1.0.0
     */
    @Transactional(TxType.REQUIRES_NEW)
    public Optional<Job> nextRunnable() {
        List<JobEntity> actives = jobDao.lockActiveJobs(realTimeSvc.getLocalNow());
        Optional<JobEntity> next = actives.stream().filter(p(JobStatus.SCHEDULE::equals, JobEntity::getStatus)).findFirst();

        return next.flatMap(n -> {
            Predicate<JobEntity> isNext = n::equals;
            Predicate<JobEntity> isConflictToNext = j -> isConflictToRun(j, n);

            return actives.stream().sequential()
                    .filter(isNext.or(isConflictToNext))
                    .findFirst()
                    .filter(isNext)
                    .map(this::toRunningState)
                    .map(jobDxo::toValue);
        });
    }

    boolean isConflictToRun(JobEntity eval, JobEntity criteria) {
        boolean isRunning = eval.getStatus() == JobStatus.RUNNING;
        boolean hasSameDef = Objects.equals(eval.getJobdefId(), criteria.getJobdefId());

        return isExclusiveRun(criteria)
                ? isExclusiveRun(eval) || hasSameDef || isRunning
                : isExclusiveRun(eval) || hasSameDef;
    }

    boolean isExclusiveRun(JobEntity job) {
        return job.getKind() != JobKind.EXPORT;
    }

    JobEntity toRunningState(JobEntity entity) {
        entity.setStatus(JobStatus.RUNNING);
        entity.setBeginTime(realTimeSvc.getLocalNow());

        comDao.flush();

        return entity;
    }

    /**
     * Rebuild job schedules.
     *
     * @param schedulingTerm scheduling term
     * @return new schedules
     */
    @Transactional
    @RequireTenantDb
    public List<JobEntity> rebuildSchedules(Period schedulingTerm) {

        List<JobEntity> exsitingSches
                = jobDao.lockJobs(Set.of(JobStatus.SCHEDULE)).toList();

        LocalDateTime now = realTimeSvc.getNow();

        Predicate<JobEntity> fromNow = s -> !s.getScheduleTime().isBefore(now);

        Predicate<MSchedef> isEnabled = predicate(Range.contain(now), MSchedef::getRange)
                .and(predicate(Range.contain(now), f(MJobdef::getRange).compose(MSchedef::getJobdef)));

        List<MSchedef> entities = schedefDao.getSchedefs()
                .filter(isEnabled).toList();

        List<SchedefValue> schedefs = entities.stream().map(SchedefValue::new).toList();

        Map<String, MJobdef> jobdefs = entities.stream().collect(
                toUnmodifiableMap(MSchedef::getJobdefId, MSchedef::getJobdef));

        var resolver = new ScheduleResolver(now, now.plus(schedulingTerm));

        // Remove old schedules.
        exsitingSches.stream().filter(fromNow).forEach(comDao::remove);
        comDao.flush();

        // Register new schedules.
        Function<Map.Entry<LocalDateTime, SchedefValue>, JobEntity> toScheduleJob
                = e -> toScheduleJob(e.getKey(), e.getValue(),
                        jobdefs.get(e.getValue().getJobdefId()));

        return resolver.resolve(schedefs).stream()
                .map(toScheduleJob).map(comDao::persist).toList();
    }

    private JobEntity toScheduleJob(LocalDateTime t, SchedefValue schedef, MJobdef jobdef) {

        var newJob = new JobEntity();

        newJob.setJobdefId(jobdef.getId());
        newJob.setKind(jobdef.getJobKind());
        newJob.setContentType(jobdef.getContentType());
        newJob.setStatus(JobStatus.SCHEDULE);
        newJob.setScheduleTime(t);
        newJob.setLimitTime(t.plus(jobdef.getTimeout()));
        newJob.setOptions(new JsonObjectVo(JsonValue.EMPTY_JSON_OBJECT));
        newJob.setAssignables(new JsonArrayVo(schedef.getAssignables()));
        newJob.setJobdef(jobdef);
        newJob.setId(generateJobId(jobdef.getId(), t, schedef.getId()));

        return newJob;
    }

    /**
     * Indicates that a job I/O exception has occurred. For example, it doesn't exist, you don't have permission to
     * modify it, and so on.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class JobIOException extends RuntimeException {

        private static final long serialVersionUID = 7853095085270398570L;

        /**
         * Construct with reason that occurred this exception.
         *
         * @param reason reason that occurred. Keep in mind that this may be exposed to users. It must be an abstract
         * message that can briefly describe the issue.
         * @since 1.0.0
         */
        public JobIOException(String reason) {
            super(reason);
        }

        private static Supplier<JobIOException> noFoundJob(String jobId) {
            return () -> new JobIOException(String.format("No Found a specified Job. [%s]", jobId));
        }
    }
}
