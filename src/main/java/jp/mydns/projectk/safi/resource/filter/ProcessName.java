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
import jakarta.enterprise.context.Dependent;
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
import java.lang.reflect.Method;
import java.util.Optional;
import static java.util.function.Predicate.not;
import jp.mydns.projectk.safi.producer.RequestContextProducer;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
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

    @Nonbinding
    String value() default "";

    /**
     * Extract process name from resource method's annotation and stores original URI only during this request.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Provider
    @Priority(Priorities.USER)
    @Dependent
    class Extractor implements ContainerRequestFilter {

        private static final Logger log = LoggerFactory.getLogger(Extractor.class);

        private final RequestContextProducer reqCtxProducer;

        @Context
        private ResourceInfo resourceInfo;

        /**
         * Constructor.
         *
         * @param reqCtxProducer the {@code RequestContextProducer}
         * @since 3.0.0
         */
        @Inject
        public Extractor(RequestContextProducer reqCtxProducer) {
            this.reqCtxProducer = reqCtxProducer;
        }

        /**
         * Extract process name and set it to the {@code RequestContextProducer}.
         *
         * @param crc the {@code ContainerRequestContext}
         * @since 3.0.0
         */
        @Override
        @ProcessName
        public void filter(ContainerRequestContext crc) {
            Optional.of(resourceInfo).map(ResourceInfo::getResourceMethod).map(this::extract)
                .map(ProcessName::value).filter(not(String::isBlank))
                .ifPresent(c(reqCtxProducer::setProcessName)
                    .andThen(n -> log.debug("Process name is {}.", n)));
        }

        private ProcessName extract(Method m) {
            return m.getAnnotation(ProcessName.class);
        }
    }
}
