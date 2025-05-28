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
import jakarta.persistence.PersistenceException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.AppConfigId;
import jp.mydns.projectk.safi.entity.AppConfigEntity;
import jp.mydns.projectk.safi.entity.AppConfigEntity_;

/**
 <i>Application Configuration</i> data access processing.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface AppConfigDao {

/**
 Get an application configuration.

 @param id the {@code AppConfigId}.
 @return application configuration entity.
 @throws PersistenceException if the query execution was failed.
 @throws QueryTimeoutException if the query execution exceeds the query timeout value set and only
 the statement is rolled back.
 @throws PersistenceException if the query execution exceeds the query timeout value set and the
 transaction is rolled back.
 @since 3.0.0
 */
Optional<AppConfigEntity> getAppConfig(AppConfigId id);

/**
 Implements of the {@code AppConfigDao}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(AppConfigDao.class)
@ApplicationScoped
class Impl implements AppConfigDao {

private final Provider<EntityManager> emPvd;

@Inject
@SuppressWarnings("unused")
Impl(Provider<EntityManager> emPvd) {
    this.emPvd = emPvd;
}

/**
 {@inheritDoc}

 @throws PersistenceException if the query execution was failed.
 @throws QueryTimeoutException if the query execution exceeds the query timeout value set and only
 the statement is rolled back.
 @throws PersistenceException if the query execution exceeds the query timeout value set and the
 transaction is rolled back.
 @since 3.0.0
 */
@Override
public Optional<AppConfigEntity> getAppConfig(AppConfigId id) {
    EntityManager em = emPvd.get();

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<AppConfigEntity> cq = cb.createQuery(AppConfigEntity.class);

    Root<AppConfigEntity> appConf = cq.from(AppConfigEntity.class);

    return em.createQuery(cq.where(cb.equal(appConf.get(AppConfigEntity_.id), id)))
        .getResultStream().findFirst();
}

}

}
