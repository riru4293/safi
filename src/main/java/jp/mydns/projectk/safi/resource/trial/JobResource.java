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
package jp.mydns.projectk.safi.resource.trial;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jp.mydns.projectk.safi.resource.filter.ProcessName;
import jp.mydns.projectk.safi.service.JobdefService;
import jp.mydns.projectk.safi.value.JobCreationContext;
import jp.mydns.projectk.safi.value.JobdefValue;

/**
 * JAX-RS resource for <i>Job</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface JobResource {

    /**
     * Create new job. Used to manually schedule job execution.
     *
     * @param ctx the {@code JobCreationContext}
     * @return created job
     * @since 3.0.0
     */
    public Response createJob(@NotNull @Valid JobCreationContext ctx);

    /**
     * JAX-RS resource for <i>Job</i>.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Typed(JobResource.class)
    @RequestScoped
    @Path("jobs")
    class Impl implements JobResource {

        private final JobdefService jobdefSvc;

        @Context
        private UriInfo uriInfo;

        /**
         * Constructor.
         *
         * @param jobdefSvc the {@code JobdefService}
         * @since 3.0.0
         */
        @Inject
        public Impl(JobdefService jobdefSvc) {
            this.jobdefSvc = jobdefSvc;
        }

        /**
         * Create new job. Used to manually schedule job execution.
         *
         * @param ctx the {@code JobCreationContext}
         * @return created job
         * @since 3.0.0
         */
        @Path("")
        @POST
        @Consumes(APPLICATION_JSON)
        @Produces(APPLICATION_JSON)
        @ProcessName("CreateJob")
        @Override
        public Response createJob(@NotNull @Valid JobCreationContext ctx) {

            final JobdefValue jobdef;
            try {
                jobdef = jobdefSvc.buildJobdef(ctx);
            } catch (JobdefService.JobdefIOException ex) {
                throw new BadRequestException(ex);
            }
//
//        Job job = jobSvc.create(jobdef, ctx);
//
//        URI location = uriInfo.getAbsolutePathBuilder().path(job.getId()).build();
//
//        return Response.created(location).entity(job).build();
            return Response.ok().build();
        }
    }
}
