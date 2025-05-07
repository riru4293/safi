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
package jp.mydns.projectk.safi.value;

import java.net.URI;
import jp.mydns.projectk.safi.exception.PublishableIllegalStateException;

/**
 Current request information. A <i>Request</i> is a processing request, and there are processing
 requests via Web API and batch processing requests by the <i>SAFI</i>.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface RequestContext {

/**
 Get HTTP request path. Used to create the HTTP header <i>Location</i>.

 @return request path. It ends with {@code /}.
 @throws PublishableIllegalStateException @throws PublishableIllegalStateException if no found the
 HTTP request path. It is bug.
 @since 3.0.0
 */
URI getRestApiPath();

/**
 Get raw HTTP request path.

 @return request path. It ends with {@code /}. {@code null} If the process is not based on an HTTP
 request.
 @since 3.0.0
 */
URI getRawRestApiPath();

/**
 Get account id.

 @return logged account id. {@code null} if not logged in.
 @since 3.0.0
 */
String getAccountId();

/**
 Get processing name. Although this is not usually the case, if a value is provided both via REST
 API and via batch processing, the value provided via REST API will be returned.

 @return current processing name. {@code null} if no found process name.
 @since 3.0.0
 */
String getProcessName();

/**
 Current logged account id information.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
interface AccountIdContext {

/**
 Get current logged account id.

 @return current logged account id
 @since 3.0.0
 */
String getValue();

}

/**
 Current HTTP request path information. If the request did not come from HTTP, the value will be
 {@code null}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
interface RestApiPathContext {

/**
 Get current HTTP request path.

 @return current HTTP request path. {@code null} if the request did not come from HTTP.
 @since 3.0.0
 */
URI getValue();

}

/**
 Current process name information.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
interface ProcessNameContext {

/**
 Get current process name.

 @return current process name
 @since 3.0.0
 */
String getValue();

}

/**
 Current HTTP request process name information. This and {@link BatchProcessNameContext} cannot
 coexist.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
interface RestApiProcessNameContext extends ProcessNameContext {
}

/**
 Current batch process name information. This and {@link RestApiProcessNameContext} cannot coexist.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
interface BatchProcessNameContext extends ProcessNameContext {
}

}
