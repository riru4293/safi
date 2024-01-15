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
package jp.mydns.projectk.safi.batch.listener;

import jakarta.batch.api.listener.StepListener;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.context.StepContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;
import java.util.Optional;
import java.util.Properties;

/**
 * Logger for the begin and end of a batch process.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Named
@Dependent
public class BatchLogger implements StepListener {

    @Inject
    private Logger logger;

    @Inject
    private StepContext stepCtx;

    @Inject
    private JobOperator operator;

    /**
     * Output log on begin processing.
     *
     * @throws Exception throw if an error occurs
     * @since 1.0.0
     */
    @Override
    public void beforeStep() throws Exception {

        long id = stepCtx.getStepExecutionId();
        Properties props = operator.getJobExecution(id).getJobParameters();

        logger.log(INFO, "Begin a batch execution. ExecutionId={0}, Properties={1}.", id, props);

    }

    /**
     * Output log on end processing.
     *
     * @throws Exception throw if an error occurs
     * @since 1.0.0
     */
    @Override
    public void afterStep() throws Exception {

        long id = stepCtx.getStepExecutionId();

        Optional.ofNullable(stepCtx.getException()).ifPresentOrElse(ex -> {

            logger.log(ERROR, "Occurred an exception in batch.", ex);
            logger.log(ERROR, "Batch execution failed. ExecutionId={0}", id);

        }, () -> logger.log(INFO, "Batch execution successful. ExecutionId={0}.", id));

    }
}
