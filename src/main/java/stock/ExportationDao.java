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
package stock;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Selection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.ContentKind;
import jp.mydns.projectk.safi.dao.CommonBatchDao;
import jp.mydns.projectk.safi.dao.criteria.CriteriaPathContext;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.entity.ContentEntity_;
import jp.mydns.projectk.safi.producer.EntityManagerProducer;

/**
 * Data access processing to export the <i>ID-Content</i>.
 *
 * @param <E> entity type of the <i>ID-Content</i>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ExportationDao<E extends ContentEntity<E>> {

    @Inject
    @EntityManagerProducer.ForBatch
    private EntityManager em;

    @Inject
    private CommonBatchDao comDao;

    /**
     * Get entity type of the <i>ID-Content</i>.
     *
     * @return entity type
     * @since 1.0.0
     */
    protected abstract Class<E> getEntityType();

    /**
     * Get the {@code CriteriaPathContext} made from path of the <i>ID-Content</i> entity.
     *
     * @param path path to the <i>ID-Content</i> entity
     * @return the {@code CriteriaPathContext}
     * @throws NullPointerException if {@code path} is {@code null}
     * @since 1.0.0
     */
    protected abstract CriteriaPathContext getPathContext(Path<E> path);

    /**
     * Get the export elements.
     *
     * @param path path of the content entity
     * @return export elements
     * @since 1.0.0
     */
    protected abstract List<Selection<String>> getExportItems(Path<E> path);

    /**
     * Get the <i>ID-Content</i> with specified id.
     *
     * @param targetIds specifying the <i>ID-Content</i>
     * @return obtained <i>ID-Content</i>
     * @throws NullPointerException if {@code targetIds} is {@code null}, or contains {@code null}.
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    public Stream<E> getContents(List<String> targetIds) {
        if (Set.copyOf(targetIds).isEmpty()) {
            return Stream.empty();
        }

        Class<E> eType = getEntityType();
        CriteriaQuery<E> cq = em.getCriteriaBuilder().createQuery(eType);

        return em.createQuery(cq.where(cq.from(eType).get(ContentEntity_.id).in(targetIds)))
                .setLockMode(PESSIMISTIC_WRITE).getResultStream();
    }

    /**
     * Get the export materials.
     *
     * @param kind type of content to retrieve.
     * @return materials of exporting
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code contentType} is not allowed
     * @throws PersistenceException if the query execution was failed
     *
     * @since NewApp 9.0
     * @since 2022-09-01
     */
    public Stream<List<Map<String, String>>> getMaterials(ContentKind kind) {

        Objects.requireNonNull(kind);

        return null;

//        return switch (kind) {
        // ToDo: 結合されたエクスポートコンテンツの取得処理を実装する
//            case COMBINED_PER_USER ->
//                combiUserDao.fetchAll();
//
//            case COMBINED_PER_MEDIUM ->
//                combiMediumDao.fetchAll();
//
        // ToDo: コンテンツごとのエクスポートコンテンツの取得処理を実装する
//            case USER ->
//                userDao.fetchAll();
//            case MEDIUM ->
//                medDao.fetchAll();
//
//            case GROUP ->
//                grpDao.fetchAll();
//
//            case BELONG_GROUP ->
//                blgGrpDao.fetchAll();
//
//            case ORG1 ->
//                org1Dao.fetchAll();
//
//            case ORG2 ->
//                org2Dao.fetchAll();
//
//            case BELONG_ORG ->
//                blgOrgDao.fetchAll();
//            default ->
//                throw new IllegalArgumentException("Illegal contentType.");
//        };
    }
}
