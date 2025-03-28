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
package jp.mydns.projectk.safi.service.trial;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import jp.mydns.projectk.safi.dao.CommonDao;
import jp.mydns.projectk.safi.dao.JobDao;
import jp.mydns.projectk.safi.dxo.JobDxo;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.value.JobCreationContext;
import jp.mydns.projectk.safi.value.JobValue;

/**
 * Service for <i>Job</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface JobService {

    /**
     * Indicates that a <i>Job</i> I/O exception has occurred. For example, it doesn't exist, you don't have permission
     * to modify it, and so on.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class JobIOException extends IOException implements Serializable {

        @java.io.Serial
        static final long serialVersionUID = 6729040049842755289L;

        /**
         * Construct with error message.
         *
         * @param message error message. Keep in mind that this may be exposed to users. It must be an abstract message
         * that can briefly describe the issue.
         * @since 3.0.0
         */
        public JobIOException(String message) {
            super(message);
        }
    }

    /**
     * Create a job. The state of the job that is created is schedule, and the schedule means schedule of batch process
     * execution.
     *
     * @param ctx the {@code JobCreationContext}
     * @return created job
     * @throws NullPointerException if {@code ctx} is {@code null}
     * @throws PersistenceException if register fail to database
     * @since 3.0.0
     */
    JobValue createJob(JobCreationContext ctx);

    /**
     * Implements of the {@code JobdefService}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Typed(JobService.class)
    @RequestScoped
    class Impl implements JobService {

        private final CommonDao comDao;
        private final JobDao jobDao;
        private final JobDxo jobDxo;

        /**
         * Constructor.
         *
         * @param comDao the {@code CommonDao}
         * @param jobDao the {@code JobDao}
         * @param jobDxo the {@code JobDxo}
         * @since 3.0.0
         */
        @Inject
        public Impl(CommonDao comDao, JobDao jobDao, JobDxo jobDxo) {
            this.comDao = comDao;
            this.jobDao = jobDao;
            this.jobDxo = jobDxo;
        }

        /**
         * {@inheritDoc}
         *
         * @throws ConstraintViolationException if any argument violates the constraint
         * @throws PersistenceException if register fail to database
         * @since 3.0.0
         */
        @Override
        @Transactional(TxType.REQUIRES_NEW)
        public JobValue createJob(JobCreationContext ctx) {

            JobEntity job = comDao.persist(jobDxo.newEntity(ctx));

            comDao.flush();

            return jobDxo.toValue(job);
        }
    }
}
