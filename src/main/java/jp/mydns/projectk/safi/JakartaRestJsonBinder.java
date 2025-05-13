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

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParsingException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 JSON serialization and deserialization of HTTP requests and responses.
 <p>
 Implementation requirements.
 <ul>
 <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JakartaRestJsonBinder extends MessageBodyReader<Object>, MessageBodyWriter<Object> {

/**
 Implements of the {@code JakartaRestJsonBinder}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Dependent
@Typed(JakartaRestJsonBinder.class)
class Impl implements JakartaRestJsonBinder {

private final Jsonb jsonb;

@Inject
@SuppressWarnings("unused")
Impl(Jsonb jsonb) {
    this.jsonb = jsonb;
}

/**
 Return {@code true} if {@code mediaType} is {@code application/json}.

 @param clazz no use
 @param type no use
 @param annons no use
 @param mediaType the {@code MediaType}
 @return {@code true} if {@code mediaType} is {@code application/json}
 @since 3.0.0
 */
@Override
public boolean isReadable(Class<?> clazz, Type type, Annotation[] annons, MediaType mediaType) {
    return MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
}

/**
 Read and deserialize the {@code InputStream} as JSON.

 @param clazz no use
 @param type deserialize type
 @param annons no use
 @param mediaType no use
 @param headers no use
 @param is the {@code InputStream}
 @return deserialized JSON
 @throws IOException no occurs
 @throws MalformedRequestException if failed deserialize request entity to JSON
 @throws NullPointerException if {@code is} or {@code type} is {@code null}.
 @since 3.0.0
 */
@Override
public Object readFrom(Class<Object> clazz, Type type, Annotation[] annons, MediaType mediaType,
    MultivaluedMap<String, String> headers, InputStream is) throws IOException {

    try {
        return jsonb.fromJson(is, type);
    } catch (JsonParsingException | JsonbException ex) {
        throw new MalformedRequestException(ex);
    }
}

/**
 Return {@code true} if {@code mediaType} is {@code application/json}.

 @param clazz no use
 @param type no use
 @param annons no use
 @param mediaType the {@code MediaType}
 @return {@code true} if {@code mediaType} is {@code application/json}
 @since 3.0.0
 */
@Override
public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annons, MediaType mediaType) {
    return MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
}

/**
 Serialize to JSON and write to the {@code OutputStream}.

 @param obj source object
 @param clazz no use
 @param type no use
 @param annons no use
 @param mediaType no use
 @param headers no use
 @param os the {@code OutputStream}
 @throws IOException if an IO error arises
 @throws JsonbException if any unexpected problem occurs during the serialization, such as I/O
 error.
 @throws NullPointerException if {@code obj} or {@code os} is {@code null}.
 @since 3.0.0
 */
@Override
public void writeTo(Object obj, Class<?> clazz, Type type, Annotation[] annons, MediaType mediaType,
    MultivaluedMap<String, Object> headers, OutputStream os) throws IOException {

    jsonb.toJson(obj, os);
}

}

/**
 Indicating that the HTTP request format is invalid.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public static class MalformedRequestException extends RuntimeException {

/**
 Construct with the {@code Throwable}.

 @param cause the {@code Throwable}
 @throws NullPointerException if {@code cause} is {@code null}
 @since 3.0.0
 */
public MalformedRequestException(Throwable cause) {
    super(Objects.requireNonNull(cause));
}

}

}
