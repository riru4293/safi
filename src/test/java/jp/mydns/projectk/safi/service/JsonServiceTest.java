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
package jp.mydns.projectk.safi.service;

import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import java.util.Map;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.value.JsonObjectValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code JsonService}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
class JsonServiceTest {

    /**
     * Test of toJsonObject method.
     *
     * @since 3.0.0
     */
    @Test
    void testToJsonObject() {
    }

    /**
     * Test of merge method.
     *
     * @since 3.0.0
     */
    @Test
    void testMerge() {
    }

    /**
     * Test of toJsonObjectValue method.
     *
     * @since 3.0.0
     */
    @Test
    void testToJsonObjectValue_JsonObject() {
    }

    /**
     * Test of toJsonObjectValue method.
     *
     * @since 3.0.0
     */
    @Test
    void testToJsonObjectValue_Object() {
    }

    /**
     * Test of fromJsonObject method.
     *
     * @since 3.0.0
     */
    @Test
    void testFromJsonObject() {
    }

    /**
     * Test of convertViaJson method.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testConvertViaJson(Jsonb jsonb) {
        var instance = new JsonService(jsonb);

        var result = instance.convertViaJson(Map.of(1, "hello", 2, "hey"), JsonObjectValue.class);

        System.out.println(result);

        new JsonObjectValue.Deserializer();
    }

}
