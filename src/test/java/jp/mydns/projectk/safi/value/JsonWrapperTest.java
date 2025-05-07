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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code SJson}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
class JsonWrapperTest {

    /**
     * Test serialize and deserialize.
     *
     * @since 3.0.0
     */
    @Test
    void testSerialize() throws IOException, ClassNotFoundException {

        SJson expect = SJson.of(Json.createObjectBuilder()
            .add("null", JsonValue.NULL)
            .add("true", JsonValue.TRUE)
            .add("false", JsonValue.FALSE)
            .add("number", 777)
            .add("string", "hello")
            .add("array", Json.createArrayBuilder().add(1))
            .add("object", Json.createObjectBuilder().add("key", "value"))
            .build());

        // Serialize
        final byte[] serialized;
        try (var baos = new ByteArrayOutputStream(); var oos = new ObjectOutputStream(baos);) {
            oos.writeObject(expect);
            oos.flush();
            serialized = baos.toByteArray();
        }

        // Deserialize
        final SJson result;
        try (var bais = new ByteArrayInputStream(serialized); var ois = new ObjectInputStream(bais);) {
            result = SJson.class.cast(ois.readObject());
        }

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test equals method.
     *
     * @since 3.0.0
     */
    @Test
    void testEquals() {
        JsonObject raw = Json.createObjectBuilder()
            .add("null", JsonValue.NULL)
            .add("true", JsonValue.TRUE)
            .add("false", JsonValue.FALSE)
            .add("number", 777)
            .add("string", "hello")
            .add("array", Json.createArrayBuilder().add(1))
            .add("object", Json.createObjectBuilder().add("key", "value"))
            .build();

        // Wrap with SJson
        SJson val1 = SJson.of(raw);
        SJson val2 = SJson.of(raw);

        assertThat(val1).isEqualTo(val2);
    }

    /**
     * Test of equals method if not same.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotSame() {
        var val1 = SJson.of(JsonValue.EMPTY_JSON_OBJECT);
        var val2 = SJson.of(Json.createObjectBuilder().add("k", true).build());

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of equals method if other class.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfOtherClass() {
        var val1 = SJson.of(JsonValue.EMPTY_JSON_OBJECT);
        var val2 = new Object();

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of hashCode method.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCode() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = SJson.of(src);

        assertThat(val).hasSameHashCodeAs(src);
    }

    /**
     * Test of toString method.
     *
     * @since 3.0.0
     */
    @Test
    void testToString() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = SJson.of(src);

        var expect = src.toString();

        assertThat(val).hasToString(expect);
    }

    /**
     * Test of deserialize method, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserialize(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder().add("from", "2000-01-01T00:00:00Z")
            .add("to", "2999-12-31T23:59:59Z").add("ignored", true).build();

        var deserialized = jsonb.fromJson(expect.toString(), SJson.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }
}
