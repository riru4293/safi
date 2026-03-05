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
package jp.mydns.projectk.safi.resource.trial;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jp.mydns.projectk.safi.resource.NeedToAuth;
import jp.mydns.projectk.safi.value.RequestContext;

/**
 Account login authenticator.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface Authenticator {

/**
 Authenticate the account of the HTTP request.

 @param crc the {@code ContainerRequestContext}
 @since 3.0.0
 */
void filter(ContainerRequestContext crc);

/**
 Implements of the {@code Authenticator}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@RequestScoped
@Provider
@Priority(Priorities.AUTHENTICATION)
class Impl implements ContainerRequestFilter, Authenticator {

// Note: It is CDI Bean.
private ContextImpl ctx;

@Inject
@SuppressWarnings("unused")
void setCtx(ContextImpl ctx) {
    this.ctx = ctx;
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
@NeedToAuth
public void filter(ContainerRequestContext crc) {
// ToDo: Require implements of the auth process.
    ctx.setValue("anonymous");
}

@RequestScoped
static class ContextImpl implements RequestContext.AccountIdContext {

private String value = null;

@Override
public String getValue() {
    return value;
}

void setValue(String value) {
    this.value = value;
}

@Override
public String toString() {
    return "AccountIdContext{" + "value=" + value + '}';
}

}

}

}
