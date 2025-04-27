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
import jakarta.enterprise.inject.Typed;
import jakarta.ws.rs.Priorities;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.net.URI;
import java.util.Optional;
import jp.mydns.projectk.safi.value.WebApiRequestPath;
import jp.mydns.projectk.safi.producer.WebApiRequestPathProducer;

public interface RequestPathExtractor {

    @Typed(RequestPathExtractor.class)
    @RequestScoped
    @Provider
    @Priority(Priorities.USER)
    class Extractor implements ContainerRequestFilter, RequestPathExtractor {

        private ProducerImpl producer;

        @Inject
        @SuppressWarnings("unused")
        void setProducer(ProducerImpl producer) {
            this.producer = producer;
        }

        @Override
        public void filter(ContainerRequestContext crc) {
            producer.setRequestPath(crc.getUriInfo().getAbsolutePath());
        }

        @RequestScoped
        static class ProducerImpl implements WebApiRequestPathProducer {

            private URI requestPath;

            void setRequestPath(URI requestPath) {
                this.requestPath = requestPath;
            }

            @Produces
            @RequestScoped
            @Override
            public WebApiRequestPath produce() {
                return () -> Optional.ofNullable(requestPath);
            }
        }
    }
}
