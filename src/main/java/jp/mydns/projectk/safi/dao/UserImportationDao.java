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
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.SingularAttribute;
import jp.mydns.projectk.safi.dao.criteria.CriteriaPathContext;
import jp.mydns.projectk.safi.dao.criteria.UserPathContext;
import jp.mydns.projectk.safi.entity.ImportationWorkEntity;
import jp.mydns.projectk.safi.entity.ImportationWorkEntity_;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.entity.UserEntity_;

/**
 * Data access processing to import the <i>ID-Content</i> of the <i>User</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class UserImportationDao extends ImportationDao<UserEntity> {

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
    protected SingularAttribute<ImportationWorkEntity, UserEntity> getRelationToEntity() {
        return ImportationWorkEntity_.userEntity;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    protected SingularAttribute<UserEntity, ImportationWorkEntity> getRelationToWorkEntity() {
        return UserEntity_.wrkEntity;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    protected CriteriaPathContext getPathContext(Path<UserEntity> contentEntityPath) {
        return new UserPathContext(contentEntityPath);
    }
}
