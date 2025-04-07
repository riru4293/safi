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
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of class {@code TimeAccuracy}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class TimeAccuracyTest {

    /**
     * Test of isValid method for {@code LocalDateTime}.
     *
     * @param annon the {@code TimeAccuracy}. It provides by Mockito.
     * @param ctx the {@code ConstraintValidatorContext}. It provides by Mockito.
     * @since 3.0.0
     */
    @Test
    void testIsValidLocalDateTime(@Mock TimeAccuracy annon, @Mock ConstraintValidatorContext ctx) {

        var instance = new TimeAccuracy.LocalDateTimeValidator();

        var invalid = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 1);
        var valid = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);

        assertThat(instance.isValid(invalid, ctx)).isFalse();
        assertThat(instance.isValid(valid, ctx)).isTrue();

        assertThat(instance.isValid(null, ctx)).isTrue();
    }

    /**
     * Test of isValid method for {@code OffsetDateTime}.
     *
     * @param annon the {@code TimeAccuracy}. It provides by Mockito.
     * @param ctx the {@code ConstraintValidatorContext}. It provides by Mockito.
     * @since 3.0.0
     */
    @Test
    void testIsValidOffsetDateTime(@Mock TimeAccuracy annon, @Mock ConstraintValidatorContext ctx) {

        var instance = new TimeAccuracy.OffsetDateTimeValidator();

        var invalid = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 1, ZoneOffset.UTC);
        var valid = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        assertThat(instance.isValid(invalid, ctx)).isFalse();
        assertThat(instance.isValid(valid, ctx)).isTrue();

        assertThat(instance.isValid(null, ctx)).isTrue();
    }
}
