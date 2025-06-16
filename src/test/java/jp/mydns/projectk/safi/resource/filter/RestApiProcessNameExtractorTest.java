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

import jakarta.ws.rs.container.ResourceInfo;
import java.lang.reflect.Method;
import jp.mydns.projectk.safi.resource.RestApiProcessName;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 Test of class {@code RestApiProcessNameExtractor}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class RestApiProcessNameExtractorTest {

/**
 Test of filter method.

 @param inf the {@code ResourceInfo}. It provides by Mockito.
 @throws NoSuchMethodException if failed test.
 @since 3.0.0
 */
@Test
void testFilter(@Mock ResourceInfo inf) throws NoSuchMethodException {

    // [Setup mocks] InvocationContext.
    Method m = this.getClass().getDeclaredMethod("stubMethod");
    doReturn(m).when(inf).getResourceMethod();

    // [Pre-Exec] Create an instance that target for testing.
    var instance = new RestApiProcessNameExtractor.Impl();
    var ctx = new RestApiProcessNameExtractor.Impl.ContextImpl();
    instance.setCtx(ctx);
    instance.setResInf(inf);

    // [Execute] Extract a REST api process name.
    instance.filter(null/* not use */);

    // [Verify]
    assertThat(ctx).returns("test-api-name", RestApiProcessNameExtractor.Impl.ContextImpl::getValue)
        .hasToString("RestApiProcessNameContext{value=test-api-name}");
}

@RestApiProcessName("test-api-name")
@SuppressWarnings("unused")
void stubMethod() {
    // For get a Method instance.
}

}
