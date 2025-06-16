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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import static jakarta.validation.ElementKind.METHOD;
import static jakarta.validation.ElementKind.PARAMETER;
import jakarta.validation.Path;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.status;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static java.util.Spliterator.CONCURRENT;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import jp.mydns.projectk.safi.resource.ErrorResponseContext;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import static jp.mydns.projectk.safi.util.LambdaUtils.f;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Catch the {@code ConstraintViolationException} and creates a <i>400 Bad Request</i> response.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Provider
@ApplicationScoped
public class ViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

private static final String RESOURCE_PKG = "jp.mydns.projectk.safi.resource";
private static final URI CODE = URI.create(
    "https://project-k.mydns.jp/safi/errors/request-violation.html");
private static final String MSG = "A constraint violation was detected in the request.";

private static final Logger log = LoggerFactory.getLogger(ViolationExceptionMapper.class);

private UnexpectedErrorResponseFactory responseFactory;

@Inject
@SuppressWarnings("unused")
void setResponseFactory(UnexpectedErrorResponseFactory responseFactory) {
    this.responseFactory = responseFactory;
}

/**
 Create a <i>400 Bad Request</i> response if a constraint violation occurs in an HTTP request.

 @param ex the {@code ConstraintViolationException}
 @return if the request violates a constraint, it's a <i>400 Bad Request</i> response containing the
 reason for the violation, otherwise it's a <i>500 Internal Error</i> response.
 @throws InternalServerErrorException if a constraint violation occurs in a request other than an
 HTTP request.
 @since 3.0.0
 */
@Override
public Response toResponse(ConstraintViolationException ex) {

    log.debug("Constraint violations were detected.", ex);

    return Optional.ofNullable(ex.getConstraintViolations())
        .filter(this::isBadRequest)
        .map(this::toDetails)
        .map(new ErrorResponseContext.Builder().withCode(CODE).withMessage(MSG)::withDetails)
        .map(ErrorResponseContext.Builder::unsafeBuild)
        .map(f(status(BAD_REQUEST).type(APPLICATION_JSON)::entity).andThen(ResponseBuilder::build))
        .orElseGet(responseFactory::create);
}

private boolean isBadRequest(Set<ConstraintViolation<?>> cvSet) {

    // Note: A BadRequest is only judged as a violation of parameter constraints
    //       and caused class belongs to a package in which the resource is located.
    //       This is to determine whether the constraint violation is the user's responsibility or not.
    return cvSet.stream().parallel()
        .filter(this::isParameterViolation)
        .map(ConstraintViolation::getRootBeanClass)
        .filter(Objects::nonNull)
        .map(f(Package::getName).compose(Class::getPackage))
        .anyMatch(n -> n.startsWith(RESOURCE_PKG));
}

private boolean isParameterViolation(ConstraintViolation<?> cv) {

    // Note: true if a parameter constraint was violated.
    //       Specifically, checks that both METHOD and PARAMETER are present.
    return 2 == stream(spliteratorUnknownSize(cv.getPropertyPath().iterator(), CONCURRENT), true)
        .map(Path.Node::getKind)
        .filter(p(METHOD::equals).or(PARAMETER::equals))
        .distinct().count();
}

private List<JsonObject> toDetails(Set<ConstraintViolation<?>> cvSet) {
    return cvSet.stream().sequential()
        .map(v -> Json.createObjectBuilder()
            .add("path", toJsonPath(v.getPropertyPath()))
            .add("message", v.getMessage()).build())
        .toList();
}

// Note: Only parameter constraint violations are assumed.
private String toJsonPath(Path p) {

    Iterator<Path.Node> nodes = p.iterator();
    StringBuilder sb = new StringBuilder("$");

    for (int i = 0; nodes.hasNext(); i++) {

        Path.Node n = nodes.next();

        switch (i) {
        case 0 -> {
            // Note: Ignore the method name.
        }
        case 1 ->
            // Note: Ignore the argument name. But only extracts an index if JSON array.
            Optional.ofNullable(n.getIndex()).ifPresent(c(".[%d]"::formatted).andThen(sb::append));
        default ->
            // Note: Property name.
            sb.append('.').append(n.toString());
        }
    }

    return sb.toString();
}

}
