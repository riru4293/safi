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
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.exception.trial.PublishableIllegalStateException;
import jp.mydns.projectk.safi.value.RequestContext;

/**
 Producer of the {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@RequestScoped
public class RequestContextProducer {

private RequestContext.AccountIdContext accountIdCtx;
private RequestContext.RestApiProcessNameContext restApiProcNameCtx;
private RequestContext.BatchProcessNameContext batchProcNameCtx;
private RequestContext.PathContext pathCtx;

/**
 Inject the {@code AccountIdContext}.

 @param accountIdCtx the {@code AccountIdContext}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
public void setAccountIdCtx(RequestContext.AccountIdContext accountIdCtx) {
    this.accountIdCtx = accountIdCtx;
}

/**
 Inject the {@code RestApiProcessNameContext}.

 @param restApiProcNameCtx the {@code RestApiProcessNameContext}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
public void setRestApiProcNameCtx(RequestContext.RestApiProcessNameContext restApiProcNameCtx) {
    this.restApiProcNameCtx = restApiProcNameCtx;
}

/**
 Inject the {@code BatchProcessNameContext}.

 @param batchProcNameCtx the {@code BatchProcessNameContext}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
public void setBatchProcNameCtx(RequestContext.BatchProcessNameContext batchProcNameCtx) {
    this.batchProcNameCtx = batchProcNameCtx;
}

/**
 Inject the {@code PathContext}.

 @param pathCtx the {@code PathContext}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
void setPathCtx(RequestContext.PathContext pathCtx) {
    this.pathCtx = pathCtx;
}

/**
 Produce the {@code RequestContext}.

 @return the {@code RequestContext}
 @since 3.0.0
 */
@Produces
@RequestScoped
public RequestContext produce() {
    return new RequestContextImpl();
}

private class RequestContextImpl implements RequestContext {

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public Optional<String> getAccountId() {
    return Optional.ofNullable(accountIdCtx.getValue());
}

/**
 {@inheritDoc}

 @throws PublishableIllegalStateException if no found or multiple definitions.
 @since 3.0.0
 */
@Override
public String getProcessName() {

    final Supplier<IllegalStateException> noSingularProcName = () ->
        new PublishableIllegalStateException(new IllegalStateException(
            "Multiple process name definitions found, only one is allowed."));

    return Stream.of(restApiProcNameCtx, batchProcNameCtx)
        .filter(ProcessNameContext::isAvailable).map(ProcessNameContext::getValue)
        .reduce((a, b) -> {
            throw noSingularProcName.get();
        })
        .orElseThrow(noSingularProcName);
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public Optional<URI> getPath() {
    return Optional.ofNullable(pathCtx.getValue());
}

/**
 Returns a string representation.

 @return a string representation
 @since 3.0.0
 */
@Override
public String toString() {
    return "RequestContext{" + "accountId=" + accountIdCtx + ", path=" + pathCtx
        + ", restApiProcessName=" + restApiProcNameCtx + ", batchProcessName=" + batchProcNameCtx
        + '}';
}

}

}
