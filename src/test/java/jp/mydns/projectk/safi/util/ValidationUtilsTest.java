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

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import jp.mydns.projectk.safi.test.TestBean;
import jp.mydns.projectk.safi.test.TestValidationGroup;
import jp.mydns.projectk.safi.test.junit.ValidatorParameterResolver;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class {@code ValidationUtils}.
 *
 * @author riru
 * @since 3.0.0
 */
@ExtendWith(ValidatorParameterResolver.class)
class ValidationUtilsTest {

    /**
     * Test of requireValid method.
     *
     * @param validator the {@code Validator}. This parameter resolved by {@code ValidatorParameterResolver}.
     * @since 3.0.0
     */
    @Test
    void testRequireValid(Validator validator) throws Throwable {
        var bean = new TestBean();
        bean.setPositiveIfTestNumber(-1);   // Invalid if TestValidationGroup.
        bean.setNotEmptyString("notEmpty"); // Valid.

        // Validation OK.
        ThrowingCallable ok = () -> ValidationUtils.requireValid(bean, validator, Default.class);

        assertThatCode(ok).doesNotThrowAnyException();

        // Validation NG.
        ThrowingCallable ng = () -> ValidationUtils.requireValid(bean, validator, TestValidationGroup.class);

        assertThatThrownBy(ng).isInstanceOf(ConstraintViolationException.class)
            .asInstanceOf(InstanceOfAssertFactories.throwable(ConstraintViolationException.class))
            .extracting(ConstraintViolationException::getConstraintViolations)
            .asInstanceOf(InstanceOfAssertFactories.SET).isNotEmpty();
    }

    /**
     * Test of toMessageEntry method.
     *
     * @since 3.0.0
     */
    @Test
    void testToMessageEntry() {
    }

}
