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

import jakarta.batch.operations.JobOperator;
import jakarta.batch.operations.NoSuchJobExecutionException;
import jakarta.batch.runtime.JobExecution;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toUnmodifiableSet;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobStatus;
import static jp.mydns.projectk.safi.util.LambdaUtils.f;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.Job;
import jp.mydns.projectk.safi.batch.JobBatchlet;

/**
 * Utilities for <i>Jakarta-Batch</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class BatchService {

    @Inject
    private JobOperator operator;

    @Inject
    private Jsonb jsonb;

    /**
     * Collect id of the {@link Job} from all the <i>Jakarta-Batch</i> executions. Anything other than those running as
     * {@code Job} will be ignored.
     *
     * @return running job ids
     * @since 1.0.0
     */
    public Set<String> getRunningJobIds() {
        return getRunningBatches().flatMap(this::extractJobIds).collect(toUnmodifiableSet());
    }

    private Stream<String> extractJobIds(JobExecution jobExec) {
        return Optional.of(jobExec).map(extractProperty(JobBatchlet.PropName.JOB_ID)).stream();
    }

    /**
     * Start a {@code job} as <i>Jakarta-Batch</i>.
     *
     * @param job the {@code Job}
     * @return batch execution id
     * @throws NullPointerException if {@code job} is {@code null}
     * @throws IllegalStateException if state of {@code job} is not {@link JobStatus#RUNNING}
     * @since 1.0.0
     */
    public long startBatchAsync(Job job) {
        if (Objects.requireNonNull(job).getStatus() != JobStatus.RUNNING) {
            throw new IllegalArgumentException("Illegal job state.");
        }

        Properties props = new Properties();
        props.setProperty(JobBatchlet.PropName.JOB_ID, job.getId());
        props.setProperty(JobBatchlet.PropName.JSON_JOB, jsonb.toJson(job));
        props.setProperty(JobBatchlet.PropName.TIMEOUT, jsonb.toJson(TimeUtils.toLocalDateTime(job.getLimitTime())));

        return operator.start(job.getJobdef().getJobKind().name().toLowerCase(), props);
    }

    /**
     * Interrupt the expired batches. Execute {@link JobBatchlet#stop} of <i>Jakarta-Batch</i> which is timed out.
     *
     * @param refTime reference time
     * @throws NullPointerException if {@code refTime} is {@code null}
     * @since 1.0.0
     */
    public void interruptExpiredBatches(LocalDateTime refTime) {
        getRunningBatches().filter(isExpired(Objects.requireNonNull(refTime))).map(JobExecution::getExecutionId)
                .forEach(operator::stop);
    }

    Predicate<JobExecution> isExpired(LocalDateTime refTime) {
        return b -> Optional.of(b).map(extractProperty(JobBatchlet.PropName.TIMEOUT)).flatMap(TimeUtils::tryToLocalDateTime)
                .filter(refTime::isAfter).isPresent();
    }

    Stream<JobExecution> getRunningBatches() {
        return operator.getJobNames().stream().flatMap(this::toBatchIds).flatMap(f(this::toBatchExecution)
                .andThen(Optional::stream));
    }

    private Stream<Long> toBatchIds(String batchName) {
        try {
            return operator.getRunningExecutions(batchName).stream();
        } catch (NoSuchJobExecutionException ignore) {
            return Stream.empty();
        }
    }

    private Optional<JobExecution> toBatchExecution(Long batchId) {
        try {
            return Optional.of(operator.getJobExecution(batchId));
        } catch (NoSuchJobExecutionException ignore) {
            return Optional.empty();
        }
    }

    private Function<JobExecution, String> extractProperty(String propName) {
        return b -> Optional.of(b).map(JobExecution::getJobParameters).map(p -> p.getProperty(propName)).orElse(null);
    }
}
