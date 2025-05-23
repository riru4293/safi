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
import jakarta.enterprise.inject.Typed;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.PublishableIllegalStateException;
import jakarta.inject.Inject;
import java.util.Objects;
import jp.mydns.projectk.safi.value.RequestContext;
import jp.mydns.projectk.safi.value.RequestContext.AccountIdContext;
import jp.mydns.projectk.safi.value.RequestContext.BatchProcessNameContext;
import jp.mydns.projectk.safi.value.RequestContext.RestApiPathContext;
import jp.mydns.projectk.safi.value.RequestContext.RestApiProcessNameContext;

/**
 Producer of the {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface RequestContextProducer {

/**
 Produce the {@code RequestContext}.

 @return the {@code RequestContext}
 @since 3.0.0
 */
RequestContext produce();

/**
 Implements of the {@code RequestContextProducer}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(RequestContextProducer.class)
@RequestScoped
class Impl implements RequestContextProducer {

private AccountIdContext accountIdCtx;
private RestApiProcessNameContext restApiProcNameCtx;
private BatchProcessNameContext batchProcNameCtx;
private RestApiPathContext restApiPathCtx;

@SuppressWarnings("unused")
Impl() {
}

@Inject
@SuppressWarnings("unused")
void setAccountIdCtx(AccountIdContext accountIdCtx) {
    this.accountIdCtx = accountIdCtx;
}

@Inject
@SuppressWarnings("unused")
void setRestApiProcNameCtx(RestApiProcessNameContext restApiProcNameCtx) {
    this.restApiProcNameCtx = restApiProcNameCtx;
}

@Inject
@SuppressWarnings("unused")
void setBatchProcNameCtx(BatchProcessNameContext batchProcNameCtx) {
    this.batchProcNameCtx = batchProcNameCtx;
}

@Inject
@SuppressWarnings("unused")
void setRestApiPathCtx(RestApiPathContext restApiPathCtx) {
    this.restApiPathCtx = restApiPathCtx;
}

@Produces
@RequestScoped
@Override
public RequestContext produce() {
    return new RequestContextImpl();
}

private class RequestContextImpl implements RequestContext {

@Override
public String getAccountId() {
    return accountIdCtx.getValue();
}

@Override
public String getProcessName() {
    return Stream.of(restApiProcNameCtx, batchProcNameCtx).sequential()
        .map(ProcessNameContext::getValue)
        .filter(Objects::nonNull)
        .findFirst().orElse(null);
}

@Override
public URI getRestApiPath() {
    return Optional.ofNullable(getRawRestApiPath()).orElseThrow(() ->
        new PublishableIllegalStateException(new IllegalStateException(
            "There is no request path to the REST API."
            + " Either it is not an HTTP request or the request path has not been extracted."
            + " Either way, it is an implementation defect.")));
}

@Override
public URI getRawRestApiPath() {
    return restApiPathCtx.getValue();
}

@Override
public String toString() {
    return "RequestContext{" + "accountId=" + getAccountId()
        + ", restApiPath=" + getRawRestApiPath() + ", processName=" + getProcessName() + '}';
}

}

}

}
