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

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ForwardedHeaderUtil {

    public static String reconstructBaseUri(UriInfo uriInfo, HttpHeaders headers) {
        URI baseUri = uriInfo.getBaseUri(); // includes context path

        return Optional.ofNullable(headers.getHeaderString("Forwarded"))
            .map(ForwardedHeaderUtil::parseFirstEntry)
            .map(entry -> {
                String scheme = extract(entry, "proto").orElse("http");
                String hostPort = extract(entry, "host").orElse(baseUri.getHost());
                String contextPath = baseUri.getPath(); // already ends with "/"

                return scheme + "://" + hostPort + contextPath;
            })
            .orElse(baseUri.toString());
    }

    private static String parseFirstEntry(String header) {
        return Stream.of(header.split(","))
            .map(String::trim)
            .findFirst()
            .orElse("");
    }

    /*
    ✅ 出力例

Forwarded ヘッダー	baseUri（実際のURL）	出力 URI
proto=https;host=example.com:443	http://localhost:8080/app/	https://example.com:443/app/
proto=http;host=foo.local	http://localhost:8080/app/	http://foo.local/app/
(なし)	http://localhost:8080/app/	http://localhost:8080/app/
    
    
    @Context
UriInfo uriInfo;

@Context
HttpHeaders headers;

String baseUri = ForwardedHeaderUtil.reconstructBaseUri(uriInfo, headers);
     */
    private static Optional<String> extract(String entry, String key) {
        Matcher matcher = Pattern.compile(key + "=([^;\\s]+)").matcher(entry);
        return matcher.find()
            ? Optional.of(matcher.group(1).replace("\"", ""))
            : Optional.empty();
    }
}
