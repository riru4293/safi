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

import jakarta.validation.Configuration;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Locale;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Resolve the {@link ValidatorFactory} and{@link Validator} type parameter in JUnit methods.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class ValidatorParameterResolver implements ParameterResolver, ExtensionContext.Store.CloseableResource {

    private final ValidatorFactory factory;

    public ValidatorParameterResolver() {
        Configuration<?> conf = Validation.byDefaultProvider().configure();

        var mi = new UsMessageInterpolator(conf.getDefaultMessageInterpolator());

        // Note: Force use US locale.
        this.factory = conf.messageInterpolator(mi).buildValidatorFactory();
    }

    /**
     * Returns a {@code true} if parameter class is {@code ValidatorFactory} or {@code Validator}.
     *
     * @param pc the {@code ParameterContext}
     * @param ec the {@code ExtensionContext}
     * @return {@code true} if parameter class is {@code ValidatorFactory} or {@code Validator}
     * @throws ParameterResolutionException no occurs
     * @since 3.0.0
     */
    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) throws ParameterResolutionException {
        return ValidatorFactory.class == pc.getParameter().getType()
                || Validator.class == pc.getParameter().getType();
    }

    /**
     * Returns a {@code ValidatorFactory} or {@code Validator} instance.
     *
     * @param pc the {@code ParameterContext}
     * @param ec the {@code ExtensionContext}
     * @return {@code ValidatorFactory} or {@code Validator}
     * @throws ParameterResolutionException no occurs
     * @since 3.0.0
     */
    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) throws ParameterResolutionException {

        Class<?> clazz = pc.getParameter().getType();
        
        if(clazz == ValidatorFactory.class) {
            return factory;
        } else if (clazz == Validator.class) {
            return factory.getValidator();
        }
        
        throw new ParameterResolutionException("Impossible.");
    }
    
    /**
     * Close the {@code ValidatorFactory} instance.
     *
     * @throws Exception if occurs any exception
     * @since 3.0.0
     */
    @Override
    public void close() throws Exception {
        factory.close();
    }

    /**
     * Force US locale {@code MessageInterpolator}.
     *
     * @since 3.0.0
     */
    private class UsMessageInterpolator implements MessageInterpolator {

        private final MessageInterpolator origin;

        public UsMessageInterpolator(MessageInterpolator origin) {
            this.origin = origin;
        }

        @Override
        public String interpolate(String tmpl, Context ctx) {
            return interpolate(tmpl, ctx, Locale.US);
        }

        @Override
        public String interpolate(String tmpl, Context ctx, Locale locale) {
            return origin.interpolate(tmpl, ctx, Locale.US);
        }
    }
}
