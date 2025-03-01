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
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import jp.mydns.projectk.safi.entity.FooterContext;
import jp.mydns.projectk.safi.service.RealTimeService;
import jp.mydns.projectk.safi.value.RequestContext;

/**
 * Produce the {@link FooterContext}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class FooterContextProducer {

    private final RequestContext reqCtx;
    private final RealTimeService realTimeSvc;

    /**
     * Constructor.
     *
     * @param reqCtx the {@code RequestContext}. It inject by CDI.
     * @param realTimeSvc the {@code RealTimeService}. It inject by CDI.
     * @since 3.0.0
     */
    @Inject
    public FooterContextProducer(RequestContext reqCtx, RealTimeService realTimeSvc) {
        this.reqCtx = reqCtx;
        this.realTimeSvc = realTimeSvc;
    }

    /**
     * Produce the {@code FooterContext}.
     *
     * @return the {@code FooterContext}
     * @since 3.0.0
     */
    @Produces
    @RequestScoped
    public FooterContext produce() {
        return new FooterContextImpl(realTimeSvc.getLocalNow(), reqCtx.getAccountId(), reqCtx.getProcessName());
    }

    private class FooterContextImpl implements FooterContext {

        private final LocalDateTime utcNow;
        private final String accountId;
        private final String processName;

        /**
         * Constructor.
         *
         * @param utcNow now as UTC
         * @param accountId logged account id
         * @param processName current processing name
         */
        public FooterContextImpl(LocalDateTime utcNow, String accountId, String processName) {
            this.utcNow = utcNow;
            this.accountId = accountId;
            this.processName = processName;
        }

        /**
         * {@inheritDoc}.
         *
         * @since 3.0.0
         */
        @Override
        public LocalDateTime getUtcNow() {
            return utcNow;
        }

        /**
         * {@inheritDoc}.
         *
         * @since 3.0.0
         */
        @Override
        public String getAccountId() {
            return accountId;
        }

        /**
         * {@inheritDoc}.
         *
         * @since 3.0.0
         */
        @Override
        public String getProcessName() {
            return processName;
        }

        /**
         * Returns a string representation.
         *
         * @return a string representation
         * @since 3.0.0
         */
        @Override
        public String toString() {
            return "FooterContext{" + "utcNow=" + utcNow + ", accountId=" + accountId + ", processName=" + processName + '}';
        }
    }
}
