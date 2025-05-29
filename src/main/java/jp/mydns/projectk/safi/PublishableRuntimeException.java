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
package jp.mydns.projectk.safi;

import java.util.Objects;

/**
 An {@code RuntimeException} implementation with a message that can be exposed to the consumer.
 This exception has a message for the consumer. This message conveys that the consumer is not
 responsible, that the problem was caused by a flaw in the implementation, and that they should ask
 a maintainer to deal with it. The message should not include information about the internals of the
 implementation, as that is unnecessary for the consumer. Any message or stack trace for the
 maintainer should be provided in the internal {@code Throwable}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public class PublishableRuntimeException extends RuntimeException {

@java.io.Serial
private static final long serialVersionUID = 8274928374928374907L;

/**
 Construct with the {@code Throwable}.

 @param cause the {@code Throwable} for maintainer.
 @throws NullPointerException if {@code cause} is {@code null}
 @since 3.0.0
 */
public PublishableRuntimeException(Throwable cause) {
    super(Objects.requireNonNull(cause));
}

/**
 Get publishable message.

 @return the reason for the exception and how to handle it to users
 @since 3.0.0
 */
@Override
public String getMessage() {
    return "Sorry. Illegal internal configuration. Please contact your system administrator.";
}

}
