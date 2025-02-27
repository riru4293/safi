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
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code JsonArrayValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class JsonArrayValueTest {

    /**
     * Test serialize and deserialize.
     *
     * @since 3.0.0
     */
    @Test
    void testSerialize() throws IOException, ClassNotFoundException {

        JsonArrayValue expect = new JsonArrayValue(Json.createArrayBuilder()
            .add(JsonValue.NULL)
            .add(JsonValue.TRUE)
            .add(JsonValue.FALSE)
            .add(777)
            .add("string")
            .add(Json.createArrayBuilder().add(1))
            .add(Json.createObjectBuilder().add("key", "value"))
            .build());

        // Serialize
        final byte[] serialized;
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos);) {
            oos.writeObject(expect);
            oos.flush();
            serialized = baos.toByteArray();
        }

        // De-serialize
        final JsonArrayValue result;
        try (var bais = new ByteArrayInputStream(serialized);
             var ois = new ObjectInputStream(bais);) {
            result = JsonArrayValue.class.cast(ois.readObject());
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
        JsonArray raw = Json.createArrayBuilder()
            .add(JsonValue.NULL)
            .add(JsonValue.TRUE)
            .add(JsonValue.FALSE)
            .add(777)
            .add("hello")
            .add(Json.createArrayBuilder().add(1))
            .add(Json.createObjectBuilder().add("key", "value"))
            .build();

        // Wrap with JsonArrayValue
        JsonArrayValue value = new JsonArrayValue(raw);

        assertThat(value).isEqualTo(raw);
    }
}
