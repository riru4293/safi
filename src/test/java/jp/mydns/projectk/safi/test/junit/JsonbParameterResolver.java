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
package jp.mydns.projectk.safi.test.junit;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Resolve the {@link Jsonb} type parameter in JUnit methods.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class JsonbParameterResolver implements ParameterResolver, ExtensionContext.Store.CloseableResource {

    private final Jsonb jsonb = JsonbBuilder.create();

    /**
     * Returns a {@code true} if parameter class is {@code Jsonb}.
     *
     * @param pc the {@code ParameterContext}
     * @param ec the {@code ExtensionContext}
     * @return {@code true} if parameter class is {@code Jsonb}
     * @throws ParameterResolutionException no occurs
     * @since 3.0.0
     */
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) throws ParameterResolutionException {
        return Jsonb.class == pc.getParameter().getType();
    }

    /**
     * Returns a {@code Jsonb} instance.
     *
     * @param pc the {@code ParameterContext}
     * @param ec the {@code ExtensionContext}
     * @return {@code Jsonb}
     * @throws ParameterResolutionException no occurs
     * @since 3.0.0
     */
    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) throws ParameterResolutionException {
        return jsonb;
    }

    /**
     * Close the {@code Jsonb} instance.
     *
     * @throws Exception if occurs any exception
     * @since 3.0.0
     */
    @Override
    public void close() throws Exception {
        jsonb.close();
    }
}
