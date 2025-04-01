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
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import jakarta.ws.rs.core.UriInfo;
import jp.mydns.projectk.safi.resource.filter.ProcessName;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.value.JobValue;
import jp.mydns.projectk.safi.value.JobdefValue;
import jp.mydns.projectk.safi.value.SchedefValue;

/**
 * JAX-RS resource for test.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
@Path("tests")
public class TestResource {

    @Inject
    private EntityManager em;

    @Context
    private UriInfo uriInfo;

    @Inject private ConfigService configSvc;

    /**
     * API communication check.
     *
     * @return response message
     * @since 3.0.0
     */
    @GET
    @Path("ping")
    @Produces(TEXT_PLAIN)
    @ProcessName("ping")
    public String ping() {
        uriInfo.getAbsolutePath();
        return "Hello SAFI API.";
    }

    @GET
    @Path("jobdef")
    @Produces(APPLICATION_JSON)
    public JobdefValue getJobdef() {
        return null;
    }

    @GET
    @Path("schedef")
    @Produces(APPLICATION_JSON)
    public SchedefValue getSchedef() {
        return null;
    }

    @GET
    @Path("job")
    @Produces(APPLICATION_JSON)
    public JobValue getJob() {
        return null;
    }

    @GET
    @Path("p")
    @Produces(TEXT_PLAIN)
    public String getPath() {
        return configSvc.getPluginDir().toString();
    }
}
