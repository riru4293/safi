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

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jp.mydns.projectk.safi.value.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface LanguageResponseFilter {

/**
 Set {@code Content-Language} and also set {@code Vary: Accept-Language} to indicate that the
 response will vary for each language.

 @param cReqCtx the {@code ContainerRequestContext}
 @param cResCtx the {@code ContainerResponseContext}
 @since 3.0.0
 */
void filter(ContainerRequestContext cReqCtx, ContainerResponseContext cResCtx);

/**
 Implements of the {@code LanguageResponseFilter}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(LanguageResponseFilter.class)
@RequestScoped
@Provider
class Impl implements ContainerResponseFilter, LanguageResponseFilter {

private static final Logger log = LoggerFactory.getLogger(Impl.class);

// Note: It is CDI Bean.
private RequestContext ctx;

@SuppressWarnings("unused")
Impl() {
}

@Inject
@SuppressWarnings("unused")
void setContext(RequestContext ctx) {
    this.ctx = ctx;
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    MultivaluedMap<String, Object> headers = responseContext.getHeaders();

    String lang = ctx.getLocale().toLanguageTag();

    headers.add(HttpHeaders.VARY, HttpHeaders.ACCEPT_LANGUAGE);
    headers.add(HttpHeaders.CONTENT_LANGUAGE, lang);

    log.debug("Content language is {}.", lang);
}

}

}
