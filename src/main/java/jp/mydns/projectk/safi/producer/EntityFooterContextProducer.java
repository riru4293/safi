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
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.service.RealTimeService;
import jp.mydns.projectk.safi.value.RequestContext;
import jp.mydns.projectk.safi.entity.listener.EntityFooterUpdater;
import static jp.mydns.projectk.safi.util.CdiUtils.requireResolvable;

/**
 Produce the {@link EntityFooterUpdater.Context}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@RequestScoped
public class EntityFooterContextProducer {

// Note: Obtaining the request scoped CDI bean via Instance.
private final Instance<RequestContext> reqCtxInst;
private final Instance<RealTimeService> realTimeSvcInst;

/**
 Constructor.

 @param reqCtxInst the {@code RequestContext} as {@code Instance}
 @param realTimeSvcInst the {@code RealTimeService} as {@code Instance}
 @throws NullPointerException if any argument is {@code null}
 @since 3.0.0
 */
@Inject
public EntityFooterContextProducer(Instance<RequestContext> reqCtxInst,
    Instance<RealTimeService> realTimeSvcInst) {

    this.reqCtxInst = Objects.requireNonNull(reqCtxInst);
    this.realTimeSvcInst = Objects.requireNonNull(realTimeSvcInst);
}

/**
 Produce the {@code EntityFooterUpdater.Context}.

 @return the {@code EntityFooterUpdater.Context}
 @since 3.0.0
 */
@Produces
@RequestScoped
public EntityFooterUpdater.Context produce() {
    return new Impl();
}

private class Impl implements EntityFooterUpdater.Context {

@Override
public LocalDateTime getUtcNow() {
    return requireResolvable(realTimeSvcInst).getLocalNow();
}

@Override
public String getAccountId() {
    return requireResolvable(reqCtxInst).getAccountId().orElse(null);
}

@Override
public String getProcessName() {
    return requireResolvable(reqCtxInst).getProcessName();
}

/**
 Returns a string representation.

 @return a string representation
 @throws PublishableIllegalStateException if the prerequisite information is not found. This
 exception result from an illegal state due to an implementation bug, and the caller should not
 continue processing.
 @since 3.0.0
 */
@Override
public String toString() {
    return "EntityFooterContext{" + "utcNow=" + getUtcNow() + ", accountId=" + getAccountId()
        + ", processName=" + getProcessName() + '}';
}

}

}
