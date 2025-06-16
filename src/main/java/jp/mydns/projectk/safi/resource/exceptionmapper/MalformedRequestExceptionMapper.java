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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import java.net.URI;
import jp.mydns.projectk.safi.JakartaRestJsonBinder.MalformedRequestException;
import jp.mydns.projectk.safi.resource.ErrorResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Catch the {@code MalformedRequestException} and creates a <i>400 Bad Request</i> response.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Provider
@ApplicationScoped
public class MalformedRequestExceptionMapper implements ExceptionMapper<MalformedRequestException> {

private static final URI CODE = URI.create(
    "https://project-k.mydns.jp/safi/errors/malformed-request.html");
private static final String MSG = "Request format is malformed.";

private static final Logger log = LoggerFactory.getLogger(MalformedRequestExceptionMapper.class);

/**
 Create a <i>400 Bad Request</i> response.

 @param ex the {@code MalformedRequestException}
 @return a <i>400 Bad Request</i> response
 @since 3.0.0
 */
@Override
public Response toResponse(MalformedRequestException ex) {

    /*
        == Reasons for the log level being at WARNING ==

        There are two reasons for the WARNING level.

        1. The request is invalid and outside the scope of the SAFI's responsibility,
           so an ERROR level is inappropriate. A WARNING level or lower is considered appropriate.

        2. The assumption is that the request is constructed by software. Therefore,
           format errors do not usually occur. A WARNING level or higher is probably appropriate.
     */
    log.warn("Malformed request was detected.", ex);

    return Response.status(BAD_REQUEST).type(APPLICATION_JSON).entity(
        new ErrorResponseContext.Builder().withCode(CODE).withMessage(MSG).unsafeBuild()).build();
}

}
