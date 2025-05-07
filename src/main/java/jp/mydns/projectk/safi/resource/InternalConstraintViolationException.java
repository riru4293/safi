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

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import java.util.Objects;

/**
 A wrapper for {@code ConstraintViolationException} that occurs outside of direct user request data.
 Used to return HTTP Status code 500 instead of 400. {@code ConstraintViolationException} that
 occurs outside of direct requests is due to internal inconsistencies and is not the user's
 responsibility.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public class InternalConstraintViolationException extends ServerErrorException {

@java.io.Serial
private static final long serialVersionUID = 8274928374928374923L;

private final ConstraintViolationException cvex;

/**
 Construct with the {@code ConstraintViolationException}.

 @param cvex the {@code ConstraintViolationException}
 @throws NullPointerException if {@code cvex} is {@code null}
 @since 3.0.0
 */
public InternalConstraintViolationException(ConstraintViolationException cvex) {
    super(Response.Status.INTERNAL_SERVER_ERROR);
    this.cvex = Objects.requireNonNull(cvex);
}

/**
 Get the wrapped {@code ConstraintViolationException}.

 @return the wrapped {@code ConstraintViolationException}
 @since 3.0.0
 */
public ConstraintViolationException getConstraintViolationException() {
    return cvex;
}

}
