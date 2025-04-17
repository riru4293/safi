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
package jp.mydns.projectk.safi.resource.exceptionmapper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;

import jp.mydns.projectk.safi.resource.ErrorResponseContext;

/**
 *
 * @author riru
 */
public interface ConstraintViolationExceptionMapper extends ExceptionMapper<ConstraintViolationException> {

    @Typed(ConstraintViolationExceptionMapper.class)
    @Provider
    @RequestScoped
    class Impl implements ConstraintViolationExceptionMapper {

        private static final URI CODE = URI.create("https://www.google.com");
        private static final String MESSAGE = "ggrks";
        
        private final Validator validator;
        private final UriInfo uriInfo;

        @Inject
        protected Impl(UriInfo uriInfo, Validator validator) {
            this.uriInfo = uriInfo;
            this.validator = validator;
        }
        
        

        /**
         *
         * @param ex
         * @return
         */
        @Override
        public Response toResponse(ConstraintViolationException ex) {

            var builder = new ErrorResponseContext.Builder();

            builder.withCode(CODE).withMessage(MESSAGE).withContextRoot(uriInfo.getBaseUri());

            for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                Json.createObjectBuilder()
                    .add("path", violation.getPropertyPath().toString())
                    .add("message", violation.getMessage());
            }


            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
                .entity(builder.build(validator))
                .build();
        }
    }
    
    
}
