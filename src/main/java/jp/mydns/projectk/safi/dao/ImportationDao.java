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

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.dao.criteria.CriteriaPathContext;
import jp.mydns.projectk.safi.dao.criteria.PredicateBuilder;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.entity.ContentEntity_;
import jp.mydns.projectk.safi.entity.ImportationWorkEntity;
import jp.mydns.projectk.safi.entity.ImportationWorkEntity_;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb_;
import jp.mydns.projectk.safi.producer.EntityManagerProducer;
import jp.mydns.projectk.safi.util.StreamUtils;
import jp.mydns.projectk.safi.value.Condition;

/**
 * Data access processing to the <i>ID-Content</i>.
 *
 * @param <E> entity type of the <i>ID-Content</i>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ImportationDao<E extends ContentEntity<E>> {

    @Inject
    @EntityManagerProducer.ForBatch
    private EntityManager em;

    @Inject
    private CommonBatchDao comDao;

    /**
     * Clear the working area for importation. Finally call the {@link EntityManager#flush()} and the
     * {@link EntityManager#clear()}.
     *
     * @throws TransactionRequiredException if there is no transaction
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void clearWrk() {
        em.createQuery(em.getCriteriaBuilder().createCriteriaDelete(ImportationWorkEntity.class)).executeUpdate();
        comDao.flushAndClear();
    }

    /**
     * Append content to working area. Finally call the {@link EntityManager#flush()} and the
     * {@link EntityManager#clear()}.
     *
     * @param wrks collection of the {@code ImportationWorkEntity}
     * @throws NullPointerException if {@code wrks} is {@code null}, or if contains {@code null} in the {@code works}.
     * @throws TransactionRequiredException if there is no transaction
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    @Transactional(Transactional.TxType.MANDATORY)
    public void appendsWrk(Collection<ImportationWorkEntity> wrks) {
        List.copyOf(wrks).forEach(comDao::persist);
        comDao.flushAndClear();
    }

    /**
     * Get entity type of the <i>ID-Content</i>.
     *
     * @return entity type of the <i>ID-Content</i>
     * @since 1.0.0
     */
    protected abstract Class<E> getContentEntityClass();

    /**
     * Get the relationship to the <i>ID-Content</i> entity from the {@code ImportationWorkEntity}.
     *
     * @return relationship to the <i>ID-Content</i> entity
     * @since 1.0.0
     */
    protected abstract SingularAttribute<ImportationWorkEntity, E> getPathToContentEntity();

    /**
     * Get the relationship to the {@code ImportationWorkEntity} from the <i>ID-Content</i> entity.
     *
     * @return relationship to the {@code ImportationWorkEntity}
     * @since 1.0.0
     */
    protected abstract SingularAttribute<E, ImportationWorkEntity> getPathToWrkEntity();

    /**
     * Get the {@code CriteriaPathContext} made from path of the <i>ID-Content</i> entity.
     *
     * @param path path of the <i>ID-Content</i> entity
     * @return the {@code CriteriaPathContext}
     * @throws NullPointerException if {@code path} is {@code null}
     * @since 1.0.0
     */
    protected abstract CriteriaPathContext getPathContext(Path<E> path);

    /**
     * Get the <i>ID-Content</i> with the specified id.
     *
     * @param targetIds id for narrow down processing results
     * @return stream of the <i>ID-Content</i>
     * @throws NullPointerException if {@code targetIds} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<E> getContents(List<String> targetIds) {
        if (Set.copyOf(targetIds).isEmpty()) {
            return Stream.empty();
        }

        Class<E> eClass = getContentEntityClass();
        CriteriaQuery<E> cq = em.getCriteriaBuilder().createQuery(eClass);

        return em.createQuery(cq.where(cq.from(eClass).get(ContentEntity_.id).in(targetIds)))
                .setLockMode(PESSIMISTIC_WRITE).getResultStream();
    }

    /**
     * Get the id of the <i>ID-Content</i> that will be added by import processing. It exists for those that are about
     * to be registered, but not for those that have already been registered.
     *
     * <p>
     * Notes.
     * <ul>
     * <li>Must run {@link #appendsWrk(java.util.Collection) registration to working area} beforehand</li>
     * </ul>
     *
     * @param targetIds id for narrow down processing results. Anything not included in {@code targetIds} will be
     * excluded from the processing results.
     * @return id of the <i>ID-Content</i> to be added
     * @throws NullPointerException if {@code targetIds} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<String> getAdditionalDiferrence(Collection<String> targetIds) {
        if (Set.copyOf(targetIds).isEmpty()) {
            return Stream.empty();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);

        // FROM
        Root<ImportationWorkEntity> w = cq.from(ImportationWorkEntity.class);
        Join<ImportationWorkEntity, E> e = w.join(getPathToContentEntity(), JoinType.LEFT);
        w.fetch(getPathToContentEntity(), JoinType.LEFT);

        // PATH
        Path<String> wId = w.get(ImportationWorkEntity_.id);

        // WHERE
        Predicate noExists = cb.isNull(e.get(ContentEntity_.id));
        Predicate isTarget = wId.in(targetIds);

        return em.createQuery(cq.select(cb.construct(String.class, wId))
                .where(noExists, isTarget)).setLockMode(PESSIMISTIC_WRITE).getResultStream();
    }

    /**
     * Get the <i>ID-Content</i> that will be updated by import processing. They exists in both already registered and
     * will be registered, but with different values.
     *
     * <p>
     * Notes.
     * <ul>
     * <li>Must run {@link #appendsWrk(java.util.Collection) registration to working area} beforehand</li>
     * </ul>
     *
     * @param targetIds id for narrow down processing results. Anything not included in {@code targetIds} will be
     * excluded from the processing results.
     * @return the <i>ID-Content</i> to be updated
     * @throws NullPointerException if {@code targetIds} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<E> getUpdateDifference(Collection<String> targetIds) {
        if (Set.copyOf(targetIds).isEmpty()) {
            return Stream.empty();
        }

        Class<E> eClass = getContentEntityClass();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(eClass);

        // FROM
        Root<E> e = cq.from(eClass);
        e.fetch(getPathToWrkEntity(), JoinType.INNER);

        // PATH
        Path<String> wDigest = e.get(getPathToWrkEntity()).get(ImportationWorkEntity_.digest);
        Path<String> cDigest = e.get(ContentEntity_.digest);

        // WHERE
        Predicate hasDiff = cb.notEqual(cDigest, wDigest);
        Predicate isTarget = e.get(ContentEntity_.id).in(targetIds);

        return em.createQuery(cq.where(hasDiff, isTarget)).setLockMode(PESSIMISTIC_WRITE).getResultStream();
    }

    /**
     * Get count of <i>ID-Content</i> that will be lost by import processing. It exists for those that are already been
     * registered, but not for those that have about to be registered.
     *
     * <p>
     * Notes.
     * <ul>
     * <li>Must run {@link #appendsWrk(java.util.Collection) registration to working area} beforehand</li>
     * </ul>
     *
     * @param condition filtering condition
     * @return count of the <i>ID-Content</i> that will be lost
     * @throws NullPointerException if {@code condition} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public long getCountOfDeletionDifference(Condition condition) {
        return buildQueryForGetLosts(Long.class, Objects.requireNonNull(condition)).getSingleResult();
    }

    /**
     * Get the <i>ID-Content</i> that will be lost by import processing. It exists for those that are already been
     * registered, but not for those that have about to be registered.
     *
     * <p>
     * Notes.
     * <ul>
     * <li>Must run {@link #appendsWrk(java.util.Collection) registration to working area} beforehand</li>
     * </ul>
     *
     * @param condition filtering condition
     * @return the <i>ID-Content</i> to be deleted. A entities of {@value StreamUtils#CHUNK_SIZE} items is retrieved
     * from the data source every time a element of stream is retrieved. Therefore, you can prevent memory from running
     * out when there are a large number of items.
     * @throws NullPointerException if {@code condition} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<List<E>> getDeletionDifference(Condition condition) {
        return StreamUtils.toChunkedStream(buildQueryForGetLosts(getContentEntityClass(), Objects.requireNonNull(condition)));
    }

    @SuppressWarnings("unchecked")
    <Q> TypedQuery<Q> buildQueryForGetLosts(Class<Q> clazz, Condition condition) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Q> cq = cb.createQuery(clazz);

        // FROM
        Root<E> e = cq.from(getContentEntityClass());

        // SubQuery
        Subquery<ImportationWorkEntity> sub = cq.subquery(ImportationWorkEntity.class);
        Root<ImportationWorkEntity> w = sub.from(ImportationWorkEntity.class);

        // WHERE
        Predicate isExistsInWork = cb.equal(w.get(ImportationWorkEntity_.id), e.get(ContentEntity_.id));
        Predicate isLost = cb.not(cb.exists(sub.where(isExistsInWork)));
        Predicate withEnabled = cb.isTrue(e.get(ContentEntity_.enabled));
        Predicate additionalFiltering = new PredicateBuilder(cb, getPathContext(e)).build(condition);

        if (clazz == Long.class) {
            cq = (CriteriaQuery<Q>) ((CriteriaQuery<Long>) cq).select(cb.count(e));
        }

        return em.createQuery(cq.where(isLost, withEnabled, additionalFiltering)).setLockMode(PESSIMISTIC_WRITE);
    }

    /**
     * Get the <i>ID-Content</i> that require rebuilding. Extracts content that is invalid and has a {@code refTime}
     * with in validity-period, or versa.
     *
     * @param refTime reference time for rebuild
     * @return content that require rebuilding. A entities of {@value StreamUtils#CHUNK_SIZE} items is retrieved from
     * the data source every time a element of stream is retrieved. Therefore, you can prevent memory from running out
     * when there are a large number of items.
     * @throws NullPointerException if {@code refTime} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<List<E>> getRequireRebuilding(LocalDateTime refTime) {
        Objects.requireNonNull(refTime);

        Class<E> eClass = getContentEntityClass();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(eClass);

        // FROM
        Root<E> c = cq.from(eClass);

        // Path
        Path<LocalDateTime> from = c.get(ContentEntity_.validityPeriod).get(ValidityPeriodEmb_.localFrom);
        Path<LocalDateTime> to = c.get(ContentEntity_.validityPeriod).get(ValidityPeriodEmb_.localTo);
        Path<Boolean> enabled = c.get(ContentEntity_.enabled);

        // Where
        Predicate withinRange = cb.between(cb.literal(refTime), from, to);
        Predicate isEnable = cb.equal(enabled, true);

        return StreamUtils.toChunkedStream(em.createQuery(cq.where(cb.or(cb.and(withinRange, cb.not(isEnable)),
                cb.and(cb.not(withinRange), isEnable)))));
    }

    /**
     * Update derived data for <i>ID content</i>. Default implements do nothing.
     *
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public void updateDerivedData() {
        // Do nothing
    }
}
