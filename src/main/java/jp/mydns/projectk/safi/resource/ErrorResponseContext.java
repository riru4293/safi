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

import io.swagger.v3.oas.annotations.media.Schema;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import jp.mydns.projectk.safi.util.CollectionUtils;
import jp.mydns.projectk.safi.value.ValueTemplate;

/**
 Error response information.

 <p>
 Implementation requirements.
 <ul>
 <li>This class is immutable and thread-safe.</li>
 <li>Can be converted to JSON.</li>
 </ul>

 <a href="{@docRoot}/../schemas/error-response-context.schema.json">Json schema is here</a>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface ErrorResponseContext extends ValueTemplate {

/**
 Get JSON schema URI.

 @return the URI of this JSON schema
 @since 3.0.0
 */
@JsonbProperty("$schema")
@Schema(description = "JSON schema.", accessMode = READ_ONLY)
@NotNull
URI getSchema();

/**
 Get error code.

 @return error code
 @since 3.0.0
 */
@Schema(description = "Error code.", accessMode = READ_ONLY)
@NotNull
URI getCode();

/**
 Get error message.

 @return error message
 @since 3.0.0
 */
@Schema(description = "Error message.", accessMode = READ_ONLY)
@NotNull
String getMessage();

/**
 Get error details.

 @return error details
 @since 3.0.0
 */
@Schema(description = "Error details.", requiredMode = NOT_REQUIRED, accessMode = READ_ONLY)
@NotNull
Optional<List<@NotNull JsonObject>> getDetails();

/**
 Builder of the {@code ErrorResponseContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
class Builder extends ValueTemplate.AbstractBuilder<Builder, ErrorResponseContext> {

private URI code;
private String message;
private List<JsonObject> details;

/**
 Constructor.

 @since 3.0.0
 */
public Builder() {
    super(Builder.class);
}

/**
 Set error code.

 @param code error code
 @return updated this
 @since 3.0.0
 */
public Builder withCode(URI code) {
    this.code = code;
    return this;
}

/**
 Set error message.

 @param message error message
 @return updated this
 @since 3.0.0
 */
public Builder withMessage(String message) {
    this.message = message;
    return this;
}

/**
 Set error details.

 @param details error details
 @return updated this
 @since 3.0.0
 */
public Builder withDetails(List<JsonObject> details) {
    this.details = CollectionUtils.toUnmodifiable(details);
    return this;
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public ErrorResponseContext unsafeBuild() {
    return new Bean(this);
}

private class Bean implements ErrorResponseContext {

private final URI schema = URI.create(
    "https://project-k.mydns.jp/safi/schemas/error-response-context.schema.json");
private final URI code;
private final String message;
private final List<JsonObject> details;

public Bean(Builder builder) {
    this.code = builder.code;
    this.message = builder.message;
    this.details = builder.details;
}

@Override
public URI getSchema() {
    return schema;
}

@Override
public URI getCode() {
    return code;
}

@Override
public String getMessage() {
    return message;
}

@Override
public Optional<List<JsonObject>> getDetails() {
    return Optional.ofNullable(details);
}

@Override
public String toString() {
    return "ErrorResponseContext{" + "schema=" + schema + ", code=" + code + ", message=" + message
        + ", details=" + details + '}';
}

}

}

}
