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
import jakarta.batch.api.Batchlet;
import jakarta.batch.runtime.BatchStatus;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import static jakarta.json.JsonValue.EMPTY_JSON_OBJECT;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import static java.util.function.Predicate.not;
import jp.mydns.projectk.safi.constant.ContentKind;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.ValidationService;
import jp.mydns.projectk.safi.value.Job;
import jp.mydns.projectk.safi.value.Plugdef;
import trial.JobService;

/**
 * Abstract implements of the <i>Jakarta-Batch</i> for the <i>Job</i> processing.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class JobBatchlet implements Batchlet {

    private Thread myThread;

    private Job job;

    @Inject
    @BatchProperty(name = "json-job")
    private String jsonJob;

    @Inject
    private ConfigService confSvc;

    @Inject
    private JsonService jsonSvc;

    @Inject
    private ValidationService validationSvc;

    @Inject
    private JobService jobSvc;

    /**
     * Stop the executing this.
     *
     * @since 1.0.0
     */
    @Override
    public void stop() {
        if (myThread != null) {
            myThread.interrupt();
        }
    }

    /**
     * Executes unique job processing for each processing type.
     *
     * @return exit status string. It always {@literal COMPLETED}. This is because when processing fails, it always end
     * with an exception thrown.
     * @throws IOException if occurs I/O error
     * @throws InterruptedException if interrupted
     * @since 1.0.0
     */
    protected abstract String mainProcess() throws InterruptedException, IOException;

    /**
     * Execute the job processing.
     * <p>
     * Creates a working directory. It will not be deleted after finished the job. It is a directory under the
     * {@link ConfigService#getJobDir() job-directory} named after the job id.
     *
     * @return exit status string. It does not assume anything other than {@link BatchStatus#COMPLETED COMPLETED}.
     * @throws ConstraintViolationException if the {@link Job} specified with <i>Jakarta-Batch</i> properties violates
     * constraints.
     * @throws IOException if occurs I/O error
     * @throws InterruptedException if interrupted
     * @since 1.0.0
     * @see JobFinisher
     * @see BatchLogger
     */
    @Override
    @ActivateRequestContext
    public String process() throws InterruptedException, IOException {
        // Note: Require for stop the job.
        this.myThread = Optional.of(Thread.currentThread()).filter(not(Thread::isInterrupted))
                .orElseThrow(() -> new InterruptedException());

        this.job = validationSvc.requireValid(jsonSvc.convertViaJson(jsonJob, Job.class));

        Files.createDirectories(getWrkDir());

        final String result = mainProcess();

        // Note: Preventing the possibility that both normal termination processing
        //       and abort processing due to timeout will be executed.
        myThread = null;

        return result;
    }

    /**
     * Get working directory for each job executions. It created at job start.
     *
     * @return working directory
     * @since 1.0.0
     */
    protected Path getWrkDir() {
        return confSvc.getJobDir().resolve(job.getId());
    }

    /**
     * Get the {@code ContentKind}.
     *
     * @return the {@code ContentKind}
     * @since 1.0.0
     */
    protected ContentKind getContentKind() {
        return job.getJobdef().getContentKind();
    }

    /**
     * Get the {@code Plugdef}.
     *
     * @return the {@code Plugdef}
     * @throws IllegalStateException if no found a plug-in definition
     * @since 1.0.0
     */
    protected Plugdef getPlugdef() {
        return job.getJobdef().getPlugdef().orElseThrow(() -> new IllegalStateException("No plug-in definition was found."));
    }

    /**
     * Get optional configurations at job execution.
     *
     * @return optional configurations at job execution
     * @since 1.0.0
     */
    protected JsonObject getJobOptions() {
        return job.getOptions().orElse(EMPTY_JSON_OBJECT);
    }

    /**
     * Get the transform definition.
     *
     * @return the transform definition
     * @since 1.0.0
     */
    protected Map<String, String> getTrnsdef() {
        return job.getJobdef().getTrnsdef().orElseGet(Map::of);
    }

    /**
     * Property name definitions for the {@link JobBatchlet}.
     *
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class has not variable field member and it has all method is static.</li>
     * </ul>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class PropName {

        private PropName() {
        }

        /**
         * Property name for the job id.
         *
         * @since 1.0.0
         */
        public static final String JOB_ID = "job-id";

        /**
         * Property name for the {@link Job} as JSON.
         *
         * @since 1.0.0
         */
        public static final String JSON_JOB = "json-job";

        /**
         * Property name for job execution timeout as duration.
         *
         * @since 1.0.0
         */
        public static final String TIMEOUT = "timeout";
    }
}
