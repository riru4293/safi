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

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.json.Json;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import static jakarta.validation.ElementKind.METHOD;
import static jakarta.validation.ElementKind.PARAMETER;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static jakarta.ws.rs.core.Response.status;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static java.util.Spliterator.CONCURRENT;
import static java.util.Spliterators.spliteratorUnknownSize;
import java.util.function.Supplier;
import static java.util.stream.StreamSupport.stream;

import jp.mydns.projectk.safi.resource.ErrorResponseContext;
import static jp.mydns.projectk.safi.util.LambdaUtils.f;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**

 @author riru
 */
@Provider
public class ViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {


private static final URI CODE = URI.create("https://project-k.mydns.jp/safi/errors/trial.html");
private static final String MESSAGE = "ggrks";

private static final ErrorResponseContext XXX = new ErrorResponseContext.Builder()
    .withMessage("ばぐっすわ。")
    .unsafeBuild();

private static final Supplier<Response> INTERNAL_ERR_RES
    = status(INTERNAL_SERVER_ERROR).type(APPLICATION_JSON).entity(XXX)::build;

private static final Logger log = LoggerFactory.getLogger(ViolationExceptionMapper.class);

/**

 @param ex
 @return
 */
@Override
public Response toResponse(ConstraintViolationException ex) {

    log.debug("Constraint violations were detected.", ex);

    return Optional.ofNullable(ex.getConstraintViolations())
        .filter(this::isBadRequest)
        .map(cvSet -> new ErrorResponseContext.Builder().withCode(CODE).withMessage(MESSAGE)
            .withDetails(cvSet.stream().sequential()
                .map(v -> Json.createObjectBuilder()
                    .add("path", v.getPropertyPath().toString())
                    .add("message", v.getMessage()).build())
                .toList()).unsafeBuild()
        )
        .map(f(status(BAD_REQUEST).type(APPLICATION_JSON)::entity).andThen(ResponseBuilder::build))
        .orElseGet(INTERNAL_ERR_RES);
}

private boolean isBadRequest(Set<ConstraintViolation<?>> cvSet) {

    return cvSet.stream().parallel()
        .filter(this::isParameterViolation)
        .map(ConstraintViolation::getRootBeanClass).filter(Objects::nonNull)
        .map(Class::getPackage).map(Package::getName)
        .anyMatch(n -> n.startsWith("jp.mydns.projectk.safi.resource"));
}

private boolean isParameterViolation(ConstraintViolation<?> cv) {

    // Note: true if a parameter constraint was violated.
    //       Specifically, checks that both METHOD and PARAMETER are present.
    return 2 == stream(spliteratorUnknownSize(cv.getPropertyPath().iterator(), CONCURRENT), true)
        .map(Path.Node::getKind)
        .filter(p(METHOD::equals).or(PARAMETER::equals))
        .distinct().count();
}

}
