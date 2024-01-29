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
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jp.mydns.projectk.safi.value.RequestContext;

/**
 * Produce the {@link RequestContext}. Setup must be completed before producing.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class RequestContextProducer {

    private RequestContext reqCtx = new IllegalRequestContext();

    /**
     * Produce the {@code RequestContext}.
     *
     * @return the {@code RequestContext}
     * @since 1.0.0
     */
    @Produces
    @RequestScoped
    public RequestContext produce() {
        return reqCtx;
    }

    /**
     * Setup the request context for background processing that performed by system.
     *
     * @param processName name of the processing requested to be performed
     * @throws ConstraintViolationException if any argument violates the constraint
     */
    public void setup(@NotBlank @Size(max = 250) String processName) {
        setup("SAFI", processName);
    }

    /**
     * Setup the request context.
     *
     * @param accountId id of the account that requested the processing to be performed
     * @param processName name of the processing requested to be performed
     * @throws ConstraintViolationException if any argument violates the constraint
     */
    public void setup(@NotBlank @Size(max = 250) String accountId, @NotBlank @Size(max = 250) String processName) {
        this.reqCtx = new RequestContextImpl(accountId, processName);
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
         * @since 1.0.0
         */
        @Override
        public String getAccountId() {
            return accountId;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String getProcessName() {
            return processName;
        }

        /**
         * Returns a string representation.
         *
         * @return a string representation
         * @since 1.0.0
         */
        @Override
        public String toString() {
            return "RequestContext{" + "processName=" + processName + ", accountId=" + accountId + '}';
        }
    }

    private class IllegalRequestContext implements RequestContext {

        private static final String REASON = "The RequestContext has not been setup yet.";

        /**
         * {@inheritDoc}
         *
         * @throws IllegalStateException always
         * @since 1.0.0
         */
        @Override
        public String getAccountId() {
            throw new IllegalStateException(REASON);
        }

        /**
         * {@inheritDoc}
         *
         * @throws IllegalStateException always
         * @since 1.0.0
         */
        @Override
        public String getProcessName() {
            throw new IllegalStateException(REASON);
        }
    }
}
