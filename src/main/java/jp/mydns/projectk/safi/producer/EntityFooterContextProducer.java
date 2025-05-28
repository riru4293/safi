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
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.time.LocalDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.service.TimeService;
import jp.mydns.projectk.safi.value.RequestContext;
import jp.mydns.projectk.safi.entity.listener.EntityFooterUpdater;

/**
 Produce the {@link EntityFooterUpdater.Context}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface EntityFooterContextProducer {

/**
 Produce the {@code EntityFooterUpdater.Context}.

 @return the {@code EntityFooterUpdater.Context}
 @since 3.0.0
 */
EntityFooterUpdater.Context produce();

/**
 Implements of the {@code EntityFooterContextProducer}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(EntityFooterContextProducer.class)
@Dependent
class Impl implements EntityFooterContextProducer {

private final Provider<RequestContext> reqCtxPvd;
private final TimeService timeSvc;

@Inject
@SuppressWarnings("unused")
Impl(Provider<RequestContext> reqCtxPvd, TimeService timeSvc) {
    this.reqCtxPvd = Objects.requireNonNull(reqCtxPvd);
    this.timeSvc = Objects.requireNonNull(timeSvc);
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Produces
@RequestScoped
@Override
public EntityFooterUpdater.Context produce() {
    return new ContextImpl();
}

private class ContextImpl implements EntityFooterUpdater.Context {

@Override
public LocalDateTime getUtcNow() {
    return timeSvc.getLocalNow();
}

@Override
public String getAccountId() {
    return reqCtxPvd.get().getAccountId();
}

@Override
public String getProcessName() {
    return reqCtxPvd.get().getProcessName();
}

@Override
public String toString() {
    return "EntityFooterContext{" + "utcNow=" + getUtcNow() + ", accountId=" + getAccountId()
        + ", processName=" + getProcessName() + '}';
}

}

}

}
