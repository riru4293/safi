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
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import jp.mydns.projectk.safi.test.junit.JsonbParameterResolver;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code ValidityPeriodValue}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(JsonbParameterResolver.class)
@ExtendWith(ValidatorParameterResolver.class)
class ValidityPeriodValueTest {

    /**
     * Test of isEnabled method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsEnabled_LocalDateTime(Validator validator) {
        var val = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 0, ZoneOffset.UTC))
                .withIgnored(false).build(validator);

        var tooFew = LocalDateTime.of(1999, 12, 31, 23, 59, 59, 999_999_999);
        var minimum = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
        var maximum = LocalDateTime.of(2000, 1, 1, 0, 0, 1, 0);
        var tooMany = LocalDateTime.of(2000, 1, 1, 0, 0, 1, 1);

        assertThat(val.isEnabled(tooFew)).isFalse();
        assertThat(val.isEnabled(minimum)).isTrue();
        assertThat(val.isEnabled(maximum)).isTrue();
        assertThat(val.isEnabled(tooMany)).isFalse();
    }

    /**
     * Test of isEnabled method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsEnabled_OffsetDateTime(Validator validator) {
        var val = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 0, ZoneOffset.UTC))
                .withIgnored(false).build(validator);

        var tooFew = OffsetDateTime.of(1999, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.UTC);
        var minimum = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        var maximum = OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 0, ZoneOffset.UTC);
        var tooMany = OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 1, ZoneOffset.UTC);

        assertThat(val.isEnabled(tooFew)).isFalse();
        assertThat(val.isEnabled(minimum)).isTrue();
        assertThat(val.isEnabled(maximum)).isTrue();
        assertThat(val.isEnabled(tooMany)).isFalse();
    }

    /**
     * Test of isEnabled method if ignored.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsEnabledIfIgnored(Validator validator) {
        var val = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        var tooFew = OffsetDateTime.of(1999, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.UTC);
        var minimum = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        var maximum = OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 0, ZoneOffset.UTC);
        var tooMany = OffsetDateTime.of(2000, 1, 1, 0, 0, 1, 1, ZoneOffset.UTC);

        assertThat(val.isEnabled(tooFew)).isFalse();
        assertThat(val.isEnabled(minimum)).isFalse();
        assertThat(val.isEnabled(maximum)).isFalse();
        assertThat(val.isEnabled(tooMany)).isFalse();
    }

    /**
     * Test of defaultFrom method.
     *
     * @since 3.0.0
     */
    @Test
    void testDefaultFrom() {
        var expect = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        assertThat(ValidityPeriodValue.defaultFrom()).isEqualTo(expect);
    }

    /**
     * Test of defaultTo method.
     *
     * @since 3.0.0
     */
    @Test
    void testDefaultTo() {
        var expect = OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);

        assertThat(ValidityPeriodValue.defaultTo()).isEqualTo(expect);
    }

    /**
     * Test of defaultIgnored method.
     *
     * @since 3.0.0
     */
    @Test
    void testDefaultIgnored() {
        assertThat(ValidityPeriodValue.defaultIgnored()).isFalse();
    }

    /**
     * Test of hashCode method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testHashCode(Validator validator) {
        var expect = Objects.hash(
                OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC),
                true
        );

        var val = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        assertThat(val).hasSameHashCodeAs(expect);
    }

    /**
     * Test of equals method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testEquals(Validator validator) {
        var val1 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        var val2 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        assertThat(val1).isEqualTo(val2);
    }

    /**
     * Test of equals method if not same {@code from}.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotSameFrom(Validator validator) {
        var val1 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2002, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        var val2 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of equals method if not same {@code to}.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotSameTo(Validator validator) {
        var val1 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2888, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        var val2 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of equals method if not same {@code ignored}.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotSameIgnored(Validator validator) {
        var val1 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(false).build(validator);

        var val2 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(true).build(validator);

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of equals method if other class.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testEqualsIfOtherClass(Validator validator) {
        var val1 = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(false).build(validator);

        var val2 = new Object();

        assertThat(val1).isNotEqualTo(val2);
    }

    /**
     * Test of toString method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testToString(Validator validator) {
        String tmpl = "ValidityPeriodValue{from=%s, to=%s, ignored=%s}";

        var val = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(false).build(validator);

        assertThat(val).hasToString(tmpl, "2000-01-01T00:00Z", "2999-12-31T23:59:59Z", "false");
    }

    /**
     * Test of with method, of class Builder.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testWith(Validator validator) {
        var expect = new ValidityPeriodValue.Builder()
                .withFrom(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withTo(OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC))
                .withIgnored(false).build(validator);

        var val = new ValidityPeriodValue.Builder().with(expect).build(validator);

        assertThat(val).isEqualTo(expect);
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

        var deserialized = jsonb.fromJson(expect.toString(), ValidityPeriodValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of deserialize method if empty JSON, of class Deserializer.
     *
     * @param jsonb the {@code Jsonb}. This parameter resolved by {@code JsonbParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testDeserializeIfEmptyJson(Jsonb jsonb) {
        JsonObject expect = Json.createObjectBuilder().add("from", "2000-01-01T00:00:00Z")
                .add("to", "2999-12-31T23:59:59Z").add("ignored", false).build();

        var deserialized = jsonb.fromJson("{}", ValidityPeriodValue.class);

        var serialized = jsonb.toJson(deserialized);

        var result = jsonb.fromJson(serialized, JsonObject.class);

        assertThat(result).isEqualTo(expect);
    }
}
