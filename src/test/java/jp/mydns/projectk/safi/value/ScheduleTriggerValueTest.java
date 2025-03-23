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
import jakarta.json.bind.Jsonb;
import java.time.DayOfWeek;
import java.time.Month;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code ScheduleTriggerValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
class ScheduleTriggerValueTest {

    /**
     * Test of deserialize method if {@code DaysTriggerValue}, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfDays(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder()
            .add("kind", "DAYS")
            .add("anchorTime", "2000-01-01T00:00:00Z")
            .add("months", Json.createArrayBuilder().add(Month.JUNE.name()).add(Month.APRIL.name()))
            .add("days", Json.createArrayBuilder().add(1).add(31).add(2))
            .build();

        var deserialized = jsonb.fromJson(expect.toString(), ScheduleTriggerValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if {@code WeekdaysTriggerValue}, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfWeekdays(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder()
            .add("kind", "WEEKDAYS")
            .add("anchorTime", "2000-01-01T00:00:00Z")
            .add("months", Json.createArrayBuilder().add(Month.JUNE.name()).add(Month.APRIL.name()))
            .add("weeks", Json.createArrayBuilder().add(1).add(7).add(2))
            .add("weekdays", Json.createArrayBuilder().add(DayOfWeek.SATURDAY.name()).add(DayOfWeek.FRIDAY.name()))
            .build();

        var deserialized = jsonb.fromJson(expect.toString(), ScheduleTriggerValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if {@code OnceTriggerValue}, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfOnce(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder()
            .add("kind", "ONCE")
            .add("anchorTime", "2000-01-01T00:00:00Z")
            .build();

        var deserialized = jsonb.fromJson(expect.toString(), ScheduleTriggerValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if {@code CancelTriggerValue}, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfCancel(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder()
            .add("kind", "CANCEL")
            .add("anchorTime", "2000-01-01T00:00:00Z")
            .add("duration", "PT7S")
            .build();

        var deserialized = jsonb.fromJson(expect.toString(), ScheduleTriggerValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of getKind method.
     */
    @Test
    void testGetKind() {
    }

    /**
     * Test of getAnchorTime method.
     */
    @Test
    void testGetAnchorTime() {
    }
}
