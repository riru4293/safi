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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code JsonObjectValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class JsonObjectValueTest {

    /**
     * Test serialize and deserialize.
     *
     * @since 3.0.0
     */
    @Test
    void testSerialize() throws IOException, ClassNotFoundException {

        JsonObjectValue expect = new JsonObjectValue(Json.createObjectBuilder()
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
        final JsonObjectValue result;
        try (var bais = new ByteArrayInputStream(serialized); var ois = new ObjectInputStream(bais);) {
            result = JsonObjectValue.class.cast(ois.readObject());
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

        // Wrap with JsonObjectValue
        JsonObjectValue value = new JsonObjectValue(raw);

        assertThat(value).isEqualTo(raw);
    }

    /**
     * Test of equals method if not same.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotSame() {
        var val1 = new JsonObjectValue(JsonValue.EMPTY_JSON_OBJECT);
        var val2 = new JsonObjectValue(Json.createObjectBuilder().add("k", true).build());

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of equals method if other class.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfOtherClass() {
        var val1 = new JsonObjectValue(JsonValue.EMPTY_JSON_OBJECT);
        var val2 = new Object();

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of entrySet method.
     *
     * @since 3.0.0
     */
    @Test
    void testEntrySet() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.entrySet();
        var result = val.entrySet();

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getJsonArray method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetJsonArray() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getJsonArray("3");
        var result = val.getJsonArray("3");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getJsonObject method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetJsonObject() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getJsonObject("4");
        var result = val.getJsonObject("4");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getJsonNumber method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetJsonNumber() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getJsonNumber("1");
        var result = val.getJsonNumber("1");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getJsonString method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetJsonString() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getJsonString("2");
        var result = val.getJsonString("2");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getString method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetString_String() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getString("2");
        var result = val.getString("2");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getString method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetString_String_String() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getString("0", "y");
        var result = val.getString("0", "y");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getInt method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetInt_String() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getInt("1");
        var result = val.getInt("1");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getInt method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetInt_String_int() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getInt("0", 5);
        var result = val.getInt("0", 5);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getBoolean method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetBoolean_String() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getBoolean("0");
        var result = val.getBoolean("0");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getBoolean method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetBoolean_String_boolean() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getBoolean("5", false);
        var result = val.getBoolean("5", false);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of isNull method.
     *
     * @since 3.0.0
     */
    @Test
    void testIsNull() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.isNull("5");
        var result = val.isNull("5");

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getValueType method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetValueType() {
        var src = Json.createObjectBuilder().add("0", true).add("1", 7).add("2", "hello")
            .add("3", JsonValue.EMPTY_JSON_ARRAY).add("4", JsonValue.EMPTY_JSON_OBJECT).add("5", JsonValue.NULL).build();

        var val = new JsonObjectValue(src);

        var expect = src.getValueType();
        var result = val.getValueType();

        assertThat(result).isEqualTo(expect);
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

        var val = new JsonObjectValue(src);

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

        var val = new JsonObjectValue(src);

        var expect = src.toString();

        assertThat(val).hasToString(expect);
    }
}
