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
package jp.mydns.projectk.safi.entity.listener;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.entity.CommonEntity;

/**
 * Update JPA entity footer values in the {@link CommonEntity} when prePersist and preUpdate.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface EntityFooterUpdater {

    /**
     * Set only values related to register.
     *
     * @param entity the {@code CommonEntity}
     * @since 3.0.0
     */
    void insert(CommonEntity entity);

    /**
     * only values related to update.
     *
     * @param entity the {@code CommonEntity}
     * @since 3.0.0
     */
    void update(CommonEntity entity);

    /**
     * Implements of the {@code EntityFooterUpdater}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Typed(EntityFooterUpdater.class)
    @Dependent
    class Impl implements EntityFooterUpdater {

        private final Context ctx;

        /**
         * Constructor.
         *
         * @param ctx instance of the {@code EntityFooterUpdater.Context}
         * @throws NullPointerException if {@code ctx} id {@code null}
         * @since 3.0.0
         */
        @Inject
        public Impl(Context ctx) {
            this.ctx = Objects.requireNonNull(ctx);
        }

        /**
         * Set only values related to register.
         *
         * @param entity the {@code CommonEntity}
         * @since 3.0.0
         */
        @Override
        @PrePersist
        public void insert(CommonEntity entity) {
            entity.setRegTime(ctx.getUtcNow());
            entity.setRegId(ctx.getAccountId());
            entity.setRegName(ctx.getProcessName());
        }

        /**
         * only values related to update.
         *
         * @param entity the {@code CommonEntity}
         * @since 3.0.0
         */
        @Override
        @PreUpdate
        public void update(CommonEntity entity) {
            entity.setUpdTime(ctx.getUtcNow());
            entity.setUpdId(ctx.getAccountId());
            entity.setUpdName(ctx.getProcessName());
        }
    }

    /**
     * Provides a footer values for {@link CommonEntity}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface Context {

        /**
         * Get real time of UTC time zone. Rounded down to the nearest millisecond.
         *
         * @return real time
         * @since 3.0.0
         */
        LocalDateTime getUtcNow();

        /**
         * Get login account id.
         *
         * @return account id
         * @since 3.0.0
         */
        String getAccountId();

        /**
         * Get current process name.
         *
         * @return process name
         * @since 3.0.0
         */
        String getProcessName();
    }
}
