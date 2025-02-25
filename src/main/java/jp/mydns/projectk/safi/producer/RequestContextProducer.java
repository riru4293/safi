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
import jp.mydns.projectk.safi.value.RequestContext;

/**
 * Produce the {@link RequestContext}. Must be set values before producing.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class RequestContextProducer {

    private String accountId;
    private String processName;

    /**
     * Set current account id.
     *
     * @param accountId logged account id
     * @since 3.0.0
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Set current processing name.
     *
     * @param processName current processing name. It is screen or batch processing name.
     * @since 3.0.0
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    /**
     * Produce the {@code RequestContext}.
     *
     * @return the {@code RequestContext}
     * @since 3.0.0
     */
    @Produces
    @RequestScoped
    public RequestContext produce() {
        return new RequestContextImpl(accountId, processName);
    }

    private class RequestContextImpl implements RequestContext {

        private final String accountId;
        private final String processName;

        public RequestContextImpl(String accountId, String processName) {
            this.accountId = accountId;
            this.processName = processName;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public String getAccountId() {
            return accountId;
        }

        /**
         * {@inheritDoc}
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
            return "RequestContext{" + "processName=" + processName + ", accountId=" + accountId + '}';
        }
    }
}
