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

/**
 * Current request information. A <i>Request</i> is a processing request, and there are processing requests via Web API
 * and background processing requests by the system.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface RequestContext {

    /**
     * Get request path.
     *
     * @return request path. {@code null} if request not from web API.
     * @since 3.0.0
     */
    URI getPath();

    /**
     * Get account id.
     *
     * @return logged account id
     * @since 3.0.0
     */
    String getAccountId();

    /**
     * Get processing name.
     *
     * @return current processing name
     * @since 3.0.0
     */
    String getProcessName();

    /**
     * Current request path information.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface PathContext {

        /**
         * Get current request path.
         *
         * @return current request path
         * @since 3.0.0
         */
        URI getValue();
    }

    /**
     * Current process name information.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    interface ProcessNameContext {

        /**
         * Get current process name.
         *
         * @return current process name
         * @since 3.0.0
         */
        String getValue();
    }
}
