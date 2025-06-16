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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.stream.Stream;
import static jp.mydns.projectk.safi.constant.JobStatus.RUNNING;
import static jp.mydns.projectk.safi.constant.JobStatus.SCHEDULE;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.entity.JobEntity_;
import jp.mydns.projectk.safi.service.TimeService;

/**
 <i>Job</i> data access processing.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JobDao {

/**
 Lock the all schedule jobs and all running jobs. The sort order is by schedule time.

 @return all runnable and all running jobs
 @throws QueryTimeoutException if the query execution exceeds the query timeout value set and only
 the statement is rolled back.
 @throws TransactionRequiredException if there is no transaction.
 @throws PessimisticLockException if pessimistic locking fails and the transaction is rolled back.
 @throws LockTimeoutException if pessimistic locking fails and only the statement is rolled back.
 @throws PersistenceException if the query execution was failed.
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
@ApplicationScoped
class Impl implements JobDao {

private final Provider<EntityManager> emPvd;
private final TimeService timeSvc;

@SuppressWarnings("unused")
Impl() {
    // Note: The default constructor exists only to allow NetBeans to recognize the CDI bean.
    throw new UnsupportedOperationException();
}

@Inject
@SuppressWarnings("unused")
Impl(Provider<EntityManager> emPvd, TimeService timeSvc) {
    this.emPvd = emPvd;
    this.timeSvc = timeSvc;
}

/**
 {@inheritDoc}

 @throws QueryTimeoutException if the query execution exceeds the query timeout value set and only
 the statement is rolled back.
 @throws TransactionRequiredException if there is no transaction.
 @throws PessimisticLockException if pessimistic locking fails and the transaction is rolled back.
 @throws LockTimeoutException if pessimistic locking fails and only the statement is rolled back.
 @throws PersistenceException if the query execution was failed.
 @since 3.0.0
 */
@Override
public Stream<JobEntity> lockActiveJobs() {

    EntityManager em = emPvd.get();

    CriteriaBuilder cb = em.getCriteriaBuilder();

    CriteriaQuery<JobEntity> cq = cb.createQuery(JobEntity.class);

    Root<JobEntity> job = cq.from(JobEntity.class);

    Predicate onlyActive = job.get(JobEntity_.status).in(SCHEDULE, RUNNING);

    Predicate isRunnable = cb.lessThanOrEqualTo(job.get(JobEntity_.scheduleTime),
        timeSvc.getLocalNow());

    return em.createQuery(cq.where(onlyActive, isRunnable)
        .orderBy(
            cb.asc(job.get(JobEntity_.scheduleTime)),
            cb.asc(job.get(JobEntity_.id))
        ))
        .setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultStream();
}

}

}
