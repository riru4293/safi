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
package jp.mydns.projectk.safi.dao;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.stream.Stream;
import static jp.mydns.projectk.safi.constant.JobStatus.RUNNING;
import static jp.mydns.projectk.safi.constant.JobStatus.SCHEDULE;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.entity.JobEntity_;
import jp.mydns.projectk.safi.service.RealTimeService;

/**
 Data access for <i>Job</i>.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JobDao {

/**
 Lock the all schedule jobs and all running jobs. The sort order is by schedule time.

 @return all runnable and all running jobs
 @throws PersistenceException if the query execution was failed
 @since 3.0.0
 */
Stream<JobEntity> lockActiveJobs();

/**
 Implements of the {@code JobDao}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(JobDao.class)
@RequestScoped
class Impl implements JobDao {

private final EntityManager em;
private final RealTimeService realTimeSvc;

@Inject
@SuppressWarnings("unused")
Impl(EntityManager em, RealTimeService realTimeSvc) {
    this.em = em;
    this.realTimeSvc = realTimeSvc;
}

/**
 {@inheritDoc}

 @throws PersistenceException if the query execution was failed
 @since 3.0.0
 */
@Override
public Stream<JobEntity> lockActiveJobs() {

    CriteriaBuilder cb = em.getCriteriaBuilder();

    CriteriaQuery<JobEntity> cq = cb.createQuery(JobEntity.class);

    Root<JobEntity> jobEntity = cq.from(JobEntity.class);

    Predicate onlyActive = jobEntity.get(JobEntity_.status).in(SCHEDULE, RUNNING);

    Predicate isRunnable = cb.lessThanOrEqualTo(jobEntity.get(JobEntity_.scheduleTime),
        realTimeSvc.getLocalNow());

    return em.createQuery(cq.where(onlyActive, isRunnable).orderBy(
        cb.asc(jobEntity.get(JobEntity_.scheduleTime)),
        cb.asc(jobEntity.get(JobEntity_.id)))
    ).setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultStream();
}

}

}
