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
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import static jakarta.json.JsonValue.EMPTY_JSON_OBJECT;
import jakarta.json.bind.Jsonb;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jp.mydns.projectk.safi.interceptor.ProcessName;
import jp.mydns.projectk.safi.interceptor.SystemProcess;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.TransformService;
import jp.mydns.projectk.safi.service.TransformService.Transformer;
import jp.mydns.projectk.safi.util.JsonUtils;
import static jp.mydns.projectk.safi.util.JsonUtils.toJsonValue;
import jp.mydns.projectk.safi.value.Job;
import jp.mydns.projectk.safi.value.JobOptions;
import jp.mydns.projectk.safi.value.Plugdef;

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
    private Jsonb jsonb;

    @Inject
    private AppTimeService appTimeSvc;

    @Inject
    private ConfigService confSvc;

    @Inject
    private JsonService jsonSvc;

    @Inject
    private TransformService transSvc;

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
     *
     * @return exit status string
     * @throws IOException if occurs I/O error
     * @throws InterruptedException if interrupted
     * @since 1.0.0
     */
    @Override
    @ActivateRequestContext
    @SystemProcess
    @ProcessName("HO")
    public String process() throws InterruptedException, IOException {
        // Note: Require for stop the job.
        myThread = Thread.currentThread();

        // Resolve a parameters.
        job = jsonb.fromJson(jsonJob, Job.class);
        jsonSvc.convertViaJson(jsonJob, Job.class);

        // Creates a work-directory for each job executions.
        Files.createDirectories(getJobVarDir());

        final String result = mainProcess();

        // Note: Preventing the possibility that both normal termination processing
        //       and abort processing due to timeout will be executed.
        myThread = null;

        return result;
    }

    /**
     * Get my job dedicated variable data directory.
     *
     * @return path of the var-directory
     * @since 1.0.0
     */
    protected Path getJobVarDir() {
        return confSvc.getJobDir().resolve(job.getId());
    }

    Plugdef getPlugdef() {
        return job.getJobdef().getPlugdef().orElseThrow(() -> new IllegalStateException("No plug-in definition was found."));
    }

    /**
     * Get the plug-in name.
     *
     * @return plug-in name
     * @throws IllegalStateException if no plug-in definition was found
     * @since 1.0.0
     */
    protected String getPluginName() {
        return getPlugdef().getName();
    }

    /**
     * Get the plug-in properties.
     *
     * @return plug-in properties
     * @throws IllegalStateException if no plug-in definition was found
     * @since 1.0.0
     */
    protected JsonObject getPluginProps() {
        return JsonUtils.merge(
                // User defined values
                getPlugdef().getArgs(),
                // Common values
                Json.createObjectBuilder()
                        .add("varDir", toJsonValue(getJobVarDir()))
                        .add("tmpDir", toJsonValue(confSvc.getTmpDir()))
                        .add("appNow", toJsonValue(appTimeSvc.getLocalNow()))
                        .add("jobOpts", job.getOptions().orElse(EMPTY_JSON_OBJECT))
                        .build()
        );
    }

    /**
     * Get the {@code JobOptions}.
     *
     * @return the {@code JobOptions}
     * @since 1.0.0
     */
    protected JobOptions getJobOptions() {
        return new JobOptions(job.getOptions().orElse(EMPTY_JSON_OBJECT));
    }

    /**
     * Get the {@code Transformer}.
     *
     * @return the {@code Transformer}
     * @throws IllegalStateException if no transform definition was found
     * @since 1.0.0
     */
    public Transformer getTransformer() {
        return transSvc.buildTransformer(job.getJobdef().getTrnsdef().orElseThrow(
                () -> new IllegalStateException("No transform definition was found.")));
    }

    /**
     * Throw {@code InterruptedException} if current thread is interrupted.
     *
     * @throws InterruptedException if current thread is interrupted
     * @since 1.0.0
     */
    protected void throwIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
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
