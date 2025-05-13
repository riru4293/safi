/*
 * Copyright (c) 2025, ProjectK
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
package jp.mydns.projectk.safi.test;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.util.TypeLiteral;
import jp.mydns.projectk.safi.value.RequestContext;
import org.jboss.weld.junit.MockBean;
import static org.mockito.Mockito.mock;

/**
 * Provides the {@code RequestContext} as CDI bean for testing.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class RequestContextProvider {

    private final RequestContext mock;
    private final Bean<?> bean;

    /**
     * Constructor.
     *
     * @since 3.0.0
     */
    public RequestContextProvider() {
        mock = mock(RequestContext.class);
        bean = MockBean.builder().types(new TypeLiteral<RequestContext>() {
            @java.io.Serial
            private static final long serialVersionUID = 1L;
        }.getType()).creating(mock).build();
    }

    /**
     * Get the mock by Mockito.
     *
     * @return mock
     * @since 3.0.0
     */
    public RequestContext getMock() {
        return mock;
    }

    /**
     * Get the CDI bean.
     *
     * @return the CDI bean
     * @since 3.0.0
     */
    public Bean<?> getBean() {
        return bean;
    }
}
