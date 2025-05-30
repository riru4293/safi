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
import java.time.LocalDateTime;
import java.util.Locale;
import jp.mydns.projectk.safi.interceptor.BatchProcessNameExtractor;
import jp.mydns.projectk.safi.interceptor.BatchReferenceTimeResolver;
import jp.mydns.projectk.safi.producer.RequestContextProducer;
import jp.mydns.projectk.safi.resource.filter.LocaleExtractor;
import jp.mydns.projectk.safi.resource.filter.RestApiPathExtractor;
import jp.mydns.projectk.safi.resource.filter.RestApiProcessNameExtractor;
import jp.mydns.projectk.safi.resource.filter.RestApiReferenceTimeResolver;
import jp.mydns.projectk.safi.resource.trial.Authenticator;

/**
 Represents information about the current request. A request may originate from the Web API or from
 batch processing executed by <i>SAFI</i>.

 @see RequestContextProducer producer of this class.
 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface RequestContext {

/**
 Returns the HTTP request path. This is used to construct the HTTP <i>Location</i> header.
 <p>
 Note: If the application is accessed through a reverse proxy, the returned URI may not be directly
 usable by the end user. <i>SAFI</i> does not attempt to modify or correct the URI in such cases. It
 is the responsibility of the reverse proxy to ensure the URI is properly translated for user
 visibility.
 <p>
 The returned URI always ends with a slash ({@code /}).

 @return the HTTP request path as a {@link URI}. Always ends with {@code /}.
 @throws IllegalStateException if the request path could not be determined-either because the
 request is not an HTTP request or the path has not been properly extracted. This indicates an
 implementation defect, and processing should not continue.
 @since 3.0.0
 @see #getRawRestApiPath
 */
URI getRestApiPath();

/**
 Returns the raw HTTP request path, as originally received.

 @return the raw request path ending with {@code /}, or {@code null} if the process is not based on
 an HTTP request.
 @since 3.0.0
 @see RestApiPathContext
 */
URI getRawRestApiPath();

/**
 Returns the account ID of the logged-in user.

 @return the logged-in account ID, or {@code null} if the user is not logged in.
 @since 3.0.0
 @see AccountIdContext
 */
String getAccountId();

/**
 Returns current locale.

 @return the {@code Locale}. It's default value is {@link Locale#ENGLISH}. Note that if you retrieve
 the locale before it has been determined, it will be the default value.
 @since 3.0.0
 @see LocaleContext
 */
Locale getLocale();

/**
 Get processing name. Although this is not usually the case, if a value is provided both via REST
 API and via batch processing, the value provided via REST API will be returned.

 @return the current processing name, or {@code null} if no process name is available.
 @since 3.0.0
 @see RestApiProcessNameContext
 @see BatchProcessNameContext
 */
String getProcessName();

/**
 Get a reference time for the validity period. It is fixed at the value at the start of the
 processing request. This is to prevent switching between valid and invalid during processing.
 <p>
 Although this is not usually the case, if a value is provided both via REST API and via batch
 processing, the value provided via REST API will be returned.

 @return reference time, or {@code null} if no reference time is available.
 @since 3.0.0
 @see RestApiReferenceTimeContext
 @see BatchReferenceTimeContext
 */
LocalDateTime getReferenceTime();

/**
 Current logged account id information.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 @see Authenticator
 */
interface AccountIdContext {

/**
 Get current logged account id.

 @return current logged account id. {@code null} if the user is not logged in.
 @since 3.0.0
 */
String getValue();

}

/**
 Current HTTP request path information. If the request did not come from HTTP, the value will be
 {@code null}.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 @see RestApiPathExtractor
 */
interface RestApiPathContext {

/**
 Get current HTTP request path.

 @return current HTTP request path. It ends with {@code /}. {@code null} if the request did not come
 from HTTP.
 @since 3.0.0
 */
URI getValue();

}

/**
 Current process name information.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
interface ProcessNameContext {

/**
 Get current process name.

 @return current process name. This value represents a unique identifier assigned to each
 <i>Process</i>.

 Here, <i>Process</i> refers to a single HTTP request or a single batch operation. It must never be
 {@code null} under correct implementation. However, if a developer forgets to assign a valid
 identifier when adding new <i>Process</i>, this value may unintentionally remain {@code null}.

 Do not handle this case by simply checking for {@code null}. Doing so may hide critical
 implementation mistakes. Instead, ensure that all functional units are properly assigned unique
 identifiers at the time of implementation.
 @since 3.0.0
 */
String getValue();

}

/**
 Represents the process name for an HTTP request. This context cannot coexist with
 {@link BatchProcessNameContext}.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 @see RestApiProcessNameExtractor
 */
interface RestApiProcessNameContext extends ProcessNameContext {
}

/**
 Represents the process name for a batch process. This context cannot coexist with
 {@link RestApiProcessNameContext}.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 @see BatchProcessNameExtractor
 */
interface BatchProcessNameContext extends ProcessNameContext {
}

/**
 Current {@link Locale}.
 <p>
 The locale is determined for each HTTP request. If not set, English will be used. This is used to
 determine the language of the response message.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 @see LocaleExtractor
 */
interface LocaleContext {

/**
 Get current {@code Locale}.

 @return current {@code Locale}
 @since 3.0.0
 */
Locale getValue();

}

/**
 The reference time for the validity period. It is fixed at the value at the start of the processing
 request. This is to prevent switching between valid and invalid during processing.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
interface ReferenceTimeContext {

/**
 Get reference time.

 @return reference time. This value represents a unique identifier assigned to each
 <i>Process</i>.

 Here, <i>Process</i> refers to a single HTTP request or a single batch operation. It must never be
 {@code null} under correct implementation. However, if a developer forgets to assign a valid
 identifier when adding new <i>Process</i>, this value may unintentionally remain {@code null}.

 Do not handle this case by simply checking for {@code null}. Doing so may hide critical
 implementation mistakes. Instead, ensure that all functional units are properly assigned unique
 identifiers at the time of implementation.
 @since 3.0.0
 */
LocalDateTime getValue();

}

/**
 The reference time for the validity period per a HTTP request. This context cannot coexist with
 {@link BatchReferenceTimeContext}.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 @see RestApiReferenceTimeResolver
 */
interface RestApiReferenceTimeContext extends ReferenceTimeContext {
}

/**
 The reference time for the validity period per a batch process. This context cannot coexist with
 {@link RestApiReferenceTimeContext}.
 <p>
 Do not use this class directly. It is used as part of {@link RequestContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 @see BatchReferenceTimeResolver
 */
interface BatchReferenceTimeContext extends ReferenceTimeContext {
}

}
