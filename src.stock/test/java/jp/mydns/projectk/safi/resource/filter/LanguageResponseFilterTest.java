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

import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jp.mydns.projectk.safi.value.RequestContext;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 Test of class {@code LanguageResponseFilter}

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class LanguageResponseFilterTest {

/**
 Test of filter method.

 @param reqCtx the {@code RequestContext}. It provides by Mockito.
 @param resCtx the {@code ContainerResponseContext}. It provides by Mockito.
 @since 3.0.0
 */
@Test
void testFilter(@Mock RequestContext reqCtx, @Mock ContainerResponseContext resCtx) {

    // [Setup mocks] Locale of the RequestContext.
    doReturn(Locale.CANADA).when(reqCtx).getLocale();

    // [Setup mocks] HTTP headers of the ContainerResponseContext.
    var headers = new MultivaluedHashMap<String, Object>();
    doReturn(headers).when(resCtx).getHeaders();

    // [Pre-Exec] Create an instance that target for testing.
    var instance = new LanguageResponseFilter.Impl();
    instance.setContext(reqCtx);

    // [Execute] Set values to HTTP headers.
    instance.filter(null/* no use */, resCtx);

    // [Verify] HTTP header values.
    assertThat(headers).containsAllEntriesOf(Map.of("Vary", List.of("Accept-Language"),
        "Content-Language", List.of(Locale.CANADA.toLanguageTag())));
}

}
