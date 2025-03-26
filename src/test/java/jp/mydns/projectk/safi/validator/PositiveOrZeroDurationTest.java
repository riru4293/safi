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

import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class PositiveOrZeroDuration.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class PositiveOrZeroDurationTest {

    /**
     * Test of isValid method, of class Validator.
     *
     * @since 3.0.0
     */
    @Test
    void testIsValid() {
        var validator = new PositiveOrZeroDuration.Validator();

        var valid = Duration.ZERO;
        var invalid = Duration.ofNanos(-1);

        assertThat(validator.isValid(valid, null/* no use */)).isTrue();
        assertThat(validator.isValid(invalid, null/* no use */)).isFalse();
    }

    /**
     * Test of isValid method if null, of class Validator.
     *
     * @since 3.0.0
     */
    @Test
    void testIsValidIfNull() {
        var validator = new PositiveOrZeroDuration.Validator();

        assertThat(validator.isValid(null, null/* no use */)).isTrue();
    }
}
