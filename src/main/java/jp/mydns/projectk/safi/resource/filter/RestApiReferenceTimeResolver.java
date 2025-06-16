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
package jp.mydns.projectk.safi.resource.filter;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import jp.mydns.projectk.safi.resource.RestApiProcessName;
import jp.mydns.projectk.safi.service.TimeService;
import jp.mydns.projectk.safi.value.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Resolve a reference time per HTTP request and stores to the {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface RestApiReferenceTimeResolver {

/**
 Resolve and store a reference time per HTTP request.

 @param crc the {@code ContainerRequestContext}. It no use.
 @since 3.0.0
 */
void filter(ContainerRequestContext crc);

/**
 Implements of the {@code RestApiReferenceTimeResolver}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(RestApiReferenceTimeResolver.class)
@RequestScoped
@Provider
@Priority(Priorities.AUTHENTICATION - 100)
class Impl implements ContainerRequestFilter, RestApiReferenceTimeResolver {

private static final Logger log = LoggerFactory.getLogger(Impl.class);

// Note: It is CDI Bean.
private ContextImpl ctx;
private TimeService timeSvc;

@SuppressWarnings("unused")
Impl() {
}

@Inject
@SuppressWarnings("unused")
void setCtx(ContextImpl ctx) {
    this.ctx = ctx;
}

@Inject
@SuppressWarnings("unused")
void setTimeService(TimeService timeSvc) {
    this.timeSvc = timeSvc;
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
@RestApiProcessName
public void filter(ContainerRequestContext crc) {

    LocalDateTime refTime = timeSvc.getSafiTime();

    ctx.setValue(refTime);

    log.debug("Reference time is {}.", refTime);
}

@RequestScoped
static class ContextImpl implements RequestContext.RestApiReferenceTimeContext {

private LocalDateTime value = null;

@Override
public LocalDateTime getValue() {
    return value;
}

void setValue(LocalDateTime value) {
    this.value = value;
}

@Override
public String toString() {
    return "RestApiReferenceTimeContext{" + "value=" + value + '}';
}

}

}

}
