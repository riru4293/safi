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
package jp.mydns.projectk.safi.resource.exceptionmapper;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class X implements ContainerRequestFilter {

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders httpHeaders;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        URI absolutePath = uriInfo.getAbsolutePath(); // クエリなしのパス
        String scheme = absolutePath.getScheme();
        String host = absolutePath.getHost();
        Optional<Integer> port = absolutePath.getPort() == -1 ? Optional.empty() : Optional.of(absolutePath.getPort());
        String path = absolutePath.getRawPath();

        Optional<String> forwardedHeader = Optional.ofNullable(httpHeaders.getHeaderString("Forwarded"));

        forwardedHeader.ifPresent(forwarded -> {
            // Forwarded: for=...; proto=...; host=... , for=...; proto=... ← 複数ある場合の最初だけ
            String firstForwarded = forwarded.split(",")[0]; // 複数あっても最初だけ使う
            String[] directives = firstForwarded.split(";");
            for (String directive : directives) {
                String[] kv = directive.trim().split("=", 2);
                if (kv.length != 2) continue;

                String key = kv[0].trim().toLowerCase();
                String value = kv[1].trim().replaceAll("^\"|\"$", ""); // 引用符削除

                switch (key) {
                    case "proto" -> scheme = value;
                    case "host" -> {
                        // IPv6対応 host 解析
                        Matcher ipv6Matcher = Pattern.compile("^\\[(.+)](?::(\\d+))?$").matcher(value);
                        if (ipv6Matcher.matches()) {
                            host = "[" + ipv6Matcher.group(1) + "]";
                            port = Optional.ofNullable(ipv6Matcher.group(2)).map(Integer::parseInt);
                        } else if (value.contains(":")) {
                            // 通常の host:port
                            String[] parts = value.split(":", 2);
                            host = parts[0];
                            port = Optional.of(Integer.parseInt(parts[1]));
                        } else {
                            host = value;
                            port = Optional.empty();
                        }
                    }
                }
            }
        });

        String portPart = port.map(p -> ":" + p).orElse("");
        String location = scheme + "://" + host + portPart + path;

        // ← これを Location に使えば、ユーザーが見ているURLを再現できる
        System.out.println("Resolved Location URL: " + location);
        // 例: Response.created(URI.create(location)).build();
    }
}
