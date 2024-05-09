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
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import java.util.Objects;
import jp.mydns.projectk.safi.entity.CommonEntity;

/**
 * Common data access object.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class CommonDao {

    @Inject
    private EntityManager em;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected CommonDao() {
    }

    /**
     * Call both the {@link #flush} and the {@link #clear}.
     *
     * @throws TransactionRequiredException if there is no transaction
     * @throws PersistenceException if the flush fails
     * @since 1.0.0
     */
    public void flushAndClear() {
        flush();
        clear();
    }

    /**
     * Synchronize the persistence context to the underlying database.
     *
     * @throws TransactionRequiredException if there is no transaction
     * @throws PersistenceException if the flush fails
     * @since 1.0.0
     */
    public void flush() {
        em.flush();
    }

    /**
     * Clear the persistence context, causing all managed entities to become detached. Changes made to entities that
     * have not been flushed to the database will not be persisted.
     *
     * @since 1.0.0
     */
    public void clear() {
        em.clear();
    }

    /**
     * Make an {@code entity} managed and persistent.
     *
     * @param <T> entity type
     * @param entity an entity
     * @return entity that persisted
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws EntityExistsException if the {@code entity} already exists
     * @throws IllegalArgumentException if the {@code entity} is not entity
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    public <T> T persist(T entity) {
        em.persist(Objects.requireNonNull(entity));
        return entity;
    }

    /**
     * Make an {@code entity} merge.
     *
     * @param <T> entity type
     * @param entity an entity
     * @return entity that merged
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws IllegalArgumentException if the instance is not an entity
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    public <T> T merge(T entity) {
        if (em.contains(Objects.requireNonNull(entity))) {
            return entity;
        }

        return em.merge(entity);
    }

    /**
     * Make an {@code entity} persistent or merge. Persist if version is less than 1, merge otherwise.
     *
     * @param <T> entity type
     * @param entity an entity
     * @return entity that persisted or merged
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws EntityExistsException if the entity already exists when persist
     * @throws IllegalArgumentException if the instance is not an entity
     * @throws TransactionRequiredException if there is no transaction
     * @since 1.0.0
     */
    public <T extends CommonEntity> T persistOrMerge(T entity) {
        return entity.getVersion() < 1 ? persist(entity) : merge(entity);
    }
}
