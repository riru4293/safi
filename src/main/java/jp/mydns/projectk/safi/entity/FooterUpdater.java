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
package jp.mydns.projectk.safi.entity;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Set values in common footer when insert and update to database.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class FooterUpdater {

    private final FooterContext footerCtx;

    /**
     * Constructor.
     *
     * @param footerCtx instance of the {@code FooterContext}
     * @since 1.0.0
     */
    @Inject
    public FooterUpdater(Instance<FooterContext> footerCtx) {
        this.footerCtx = footerCtx.get();
    }

    /**
     * Set entity common footer values when insert. Set all values.
     *
     * @param entity the {@code CommonEntity}
     * @since 1.0.0
     */
    @PrePersist
    public void insert(CommonEntity entity) {
        entity.setRegisterTime(footerCtx.getUtcNow());
        entity.setRegisterAccountId(footerCtx.getAccountId());
        entity.setRegisterProcessName(footerCtx.getProcessName());
        update(entity);
    }

    /**
     * Set entity common footer values when update. Set only values related to update.
     *
     * @param entity the {@code CommonEntity}
     * @since 1.0.0
     */
    @PreUpdate
    public void update(CommonEntity entity) {
        entity.setUpdateTime(footerCtx.getUtcNow());
        entity.setUpdateAccountId(footerCtx.getAccountId());
        entity.setUpdateProcessName(footerCtx.getProcessName());
    }
}
