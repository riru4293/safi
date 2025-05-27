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
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import java.util.Objects;
import jp.mydns.projectk.safi.entity.CommonEntity;

/**
 Common processing of data access using JPA.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface CommonDao {

/**
 Make an {@code entity} managed and persistent.

 @param <T> entity type.
 @param entity an any entity.
 @return entity that persisted.
 @throws NullPointerException if {@code entity} is {@code null}.
 @throws EntityExistsException if the {@code entity} already exists.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
<T> T persist(T entity);

/**
 Make an {@code entity} merge.

 @param <T> entity type.
 @param entity an any entity.
 @return entity that merged.
 @throws NullPointerException if {@code entity} is {@code null}.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
<T> T merge(T entity);

/**
 Make an {@code entity} remove. If the entity is not managed by the entity manager, make it managed
 first.

 @param <T> entity type.
 @param entity an any entity.
 @return entity that to be removed.
 @throws NullPointerException if {@code entity} is {@code null}.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
<T> T remove(T entity);

/**
 Synchronize the persistence context to the underlying database.

 @throws TransactionRequiredException if there is no transaction.
 @throws PersistenceException if the flush fails. bug.
 @since 3.0.0
 */
void flush();

/**
 Clear the persistence context, causing all managed entities to become detached. Changes made to
 entities that have not been flushed to the database will not be persisted.

 @since 3.0.0
 */
void clear();

/**
 Make an {@code entity} persistent or merge. Persist if version is less than 1, merge otherwise.

 @param <T> entity type.
 @param entity an any entity.
 @return entity that persisted or merged.
 @throws NullPointerException if {@code entity} is {@code null}.
 @throws EntityExistsException if the {@code entity} already exists.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
<T extends CommonEntity> T persistOrMerge(T entity);

/**
 Call both the {@link #persist} and the {@link #flush}.

 @param <T> entity type.
 @param entity an any entity.
 @return entity that persisted.
 @throws NullPointerException if {@code entity} is {@code null}.
 @throws EntityExistsException if the {@code entity} already exists.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @since 3.0.0
 */
<T> T persistAndflush(T entity);

/**
 Call both the {@link #flush} and the {@link #clear}.

 @throws PersistenceException if failed flush.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
void flushAndClear();

/**
 Implements of the {@code CommonDao}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(CommonDao.class)
@ApplicationScoped
class Impl implements CommonDao {

private final Provider<EntityManager> emPvd;

@Inject
@SuppressWarnings("unused")
Impl(Provider<EntityManager> emPvd) {
    this.emPvd = emPvd;
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}.
 @throws EntityExistsException if the {@code entity} already exists.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction. bug.
 @since 3.0.0
 */
@Override
public <T> T persist(T entity) {
    emPvd.get().persist(Objects.requireNonNull(entity));
    return entity;
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
@Override
public <T> T merge(T entity) {
    EntityManager em = emPvd.get();
    return !em.contains(Objects.requireNonNull(entity)) ? em.merge(entity) : entity;
}

/**
 {@inheritDoc}

 @throws TransactionRequiredException if there is no transaction.
 @throws PersistenceException if the flush fails.
 @since 3.0.0
 */
@Override
public void flush() {
    emPvd.get().flush();
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public void clear() {
    emPvd.get().clear();
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
@Override
public <T> T remove(T entity) {
    emPvd.get().remove(merge(entity));
    return entity;
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}.
 @throws EntityExistsException if the {@code entity} already exists.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
@Override
public <T extends CommonEntity> T persistOrMerge(T entity) {
    return entity.getVersion() < 1 ? persist(entity) : merge(entity);
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}.
 @throws EntityExistsException if the {@code entity} already exists.
 @throws IllegalArgumentException if the {@code entity} is not an entity.
 @since 3.0.0
 */
@Override
public <T> T persistAndflush(T entity) {
    persist(entity);
    flush();
    return entity;
}

/**
 {@inheritDoc}

 @throws PersistenceException if failed flush.
 @throws TransactionRequiredException if there is no transaction.
 @since 3.0.0
 */
@Override
public void flushAndClear() {
    flush();
    clear();
}

}

}
