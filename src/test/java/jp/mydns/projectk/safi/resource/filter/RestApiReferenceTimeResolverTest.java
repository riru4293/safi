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

import java.time.LocalDateTime;
import jp.mydns.projectk.safi.service.TimeService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 Test of class {@code RestApiReferenceTimeResolver}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class RestApiReferenceTimeResolverTest {

/**
 Test of filter method.

 @param timeSvc the {@code TimeService}. It provides by Mockito.
 @since 3.0.0
 */
@Test
void testInvoke(@Mock TimeService timeSvc) {

    // [Setup mocks] Context provider.
    var ctx = new RestApiReferenceTimeResolver.Impl.ContextImpl();

    // [Setup mocks] TimeService.
    doReturn(LocalDateTime.of(2000, 1, 1, 0, 0)).when(timeSvc).getSafiTime();

    // [Pre-Exec] Create an instance that target for testing.
    var instance = new RestApiReferenceTimeResolver.Impl();
    instance.setCtx(ctx);
    instance.setTimeService(timeSvc);

    // [Execute] Extract a REST API process name.
    instance.filter(null /* no use */);

    // [Verify]
    assertThat(ctx).returns(LocalDateTime.of(2000, 1, 1, 0, 0),
        RestApiReferenceTimeResolver.Impl.ContextImpl::getValue)
        .hasToString("RestApiReferenceTimeContext{value=2000-01-01T00:00}");
}

}
