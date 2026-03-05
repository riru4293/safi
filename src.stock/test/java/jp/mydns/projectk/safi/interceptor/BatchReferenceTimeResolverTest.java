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

import jakarta.inject.Provider;
import jakarta.interceptor.InvocationContext;
import java.time.LocalDateTime;
import jp.mydns.projectk.safi.service.TimeService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 Test of class {@code BatchReferenceTimeResolver}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class BatchReferenceTimeResolverTest {

/**
 Test of invoke method.

 @param ctxPvd the {@code Provider}. It provides by Mockito.
 @param timeSvc the {@code TimeService}. It provides by Mockito.
 @param ic the {@code InvocationContext}. It provides by Mockito.
 @throws Exception if failed test
 @since 3.0.0
 */
@Test
void testInvoke(@Mock Provider<BatchReferenceTimeResolver.Impl.ContextImpl> ctxPvd,
    @Mock TimeService timeSvc, @Mock InvocationContext ic) throws Exception {

    // [Setup mocks] Context provider.
    var ctx = new BatchReferenceTimeResolver.Impl.ContextImpl();
    doReturn(ctx).when(ctxPvd).get();

    // [Setup mocks] TimeService.
    doReturn(LocalDateTime.of(2000,1,1,0,0)).when(timeSvc).getSafiTime();

    // [Pre-Exec] Create an instance that target for testing.
    var instance = new BatchReferenceTimeResolver.Impl(ctxPvd, timeSvc);

    // [Execute] Extract a batch process name.
    instance.invoke(ic);

    // [Verify]
    assertThat(ctx)
        .returns(LocalDateTime.of(2000,1,1,0,0), BatchReferenceTimeResolver.Impl.ContextImpl::getValue)
        .hasToString("BatchReferenceTimeContext{value=2000-01-01T00:00}");
}

}
