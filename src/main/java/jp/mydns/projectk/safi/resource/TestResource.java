/*
 * Copyright (c) 2024, Project-K
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.IOException;
import jp.mydns.projectk.safi.plugin.ContentSourceReaderPlugin;
import jp.mydns.projectk.safi.service.ContentSourceService;
import jp.mydns.projectk.safi.service.proc.ContentSourceReader;
import jp.mydns.projectk.safi.value.PluginConfig;

/**
 * Provides web API for testing.
 *
 * @author riru
 * @version 2.0.0
 * @since 2.0.0
 */
@RequestScoped
@Path("tests")
public class TestResource {

    @Inject
    private ContentSourceService contentSrcSvc;

    /**
     * Get Content-Source collection.
     *
     * @param contentSourceName Content-Source name
     * @return Content-Source collection
     * @throws NotFoundException if not found resource
     * @throws InternalServerErrorException if occurs I/O exception or interrupted thread
     */
    @GET
    @Path("content-sources/{contentSourceName}")
    @Produces(APPLICATION_JSON)
    @Operation(tags = {"test"}, summary = "Get Content-Source collection.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation."),
                @ApiResponse(responseCode = "404", description = "If not found resource.")})
    public Response getContentSources(
            @Parameter(name = "contentSourceName", description = "Content-Source name")
            @PathParam("contentSourceName") @NotBlank String contentSourceName) {

        java.nio.file.Path pluginWrk = java.nio.file.Path.of("/", "tmp");
        java.nio.file.Path readerWrk = java.nio.file.Path.of("/", "tmp");

        var plgConf = new PluginConfig() {
            @Override
            public String getName() {
                return contentSourceName;
            }

            @Override
            public JsonObject getProperties() {
                return JsonValue.EMPTY_JSON_OBJECT;
            }
        };

        ContentSourceReaderPlugin plugin = contentSrcSvc.newContentSourceReaderPlugin(pluginWrk, plgConf);
        ContentSourceReader reader = contentSrcSvc.newContentSourceReader(readerWrk, plugin);

        StreamingOutput result = out -> {
            try (var g = Json.createGenerator(out); var c = reader.fetch();) {
                g.writeStartArray();
                c.forEachOrdered(m -> {
                    g.writeStartObject();
                    m.entrySet().forEach(e -> g.write(e.getKey(), e.getValue()));
                    g.writeEnd();
                });
                g.writeEnd();
            } catch (IOException | InterruptedException ex) {
                throw new InternalServerErrorException(ex);
            }
        };

        return Response.ok(result).build();
    }
}
