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
package jp.mydns.projectk.safi.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.HttpHeaders.LOCATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Objects;
import jp.mydns.projectk.safi.PublishableIllegalStateException;
import jp.mydns.projectk.safi.service.JobdefService;
import jp.mydns.projectk.safi.service.JobService;
import jp.mydns.projectk.safi.value.JobCreationContext;
import jp.mydns.projectk.safi.value.JobCreationRequest;
import jp.mydns.projectk.safi.value.JobValue;
import jp.mydns.projectk.safi.value.RequestContext;

/**
 JAX-RS resource for <i>Job</i>.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JobResource {

/**
 Creates a new job. Used to manually schedule job execution.

 @param req the {@code JobCreationRequest}
 @return created job
 @throws ConstraintViolationException if {@code req} is not valid
 @throws BadRequestException if not found valid job definition
 @throws PersistenceException if failed database operation
 @since 3.0.0
 */
Response createJob(@NotNull @Valid JobCreationRequest req);

/**
 JAX-RS resource for <i>Job</i>.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(JobResource.class)
@RequestScoped
@Path("jobs")
class Impl implements JobResource {

private final JobdefService jobdefSvc;
private final JobService jobSvc;
private final RequestContext reqCtx;

@Inject
@SuppressWarnings("unused")
Impl(JobdefService jobdefSvc, JobService jobSvc, RequestContext reqCtx) {
    this.jobdefSvc = Objects.requireNonNull(jobdefSvc);
    this.jobSvc = Objects.requireNonNull(jobSvc);
    this.reqCtx = Objects.requireNonNull(reqCtx);
}

/**
 {@inheritDoc}

 @throws ConstraintViolationException if {@code req} is not valid
 @throws BadRequestException if not found valid job definition
 @throws PersistenceException if failed database operation
 @throws PublishableIllegalStateException if an exception occurs due to an implementation bug or
 data inconsistency.
 @since 3.0.0
 */
@Override
@POST
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RestApiProcessName("CreateJob")
@Operation(tags = {"jobs"}, summary = "Create a new job. Used to create a job manualy.",
           requestBody = @RequestBody(content = @Content(schema = @Schema(implementation
               = JobCreationRequest.class))),
           responses = {
               @ApiResponse(responseCode = "201", description = "Successful operation.",
                            headers = @Header(name = LOCATION, description = "Created job.",
                                              schema = @Schema(type = "string", format = "uri")),
                            content = @Content(schema = @Schema(implementation = JobValue.class))),
               @ApiResponse(responseCode = "400", description = "If not found job definition."
                            + " Additionally, there may be violations of required value constraints.",
                            content = @Content(schema = @Schema(
                                implementation = ErrorResponseContext.class)))})
public Response createJob(@NotNull @Valid JobCreationRequest req) {

    final JobCreationContext ctx;

    try {
        ctx = jobdefSvc.buildJobCreationContext(req);
    } catch (JobdefService.JobdefIOException ex) {
        throw new BadRequestException(ex);
    }

    JobValue job = jobSvc.createJob(ctx);
    URI location = reqCtx.getRestApiPath().resolve(job.getId());

    return Response.created(location).entity(job).build();

}

}

}
