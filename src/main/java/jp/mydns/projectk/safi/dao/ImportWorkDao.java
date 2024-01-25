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
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.entity.ImportWorkEntity;
import jp.mydns.projectk.safi.producer.EntityManagerProducer.ForBatch;

/**
 * Data access processing to the import-work.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class ImportWorkDao {

    @Inject
    private CommonBatchDao comDao;

    @Inject
    @ForBatch
    private EntityManager em;

    /**
     * Clear the import-work. Finally call the {@link EntityManager#flush()} and the {@link EntityManager#clear()}.
     *
     * @throws TransactionRequiredException if there is no transaction
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public void clear() {
        em.createQuery(em.getCriteriaBuilder().createCriteriaDelete(ImportWorkEntity.class)).executeUpdate();
        comDao.flushAndClear();
    }

    /**
     * Append contents to import-work. Finally call the {@link EntityManager#flush()} and the
     * {@link EntityManager#clear()}.
     *
     * @param works id and digest of importation content
     * @throws NullPointerException if {@code works} is {@code null}, or if contains {@code null} in the {@code works}.
     * @throws TransactionRequiredException if there is no transaction
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public void appends(Stream<ImportWorkEntity> works) {
        works.forEach(comDao::persist);
        comDao.flushAndClear();
    }
}
