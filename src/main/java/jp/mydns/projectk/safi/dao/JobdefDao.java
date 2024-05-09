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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.entity.JobdefEntity_;

/**
 * Data access processing to the Job definition.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class JobdefDao {

    @Inject
    private EntityManager em;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected JobdefDao() {
    }

    /**
     * Get specified Job definition entity.
     *
     * @param jobdefId job definition id
     * @return job definition entity
     * @throws NullPointerException if {@code jobdefId} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Optional<JobdefEntity> getJobdef(String jobdefId) {
        return getJobdef(Objects.requireNonNull(jobdefId), LockModeType.NONE);
    }

    private Optional<JobdefEntity> getJobdef(String jobdefId, LockModeType lockType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JobdefEntity> cq = cb.createQuery(JobdefEntity.class);

        Root<JobdefEntity> job = cq.from(JobdefEntity.class);

        return em.createQuery(cq.where(cb.equal(job.get(JobdefEntity_.id), jobdefId)))
                .setLockMode(lockType).getResultStream().findFirst();
    }
}
