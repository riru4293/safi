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

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.exception.PublishableIllegalStateException;
import jakarta.inject.Inject;
import static jp.mydns.projectk.safi.util.CdiUtils.requireResolvable;
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
@RequestScoped
class Impl implements RequestContextProducer {

// Note: It is CDI Bean. Obtaining the request scoped CDI bean via Instance.
private Instance<AccountIdContext> accountIdCtxInst;
private Instance<RestApiProcessNameContext> restApiProcNameCtxInst;
private Instance<BatchProcessNameContext> batchProcNameCtxInst;
private Instance<RestApiPathContext> restApiPathCtxInst;

/**
 Inject the {@code AccountIdContext}.

 @param accountIdCtxInst the {@code AccountIdContext} as {@code Instance}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
public void setAccountIdCtxInst(Instance<AccountIdContext> accountIdCtxInst) {
    this.accountIdCtxInst = accountIdCtxInst;
}

/**
 Inject the {@code RestApiProcessNameContext}.

 @param restApiProcNameCtxInst the {@code RestApiProcessNameContext} as {@code Instance}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
public void setRestApiProcNameCtxInst(Instance<RestApiProcessNameContext> restApiProcNameCtxInst) {
    this.restApiProcNameCtxInst = restApiProcNameCtxInst;
}

/**
 Inject the {@code BatchProcessNameContext}.

 @param batchProcNameCtxInst the {@code BatchProcessNameContext} as {@code Instance}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
public void setBatchProcNameCtxInst(Instance<BatchProcessNameContext> batchProcNameCtxInst) {
    this.batchProcNameCtxInst = batchProcNameCtxInst;
}

/**
 Inject the {@code RestApiPathContext}.

 @param restApiPathCtxInst the {@code RestApiPathContext} as {@code Instance}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
void setRestApiPathCtxInst(Instance<RestApiPathContext> restApiPathCtxInst) {
    this.restApiPathCtxInst = restApiPathCtxInst;
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Produces
@RequestScoped
@Override
public RequestContext produce() {
    return new RequestContextImpl();
}

private class RequestContextImpl implements RequestContext {

/**
 {@inheritDoc}

 @throws PublishableIllegalStateException if the prerequisite information is not found. This
 exception result from an illegal state due to an implementation bug, and the caller should not
 continue processing.
 @since 3.0.0
 */
@Override
public Optional<String> getAccountId() {
    return Optional.ofNullable(requireResolvable(accountIdCtxInst).getValue());
}

/**
 {@inheritDoc}

 @throws PublishableIllegalStateException if the prerequisite information is not found. This
 exception result from an illegal state due to an implementation bug, and the caller should not
 continue processing.
 @since 3.0.0
 */
@Override
public String getProcessName() {

    final Supplier<IllegalStateException> noSingularProcName = () ->
        new PublishableIllegalStateException(new IllegalStateException(
            "Multiple process name definitions found, only one is allowed."));

    return Stream.of(
        requireResolvable(restApiProcNameCtxInst),
        requireResolvable(batchProcNameCtxInst)
    )
        .filter(ProcessNameContext::isAvailable).map(ProcessNameContext::getValue)
        .reduce((a, b) -> {
            throw noSingularProcName.get();
        })
        .orElseThrow(noSingularProcName);
}

/**
 {@inheritDoc}

 @throws PublishableIllegalStateException if the prerequisite information is not found. This
 exception result from an illegal state due to an implementation bug, and the caller should not
 continue processing.
 @since 3.0.0
 */
@Override
public Optional<URI> getRestApiPath() {
    return Optional.ofNullable(requireResolvable(restApiPathCtxInst).getValue());
}

/**
 Returns a string representation.

 @throws PublishableIllegalStateException if the prerequisite information is not found. This
 exception result from an illegal state due to an implementation bug, and the caller should not
 continue processing.
 @return a string representation
 @since 3.0.0
 */
@Override
public String toString() {
    return "RequestContext{" + "accountId=" + getAccountId() + ", restApiPath=" + getRestApiPath()
        + ", processName=" + getProcessName() + '}';
}

}

}

}
