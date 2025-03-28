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
package jp.mydns.projectk.safi.util;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code TimeUtils}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class TimeUtilsTest {

    /**
     * Test of {@code toOffsetDateTime} method.
     *
     * @since 3.0.0
     */
    @Test
    void testToOffsetDateTime() {

        var expect = OffsetDateTime.of(1999, 12, 31, 23, 48, 53, 123456, ZoneOffset.UTC);

        var localDateTime = LocalDateTime.of(1999, 12, 31, 23, 48, 53, 123456);

        assertThat(TimeUtils.toOffsetDateTime(localDateTime)).isEqualTo(expect);
    }

    /**
     * Test of {@code toOffsetDateTime} method, if {@code null}.
     *
     * @since 3.0.0
     */
    @Test
    void testToOffsetDateTimeIfNull() {

        LocalDateTime nullValue = null;

        assertThat(TimeUtils.toOffsetDateTime(nullValue)).isNull();
    }

    /**
     * Test of toLocalDateTime method.
     *
     * @since 3.0.0
     */
    @Test
    void testToLocalDateTime_String() {

        var expect = LocalDateTime.of(1999, 12, 31, 23, 48, 53, 123456);

        var value = "1999-12-31T23:48:53.000123456";

        assertThat(TimeUtils.toLocalDateTime(value)).isEqualTo(expect);
    }

    /**
     * Test of toLocalDateTime method, if {@code null}.
     *
     * @since 3.0.0
     */
    @Test
    void testToLocalDateTime_StringIfNull() {
        assertThatNullPointerException().isThrownBy(() -> TimeUtils.toLocalDateTime(String.class.cast(null)));
    }

        /**
     * Test of toLocalDateTime method, if malformed.
     *
     * @since 3.0.0
     */
    @Test
    void testToLocalDateTime_StringIfMalformed() {

        var malformed = "1999-13-32T27:78:92.123456";

        assertThatThrownBy(() -> TimeUtils.toLocalDateTime(malformed)).hasCauseInstanceOf(DateTimeException.class);
    }

    /**
     * Test of toLocalDateTime method.
     *
     * @since 3.0.0
     */
    @Test
    void testToLocalDateTime_OffsetDateTime() {

        var expect = LocalDateTime.of(1999, 12, 31, 23, 48, 53, 123456);

        var value = OffsetDateTime.of(2000, 1, 1, 2, 48, 53, 123456, ZoneOffset.ofHours(3));

        assertThat(TimeUtils.toLocalDateTime(value)).isEqualTo(expect);
    }

    /**
     * Test of toLocalDateTime method, if {@code null}.
     *
     * @since 3.0.0
     */
    @Test
    void testToLocalDateTime_OffsetDateTimeIfNull() {
        assertThat(TimeUtils.toLocalDateTime(OffsetDateTime.class.cast(null))).isNull();
    }
}
