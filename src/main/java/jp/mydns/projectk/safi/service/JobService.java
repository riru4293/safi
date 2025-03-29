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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.util.Objects;
import jp.mydns.projectk.safi.dao.CommonDao;
import jp.mydns.projectk.safi.dxo.JobDxo;
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
        private final JobDxo jobDxo;

        /**
         * Constructor.
         *
         * @param comDao the {@code CommonDao}
         * @param jobDxo the {@code JobDxo}
         * @throws NullPointerException if any argument is {@code null}
         * @since 3.0.0
         */
        @Inject
        public Impl(CommonDao comDao, JobDxo jobDxo) {
            this.comDao = Objects.requireNonNull(comDao);
            this.jobDxo = Objects.requireNonNull(jobDxo);
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code ctx} is {@code null}
         * @throws PersistenceException if register fail to database
         * @since 3.0.0
         */
        @Override
        @Transactional(TxType.REQUIRES_NEW)
        public JobValue createJob(JobCreationContext ctx) {
            return jobDxo.toValue(comDao.persistAndflush(jobDxo.newEntity(Objects.requireNonNull(ctx))));
        }
    }
}
