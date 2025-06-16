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

import jakarta.json.JsonValue;
import jakarta.validation.Validator;
import java.net.URI;
import java.util.List;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 Test of class {@code ErrorResponseContext}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@ExtendWith(ValidatorParameterResolver.class)
class ErrorResponseContextTest {

/**
 Test of build method.

 @param validator the {@code Validator}. This parameter resolved by
 {@code ValidatorParameterResolver}.

 @since 3.0.0
 */
@Test
void testBuild(Validator validator) {

    var instance = new ErrorResponseContext.Builder()
        .withCode(URI.create("https://safi"))
        .withMessage("msg")
        .withDetails(List.of(JsonValue.EMPTY_JSON_OBJECT))
        .build(validator);

    var anotherBuilder = new ErrorResponseContext.Builder();

    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> anotherBuilder.with(instance));

    assertThat(instance)
        .returns(URI.create(
            "https://project-k.mydns.jp/safi/schemas/error-response-context.schema.json"),
            ErrorResponseContext::getSchema)
        .returns(URI.create("https://safi"), ErrorResponseContext::getCode)
        .returns("msg", ErrorResponseContext::getMessage)
        .satisfies(v -> {
            assertThat(v.getDetails()).isNotEmpty().hasValueSatisfying(c -> {
                assertThat(c).containsExactly(JsonValue.EMPTY_JSON_OBJECT);
            });
        });
}

/**
 Test of toString method.

 @since 3.0.0
 */
@Test
void testToString() {

    var instance = new ErrorResponseContext.Builder()
        .withCode(URI.create("https://safi"))
        .withMessage("msg")
        .withDetails(List.of(JsonValue.EMPTY_JSON_OBJECT))
        .unsafeBuild();


    assertThat(instance).hasToString("ErrorResponseContext{schema=https://project-k.mydns.jp/safi/"
        + "schemas/error-response-context.schema.json, code=https://safi, message=msg, details=[{}]}");
}

}
