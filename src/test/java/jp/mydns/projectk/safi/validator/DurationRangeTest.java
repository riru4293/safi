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

import jakarta.validation.Validator;
import java.time.Duration;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class DurationRange.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(ValidatorParameterResolver.class)
class DurationRangeTest {

    /**
     * Test of isValid method if too few.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsValidIfTooFew(Validator validator) {
        var bean = new Bean();
        bean.value = Duration.ofSeconds(1);

        var result = validator.validate(bean);

        assertThat(result).isNotEmpty();
    }

    /**
     * Test of isValid method if minimum.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsValidIfMinimum(Validator validator) {
        var bean = new Bean();
        bean.value = Duration.ofSeconds(2);

        var result = validator.validate(bean);

        assertThat(result).isEmpty();
    }

    /**
     * Test of isValid method if maximum.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsValidIfMaximum(Validator validator) {
        var bean = new Bean();
        bean.value = Duration.ofSeconds(3);

        var result = validator.validate(bean);

        assertThat(result).isEmpty();
    }

    /**
     * Test of isValid method if too many.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsValidIfTooMany(Validator validator) {
        var bean = new Bean();
        bean.value = Duration.ofSeconds(4);

        var result = validator.validate(bean);

        assertThat(result).isNotEmpty();
    }

    /**
     * Test of isValid method if null.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testIsValidIfNull(Validator validator) {
        var bean = new Bean();

        var result = validator.validate(bean);
        assertThat(result).isEmpty();
    }

    public class Bean {

        @DurationRange(minSecond = 2, maxSecond = 3)
        public Duration value;
    }
}
