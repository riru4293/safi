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
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Selection;
import java.util.List;
import jp.mydns.projectk.safi.dao.criteria.CriteriaPathContext;
import jp.mydns.projectk.safi.dao.criteria.UserPathContext;
import jp.mydns.projectk.safi.entity.ContentEntity_;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.entity.embedded.AttsEmb_;
import jp.mydns.projectk.safi.entity.embedded.TxtValidityPeriodEmb_;
import jp.mydns.projectk.safi.producer.EntityManagerProducer;

/**
 * Data access processing to export the <i>ID-Content</i> of the <i>User</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserExportationDao extends ExportationDao<UserEntity> {

    @Inject
    @EntityManagerProducer.ForBatch
    private EntityManager em;

    @Inject
    private CommonBatchDao comDao;

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    protected Class<UserEntity> getEntityType() {
        return UserEntity.class;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    protected CriteriaPathContext getPathContext(Path<UserEntity> path) {
        return new UserPathContext(path);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    protected List<Selection<String>> getExportItems(Path<UserEntity> path) {
        return List.of(
                path.get(ContentEntity_.id).alias("id"),
                path.get(ContentEntity_.txtEnabled).alias("enabled"),
                path.get(ContentEntity_.name).alias("name"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att01).alias("att01"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att02).alias("att02"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att03).alias("att03"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att04).alias("att04"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att05).alias("att05"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att06).alias("att06"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att07).alias("att07"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att08).alias("att08"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att09).alias("att09"),
                path.get(ContentEntity_.attsEmb).get(AttsEmb_.att10).alias("att10"),
                path.get(ContentEntity_.txtValidityPeriod).get(TxtValidityPeriodEmb_.from).alias("from"),
                path.get(ContentEntity_.txtValidityPeriod).get(TxtValidityPeriodEmb_.to).alias("to"),
                path.get(ContentEntity_.txtValidityPeriod).get(TxtValidityPeriodEmb_.ban).alias("ban")
        );
    }
}
