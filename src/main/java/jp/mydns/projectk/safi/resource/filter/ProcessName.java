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

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Inject;
import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import static java.util.function.Predicate.not;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import jp.mydns.projectk.safi.value.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define process name. The process name is mainly used for the purpose of being recorded in the footer of the database
 * table.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ProcessName {

    /**
     * Process name.
     *
     * @return process name
     * @since 3.0.0
     */
    @Nonbinding
    String value() default "";

    /**
     * Extract process name from resource method's annotation and stores original URI only during this request.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface Extractor {

        /**
         * Extract process name and set it to the {@code RequestContextProducer}.
         *
         * @param crc the {@code ContainerRequestContext}. It no use.
         * @since 3.0.0
         */
        void filter(ContainerRequestContext crc);

        /**
         * Implements of the {@code Extractor}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        @RequestScoped
        @Provider
        @Priority(Priorities.USER)
        class Impl implements ContainerRequestFilter, Extractor {

            private static final Logger log = LoggerFactory.getLogger(Impl.class);

            // Note: It is CDI Bean.
            private ProcessNameContextImpl ctx;

            // Note: It is JAX-RS context.
            private ResourceInfo resInf;

            @Inject
            @SuppressWarnings("unused")
            void setCtx(ProcessNameContextImpl ctx) {
                this.ctx = ctx;
            }

            @Context
            @SuppressWarnings("unused")
            void setResInf(ResourceInfo resInf) {
                this.resInf = resInf;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            @ProcessName
            public void filter(ContainerRequestContext crc) {
                Optional.of(resInf).map(ResourceInfo::getResourceMethod).map(m -> m.getAnnotation(ProcessName.class))
                    .map(ProcessName::value).filter(not(String::isBlank))
                    .ifPresent(c(ctx::setValue).andThen(n -> log.debug("Process name is {}.", n)));
            }

            @RequestScoped
            static class ProcessNameContextImpl implements RequestContext.ProcessNameContext {

                private String value;

                /**
                 * {@inheritDoc}
                 *
                 * @since 3.0.0
                 */
                @Override
                public String getValue() {
                    return value;
                }

                void setValue(String value) {
                    this.value = value;
                }

                /**
                 * Returns a string representation.
                 *
                 * @return a string representation
                 * @since 3.0.0
                 */
                @Override
                public String toString() {
                    return String.valueOf(value);
                }
            }
        }
    }
}
