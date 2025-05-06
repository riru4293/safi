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
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import static java.util.function.Predicate.not;
import jp.mydns.projectk.safi.batch.BatchProcessName;
import jp.mydns.projectk.safi.exception.PublishableIllegalStateException;
import static jp.mydns.projectk.safi.util.CdiUtils.requireResolvable;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import jp.mydns.projectk.safi.value.RequestContext;
import static jp.mydns.projectk.safi.util.LambdaUtils.f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Extract process name from the {@link BatchProcessName} annotation and stores to the
 {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@BatchProcessName
public class BatchProcessNameExtractor {

private static final Logger log = LoggerFactory.getLogger(BatchProcessNameExtractor.class);

// Note: It is CDI Bean. Obtaining the request scoped CDI bean via Instance.
private Instance<ContextImpl> ctxInst;

/**
 Inject the {@code ContextImpl} as {@code Instance}.

 @param ctxInst the {@code ContextImpl} as {@code Instance}
 @since 3.0.0
 */
@Inject
@SuppressWarnings("unused")
void setCtxInst(Instance<ContextImpl> ctxInst) {
    this.ctxInst = ctxInst;
}

/**
 Extract and store the batch process name.

 @param ic the {@code InvocationContext}
 @return the return value of the intercepted method.
 @throws PublishableIllegalStateException if the prerequisite information is not found. This
 exception result from an illegal state due to an implementation bug, and the caller should not
 continue processing.
 @throws Exception the exception thrown by the intercepted method.
 @since 3.0.0
 */
@AroundInvoke
public Object invoke(InvocationContext ic) throws Exception {

    Optional.ofNullable(ic.getMethod())
        .map(f(Method::getAnnotation, BatchProcessName.class))
        .map(BatchProcessName::value)
        .filter(not(String::isBlank))
        .ifPresent(c(requireResolvable(ctxInst)::setValue)
            .andThen(n -> log.debug("Process name is {}.", n)));

    return ic.proceed();
}

@RequestScoped
static class ContextImpl implements RequestContext.BatchProcessNameContext {

private String value = null;

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public String getValue() {
    return value;
}

void setValue(String value) {
    this.value = value;
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public boolean isAvailable() {
    return value != null;
}

/**
 Returns a string representation.

 @return a string representation
 @since 3.0.0
 */
@Override
public String toString() {
    return String.valueOf(value);
}

}

}
