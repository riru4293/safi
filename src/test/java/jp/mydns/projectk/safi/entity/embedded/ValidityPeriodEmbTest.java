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
package jp.mydns.projectk.safi.entity.embedded;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code VallidityPeriodEmb}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class ValidityPeriodEmbTest {

    /**
     * Test of hashCode method.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCode() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        other.setIgnored(false);

        assertThat(instance).hasSameHashCodeAs(other);
    }

    /**
     * Test of hashCode method if not equals from property.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCodeIfNotEqualsFrom() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0, 22));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        other.setIgnored(false);

        assertThat(instance).doesNotHaveSameHashCodeAs(other);
    }

    /**
     * Test of hashCode method if not equals to property.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCodeIfNotEqualsTo() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59));
        other.setIgnored(false);

        assertThat(instance).doesNotHaveSameHashCodeAs(other);
    }

    /**
     * Test of hashCode method if not equals ignored property.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCodeIfNotEqualsIgnored() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        other.setIgnored(true);

        assertThat(instance).doesNotHaveSameHashCodeAs(other);
    }

    /**
     * Test of equals method.
     *
     * @since 3.0.0
     */
    @Test
    void testEquals() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        other.setIgnored(false);

        assertThat(instance).isEqualTo(other);
    }

    /**
     * Test of equals method if not equals from property.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotEqualsFrom() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0, 22));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        other.setIgnored(false);

        assertThat(instance).isNotEqualTo(other);
    }

    /**
     * Test of equals method if not equals to property.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotEqualsTo() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 00));
        other.setIgnored(false);

        assertThat(instance).isNotEqualTo(other);
    }

    /**
     * Test of equals method if not equals ignored property.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotEqualsIgnored() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        var other = new ValidityPeriodEmb();

        other.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        other.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        other.setIgnored(true);

        assertThat(instance).isNotEqualTo(other);
    }

    /**
     * Test of equals method if other instance.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfOtherInstance() {
        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        assertThat(instance).isNotEqualTo(null);
    }

    /**
     * Test of toString method.
     *
     * @since 3.0.0
     */
    @Test
    void testToString() {
        var expect = "ValidityPeriodEmb{from=2000-01-01T00:00, to=2999-12-31T23:59:59, ignored=false}";

        var instance = new ValidityPeriodEmb();

        instance.setFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        instance.setTo(LocalDateTime.of(2999, 12, 31, 23, 59, 59));
        instance.setIgnored(false);

        assertThat(instance).hasToString(expect);
    }
}
