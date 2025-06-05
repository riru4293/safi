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
package jp.mydns.projectk.safi.validator;

import jakarta.inject.Provider;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.MessageInterpolator.Context;
import jakarta.validation.Validation;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import jp.mydns.projectk.safi.util.CdiUtils;
import jp.mydns.projectk.safi.value.RequestContext;

/**
 Localizer for validation message.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public class LocalizedMessageInterpolator implements MessageInterpolator {

private final MessageInterpolator delegate;
private final AtomicReference<Provider<RequestContext>> cachedReqCtxPvd = new AtomicReference<>();

/**
 Construct with default the {@link MessageInterpolator}. It made by
 {@code jakarta.validation.Validation.byDefaultProvider().configure().getDefaultMessageInterpolator()}

 @since 3.0.0
 */
public LocalizedMessageInterpolator() {
    this.delegate = Validation.byDefaultProvider().configure().getDefaultMessageInterpolator();
}

/**
 Interpolates a message template based on the constraint validation context. The locale uses the
 value provided by the {@link RequestContext}.

 @param messageTemplate the message to interpolate context.
 @param context contextual information related to the interpolation.
 @return interpolated error message
 */
@Override
public String interpolate(String messageTemplate, Context context) {
    return interpolate(messageTemplate, context, resolveLocale());
}

/**
 {@inheritDoc}
 */
@Override
public String interpolate(String messageTemplate, Context context, Locale locale) {
    return delegate.interpolate(messageTemplate, context, locale);
}

private Locale resolveLocale() {

    // Retrieves the cached provider, or resolves the provider if it is not cached.
    Provider<RequestContext> pvd = cachedReqCtxPvd.updateAndGet(
        p -> p != null ? p : resolveRequestContext());

    // Returns the locale obtained from the provider, or the default locale if not available.
    return Optional.ofNullable(pvd)
        .map(Provider::get)
        .map(RequestContext::getLocale)
        .orElse(Locale.ENGLISH);
}

private Provider<RequestContext> resolveRequestContext() {
    try {
        return CdiUtils.getInstance(RequestContext.class);
    } catch (RuntimeException ex) {
        return null;
    }
}

}
