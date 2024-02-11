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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.dao.criteria.CriteriaPathContext;
import jp.mydns.projectk.safi.dao.criteria.PredicateBuilder;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.entity.ContentEntity_;
import jp.mydns.projectk.safi.entity.ImportWorkEntity;
import jp.mydns.projectk.safi.entity.ImportWorkEntity_;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb_;
import jp.mydns.projectk.safi.producer.EntityManagerProducer;
import jp.mydns.projectk.safi.util.JpaUtils;
import static jp.mydns.projectk.safi.util.LambdaUtils.convertElements;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.util.StreamUtils;

/**
 * Data access processing to the <i>ID-Content</i>.
 *
 * @param <C> entity type of the <i>ID-Content</i>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Dependent
public abstract class ContentBatchDao<C extends ContentEntity> {

    @Inject
    @EntityManagerProducer.ForBatch
    private EntityManager em;

    /**
     * Get entity class of the <i>ID-Content</i>.
     *
     * @return entity class of the <i>ID-Content</i>
     * @since 1.0.0
     */
    protected abstract Class<C> getContentEntityClass();

    /**
     * Get the relationship to the <i>ID-Content</i> entity from import-work entity.
     *
     * @return relationship to the <i>ID-Content</i> entity from import-work entity
     * @since 1.0.0
     */
    protected abstract SingularAttribute<ImportWorkEntity, C> getPathToContentEntity();

    /**
     * Get the relationship to the import-work entity from the <i>ID-Content</i> entity.
     *
     * @return relationship to the import-work entity from the <i>ID-Content</i> entity
     * @since 1.0.0
     */
    protected abstract SingularAttribute<C, ImportWorkEntity> getPathToWrkEntity();

    /**
     * Get the {@code CriteriaPathContext} made from path of the <i>ID-Content</i> entity.
     *
     * @param contentEntityPath path of the content entity
     * @return the {@code CriteriaPathContext}
     * @throws NullPointerException if {@code contentEntityPath} is {@code null}
     * @since 1.0.0
     */
    protected abstract CriteriaPathContext getPathContext(Path<C> contentEntityPath);

    /**
     * Get the id of the <i>ID-Content</i> that will be added by import processing. It exists for those that are about
     * to be registered, but not for those that have already been registered.
     *
     * @param targets the id for narrow down processing results. Anything not included in {@code target} will be
     * excluded from the processing results.
     * @return stream of id for the <i>ID-Content</i> collection to be added.
     * @throws NullPointerException if {@code targets} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<String> getAdditions(Set<String> targets) {

        if (Objects.requireNonNull(targets).isEmpty()) {
            return Stream.empty();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);

        // FROM
        Root<ImportWorkEntity> w = cq.from(ImportWorkEntity.class);
        Join<ImportWorkEntity, C> c = w.join(getPathToContentEntity(), JoinType.LEFT);

        w.fetch(getPathToContentEntity(), JoinType.LEFT);

        // PATH
        Path<String> wId = w.get(ImportWorkEntity_.id);

        // WHERE
        Predicate noExists = cb.isNull(c.get(ContentEntity_.id));
        Predicate isTarget = wId.in(targets);

        return em.createQuery(cq.select(cb.construct(String.class, wId))
                .where(noExists, isTarget)).setLockMode(PESSIMISTIC_WRITE).getResultStream();

    }

    /**
     * Get the <i>ID-Content</i> that will be updated by import. They exists in both already registered and will be
     * registered, but with different values.
     *
     * @param targets the id for narrow down processing results. Anything not included in {@code target} will be
     * excluded from the processing results.
     * @return stream for the <i>ID-Content</i> collection to be updated.
     * @throws NullPointerException if {@code targets} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<C> getUpdates(Set<String> targets) {

        if (Objects.requireNonNull(targets).isEmpty()) {
            return Stream.empty();
        }

        Class<C> cClass = getContentEntityClass();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<C> cq = cb.createQuery(cClass);

        // FROM
        Root<C> c = cq.from(cClass);
        c.fetch(getPathToWrkEntity(), JoinType.INNER);

        // PATH
        Path<String> wDigest = c.get(getPathToWrkEntity()).get(ImportWorkEntity_.digest);
        Path<String> cDigest = c.get(ContentEntity_.digest);

        // WHERE
        Predicate hasDiff = cb.notEqual(cDigest, wDigest);
        Predicate isTarget = c.get(ContentEntity_.id).in(targets);

        return em.createQuery(cq.where(hasDiff, isTarget)).setLockMode(PESSIMISTIC_WRITE).getResultStream();

    }

    /**
     * Get a stream of <i>ID-Content</i> collections with the specified id.
     *
     * @param targets the id collection
     * @return stream for the <i>ID-Content</i> collection
     * @throws NullPointerException if {@code targets} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<C> getContents(List<String> targets) {

        if (Objects.requireNonNull(targets).isEmpty()) {
            return Stream.empty();
        }

        Class<C> cClass = getContentEntityClass();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<C> cq = cb.createQuery(cClass);

        // FROM
        Root<C> c = cq.from(cClass);

        // WHERE
        Predicate isTarget = c.get(ContentEntity_.id).in(targets);

        return em.createQuery(cq.where(isTarget)).setLockMode(PESSIMISTIC_WRITE).getResultStream();

    }

    /**
     * Get count of <i>ID-Content</i> that will be lost by import. It exists for those that are already been registered,
     * but not for those that have about to be registered.
     *
     * @param additional additional extract condition
     * @return count of the <i>ID-Content</i> that will be lost
     * @throws NullPointerException if {@code additional} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public long getCountOfLosts(Condition additional) {
        return buildGetLostQuery(Long.class, Objects.requireNonNull(additional)).getSingleResult();
    }

    /**
     * Get the <i>ID-Content</i> that will be lost by import. It exists for those that are already been registered, but
     * not for those that have about to be registered.
     *
     * @param additional additional extract condition
     * @return lost contents. A entities of {@value JpaUtils#CHUNK_SIZE} items is retrieved from the data source every
     * time a element of stream is retrieved. Therefore, you can prevent memory from running out when there are a large
     * number of items.
     * @throws NullPointerException if {@code additional} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<List<C>> getLosts(Condition additional) {
        return StreamUtils.toChunkedStream(buildGetLostQuery(getContentEntityClass(), Objects.requireNonNull(additional)));
    }

    @SuppressWarnings("unchecked")
    <Q> TypedQuery<Q> buildGetLostQuery(Class<Q> clazz, Condition additional) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Q> cq = cb.createQuery(clazz);

        // FROM
        Root<C> c = cq.from(getContentEntityClass());

        // SubQuery
        Subquery<ImportWorkEntity> sub = cq.subquery(ImportWorkEntity.class);
        Root<ImportWorkEntity> w = sub.from(ImportWorkEntity.class);

        // WHERE
        Predicate isExistsInWork = cb.equal(w.get(ImportWorkEntity_.id), c.get(ContentEntity_.id));
        Predicate isLost = cb.not(cb.exists(sub.where(isExistsInWork)));
        Predicate withEnabled = cb.isTrue(c.get(ContentEntity_.enabled));
        Predicate additionalFiltering = new PredicateBuilder(cb, getPathContext(c)).build(additional);

        if (clazz == Long.class) {
            cq = (CriteriaQuery<Q>) ((CriteriaQuery<Long>) cq).select(cb.count(c));
        }

        return em.createQuery(cq.where(isLost, withEnabled, additionalFiltering)).setLockMode(PESSIMISTIC_WRITE);

    }

    /**
     * Do unique rebuild process per content type. Default implements do nothing.
     *
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public void doUniqueRebuilding() {
        // Do nothing
    }

    /**
     * Get the <i>ID-Content</i> that require rebuilding. Extracts content that is invalid and has a {@code refTime}
     * with in validity-period, or versa.
     *
     * @param refTime reference time for judging rebuild
     * @return contents that require rebuilding. A entities of {@value JpaUtils#CHUNK_SIZE} items is retrieved from the
     * data source every time a element of stream is retrieved. Therefore, you can prevent memory from running out when
     * there are a large number of items.
     * @throws NullPointerException if {@code refTime} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<List<C>> getRebuilds(LocalDateTime refTime) {

        Objects.requireNonNull(refTime);

        Class<C> cClass = getContentEntityClass();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<C> cq = cb.createQuery(cClass);

        // FROM
        Root<C> c = cq.from(cClass);

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
     * Extract the export contents. These are all <i>ID-Contents</i> that have already been registered.
     *
     * @return exportation contents. A entities of {@value JpaUtils#CHUNK_SIZE} items is retrieved from the data source
     * every time a element of stream is retrieved. Therefore, you can prevent memory from running out when there are a
     * large number of items.
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<List<Map<String, String>>> getExports() {

        CriteriaQuery<Tuple> cq = em.getCriteriaBuilder().createTupleQuery();

        return StreamUtils.toChunkedStream(em.createQuery(cq.multiselect(getExportItems(cq.from(getContentEntityClass()))
                .toArray(Selection<?>[]::new)))).map(convertElements(JpaUtils::toMap));

    }

    /**
     * Get export items for {@link #getExports()}.
     *
     * @param contentEntityPath path of the content entity
     * @return list of the {@code Selection}
     * @since 1.0.0
     */
    protected abstract List<Selection<String>> getExportItems(Path<C> contentEntityPath);
}
