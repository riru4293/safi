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
import java.util.Locale;
import java.util.Objects;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import jp.mydns.projectk.safi.value.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Extract Accept-Language from HTTP header and stores to the {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface LocaleExtractor {

/**
 Extract and store the locale. If multiple locales are specified, the first one is used.

 @param crc the {@code ContainerRequestContext}.
 @since 3.0.0
 */
void filter(ContainerRequestContext crc);

/**
 Implements of the {@code LocaleExtractor}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(LocaleExtractor.class)
@RequestScoped
@Provider
@Priority(Priorities.AUTHENTICATION - 500)
class Impl implements ContainerRequestFilter, LocaleExtractor {

private static final Logger log = LoggerFactory.getLogger(Impl.class);

// Note: It is CDI Bean.
private ContextImpl ctx;

@SuppressWarnings("unused")
Impl() {
}

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
public void filter(ContainerRequestContext crc) {

    crc.getAcceptableLanguages().stream().findFirst()
        .ifPresent(c(ctx::setValue).andThen(v -> log.debug("Locale is {}.", v)));
}

@RequestScoped
static class ContextImpl implements RequestContext.LocaleContext {

private Locale value = Locale.ENGLISH;

@Override
public Locale getValue() {
    return value;
}

void setValue(Locale value) {
    this.value = Objects.requireNonNull(value);
}

@Override
public String toString() {
    return "LocaleContext{" + "value=" + value + '}';
}

}

}

}
