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
package jp.mydns.projectk.safi.dao;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.entity.JobEntity_;

/**
 * Data access processing to the <i>Job</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class JobDao {

    @Inject
    private EntityManager em;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected JobDao() {
    }

    /**
     * Get specified <i>Job</i> entity.
     *
     * @param jobId job id
     * @return job entity
     * @throws NullPointerException if {@code jobId} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Optional<JobEntity> getJob(String jobId) {
        return getJob(Objects.requireNonNull(jobId), LockModeType.NONE);
    }

    /**
     * Get specified <i>Job</i> entity with write lock.
     *
     * @param jobId job id
     * @return job entity with write lock
     * @throws NullPointerException if {@code jobId} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    public Optional<JobEntity> lockJob(String jobId) {
        return getJob(Objects.requireNonNull(jobId), LockModeType.PESSIMISTIC_WRITE);
    }

    private Optional<JobEntity> getJob(String jobId, LockModeType lockType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JobEntity> cq = cb.createQuery(JobEntity.class);

        Root<JobEntity> job = cq.from(JobEntity.class);

        return em.createQuery(cq.where(cb.equal(job.get(JobEntity_.id), jobId)))
                .setLockMode(lockType).getResultStream().findFirst();
    }

    /**
     * Get active <i>Job</i> entities with write lock. Get jobs with status {@link JobStatus#SCHEDULE} or
     * {@link JobStatus#RUNNING} that were scheduled before the specified time. Up to 10 items will be retrieved.
     *
     * @param realNow current time, in that case timezone is UTC.
     * @return active job entities
     * @throws NullPointerException if {@code refTime} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    public List<JobEntity> lockActiveJobs(LocalDateTime realNow) {
        Objects.requireNonNull(realNow);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JobEntity> cq = cb.createQuery(JobEntity.class);

        Root<JobEntity> job = cq.from(JobEntity.class);

        Predicate onlyActive = job.get(JobEntity_.status).in(JobStatus.SCHEDULE, JobStatus.RUNNING);
        Predicate recent = cb.lessThanOrEqualTo(job.get(JobEntity_.scheduleTime), realNow);

        Expression<Object> statusOrder = cb.selectCase().when(cb.equal(job.get(JobEntity_.status), JobStatus.RUNNING), 10).otherwise(90);
        List<Order> orders = List.of(cb.asc(statusOrder), cb.asc(job.get(JobEntity_.scheduleTime)),
                cb.asc(job.get(JobEntity_.id)));

        return em.createQuery(cq.where(onlyActive, recent).orderBy(orders)).setMaxResults(10)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
    }

    /**
     * Get all the expired <i>Job</i> entities with write lock.
     *
     * @param refTime reference time
     * @return expired job entities
     * @throws NullPointerException if {@code refTime} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    public Stream<JobEntity> lockExpiredJobs(LocalDateTime refTime) {
        Objects.requireNonNull(refTime);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JobEntity> cq = cb.createQuery(JobEntity.class);

        Root<JobEntity> job = cq.from(JobEntity.class);

        Predicate onlyUnfinished = job.get(JobEntity_.status).in(JobStatus.SCHEDULE, JobStatus.RUNNING);
        Predicate expired = cb.lessThan(job.get(JobEntity_.limitTime), refTime);

        return em.createQuery(cq.where(onlyUnfinished, expired)).setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultStream();
    }
}
