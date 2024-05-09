/*
 * Copyright (c) 2024, Project-K
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

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.Optional;
import static java.util.function.Predicate.not;
import jp.mydns.projectk.safi.producer.RequestContextProducer;
import jp.mydns.projectk.safi.value.RequestContext;

/**
 * Authenticator of system processes. After authentication, construct {@link RequestContext}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 * @see SystemProcess
 * @see ProcessName
 */
@Interceptor
@SystemProcess
public class SystemProcessAuthenticator {

    @Inject
    private RequestContextProducer reqCtxPrd;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected SystemProcessAuthenticator() {
    }

    /**
     * Extract the process name from {@link ProcessName} and set it to {@link RequestContext}.
     *
     * @param ic the {@code InvocationContext}
     * @return return value of the {@link InvocationContext#proceed()} method
     * @throws IllegalStateException if not found valid process name, or if the status of {@code RequestContext} class
     * is illegal, such as when a value has already been set in the {@code RequestContext} class.
     * @throws Exception if an exception occurs inside the intercepted process
     * @since 1.0.0
     */
    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {

        String processName = Optional.ofNullable(ic.getMethod()).map(m -> m.getAnnotation(ProcessName.class))
                .map(ProcessName::value).filter(not(String::isBlank))
                .orElseThrow(() -> new IllegalStateException("No found valid process name."));

        reqCtxPrd.setup(processName);

        return ic.proceed();

    }
}
