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
package jp.mydns.projectk.safi.batch;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.listener.StepListener;
import jakarta.batch.runtime.context.StepContext;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Objects;
import java.util.Optional;
import static java.util.function.Predicate.not;
import jp.mydns.projectk.safi.producer.RequestContextProducer;
import trial.JobRecordingService;
import trial.JobService;
import trial.UncheckedInterruptedException;

/**
 * Finisher for the <i>Job</i> processing. Registers the processing record and updates the job status to finished
 * status.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Named
@Dependent
public class JobFinisher implements StepListener {

    @Inject
    @BatchProperty(name = "job-id")
    private String jobId;

    @Inject
    private StepContext stepCtx;

    @Inject
    private JobService jobSvc;

    @Inject
    private JobRecordingService recSvc;

    @Inject
    private RequestContextProducer reqCtxPrd;

    /**
     * Do nothing.
     *
     * @throws Exception never
     * @since 1.0.0
     */
    @Override
    public void beforeStep() throws Exception {
        // Do nothing.
    }

    /**
     * Terminate the job. A processing record is registered, and if there is no failure, the job will be update to a
     * successful finished status. If it is interrupted, it will be update to an aborted status. If an exception occurs,
     * or if there is content that failed to be processed, it will be update to a failure finished status.
     *
     * @throws Exception throw if an error occurs
     * @since 1.0.0
     */
    @Override
    @ActivateRequestContext
    @SuppressWarnings("ThrowableResultIgnored")
    public void afterStep() throws Exception {
        reqCtxPrd.setup("JobFinisher");

        Optional<Exception> cause = Optional.ofNullable(stepCtx.getException());

        // Record the exception's message.
        cause.map(Exception::getMessage).filter(Objects::nonNull).filter(not(String::isBlank)).ifPresent(recSvc::rec);
        cause.ifPresent(c -> recSvc.rec("The transaction was rollback because occurred an any exception."));

        // Register the rocording messages and recording contents.
        jobSvc.appendMessages(jobId, recSvc.playMessages());
        jobSvc.appendRecords(jobId, recSvc.playRecords());

        // Update job status to finished.
        if (cause.isEmpty() && recSvc.isNoFailure()) {
            jobSvc.updateToSuccessState(jobId);
        } else {
            cause.filter(this::isInterrupted).ifPresentOrElse(
                    ex -> jobSvc.updateToAbortState(jobId),
                    () -> jobSvc.updateToFailureState(jobId));
        }
    }

    boolean isInterrupted(Exception ex) {
        return ex instanceof InterruptedException || ex instanceof UncheckedInterruptedException;
    }
}
