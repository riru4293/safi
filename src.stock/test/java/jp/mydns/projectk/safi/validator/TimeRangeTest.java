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
package jp.mydns.projectk.safi.validator;

import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of class {@code TimeRange}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class TimeRangeTest {

    /**
     * Test of isValid method for {@code LocalDateTime}.
     *
     * @param annon the {@code TimeRange}. It provides by Mockito.
     * @param ctx the {@code ConstraintValidatorContext}. It provides by Mockito.
     * @since 3.0.0
     */
    @Test
    void testIsValidLocalDateTime(@Mock TimeRange annon, @Mock ConstraintValidatorContext ctx) {

        doReturn(946_684_800L).when(annon).minEpochSecond();
        doReturn(32_503_679_999L).when(annon).maxEpochSecond();

        var instance = new TimeRange.LocalDateTimeValidator();

        instance.initialize(annon);

        var tooFew = LocalDateTime.of(2000, 1, 1, 0, 0).minusNanos(1L);
        var minimum = LocalDateTime.of(2000, 1, 1, 0, 0);
        var maximum = LocalDateTime.of(2999, 12, 31, 23, 59, 59);
        var tooMany = LocalDateTime.of(2999, 12, 31, 23, 59, 59).plusSeconds(1L);

        assertThat(instance.isValid(tooFew, ctx)).isFalse();
        assertThat(instance.isValid(minimum, ctx)).isTrue();
        assertThat(instance.isValid(maximum, ctx)).isTrue();
        assertThat(instance.isValid(tooMany, ctx)).isFalse();

        assertThat(instance.isValid(null, ctx)).isTrue();
    }

    /**
     * Test of isValid method for {@code OffsetDateTime}.
     *
     * @param annon the {@code TimeRange}. It provides by Mockito.
     * @param ctx the {@code ConstraintValidatorContext}. It provides by Mockito.
     * @since 3.0.0
     */
    @Test
    void testIsValidOffsetDateTime(@Mock TimeRange annon, @Mock ConstraintValidatorContext ctx) {

        doReturn(946_684_800L).when(annon).minEpochSecond();
        doReturn(32_503_679_999L).when(annon).maxEpochSecond();

        var instance = new TimeRange.OffsetDateTimeValidator();

        instance.initialize(annon);

        var tooFew = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).minusNanos(1L);
        var minimum = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        var maximum = OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);
        var tooMany = OffsetDateTime.of(2999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC).plusSeconds(1L);

        assertThat(instance.isValid(tooFew, ctx)).isFalse();
        assertThat(instance.isValid(minimum, ctx)).isTrue();
        assertThat(instance.isValid(maximum, ctx)).isTrue();
        assertThat(instance.isValid(tooMany, ctx)).isFalse();

        assertThat(instance.isValid(null, ctx)).isTrue();
    }
}
