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
package jp.mydns.projectk.safi.interceptor;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.Optional;
import static java.util.function.Predicate.not;
import jp.mydns.projectk.safi.batch.BatchProcessName;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import jp.mydns.projectk.safi.value.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Extract process name from the {@link BatchProcessName} annotation and stores to the
 {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface BatchProcessNameExtractor {

/**
 Implements of the {@code BatchProcessNameExtractor}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@BatchProcessName
class Impl implements BatchProcessNameExtractor {

private static final Logger log = LoggerFactory.getLogger(Impl.class);

// Note: It is CDI Bean. Obtaining the request scoped CDI bean via Provider.
private final Provider<ContextImpl> ctxPvd;

@Inject
@SuppressWarnings("unused")
Impl(Provider<ContextImpl> ctxPvd) {
    this.ctxPvd = ctxPvd;
}

/**
 Extract and store the batch process name.

 @param ic the {@code InvocationContext}
 @return the return value of the intercepted method. processing.
 @throws Exception the exception thrown by the intercepted method.
 @since 3.0.0
 */
@AroundInvoke
public Object invoke(InvocationContext ic) throws Exception {

    Optional.ofNullable(ic.getMethod())
        .map(m -> m.getAnnotation(BatchProcessName.class))
        .map(BatchProcessName::value)
        .filter(not(String::isBlank))
        .ifPresent(c(ctxPvd.get()::setValue)
            .andThen(n -> log.debug("Process name is {}.", n)));

    return ic.proceed();
}

@RequestScoped
static class ContextImpl implements RequestContext.BatchProcessNameContext {

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
    return "BatchProcessNameContext{" + "value=" + value + '}';
}

}

}

}
