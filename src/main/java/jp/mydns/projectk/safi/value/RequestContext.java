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
package jp.mydns.projectk.safi.value;

import jakarta.enterprise.context.RequestScoped;
import java.util.Optional;
import static java.util.function.Predicate.not;

/**
 * Current request information.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class RequestContext {

    private String accountId;
    private String processName;

    /**
     * Get process name.
     *
     * @return process name
     * @throws IllegalStateException if value has not been set yet
     * @since 1.0.0
     */
    public String getProcessName() {
        return requireAlreadySet(processName);
    }

    /**
     * Set process name.
     *
     * @param processName process name
     * @throws IllegalArgumentException if {@code processName} is blank
     * @throws IllegalStateException if value has already set
     * @since 1.0.0
     */
    public void setProcessName(String processName) {
        requireNotSetYet(this.processName);
        this.processName = requireNotBlank(processName);
    }

    /**
     * Get account id.
     *
     * @return account id
     * @throws IllegalStateException if value has not been set yet
     * @since 1.0.0
     */
    public String getAccountId() {
        return requireAlreadySet(accountId);
    }

    /**
     * Set account id.
     *
     * @param accountId account id
     * @throws IllegalArgumentException if {@code accountId} is blank
     * @throws IllegalStateException if value has already set
     * @since 1.0.0
     */
    public void setAccountId(String accountId) {
        requireNotSetYet(this.accountId);
        this.accountId = requireNotBlank(accountId);
    }

    /**
     * Returns a string representation of this.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "RequestContext{" + "processName=" + processName + ", accountId=" + accountId + '}';
    }

    private void requireNotSetYet(String v) {
        if (v != null) {
            throw new IllegalStateException("Value has already set.");
        }
    }

    private String requireNotBlank(String v) {
        return Optional.ofNullable(v).filter(not(String::isBlank)).orElseThrow(
                () -> new IllegalArgumentException("Set blank is not allowed."));
    }

    private String requireAlreadySet(String v) {
        return Optional.ofNullable(v).orElseThrow(
                () -> new IllegalStateException("Value has not been set yet."));
    }

}
