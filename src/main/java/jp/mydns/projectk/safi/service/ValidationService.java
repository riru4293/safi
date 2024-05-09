/*
 * Copyright (c) 2024, Project-K
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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * Validation utilities by the <i>Jakarta Bean Validation</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ApplicationScoped
public class ValidationService {

    @Inject
    private Validator validator;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected ValidationService() {
    }

    /**
     * Get the {@code Validator}.
     *
     * @return the {@code Validator}
     * @since 1.0.0
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * Verify that the value is valid.
     *
     * @param <T> value type
     * @param value value that to be validated
     * @return value as is that received by argument {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws ConstraintViolationException if {@code value} is invalid
     * @see ValidationUtils#requireValid(java.lang.Object, jakarta.validation.Validator, java.lang.Class...)
     * @since 1.0.0
     */
    public <T> T requireValid(T value) {
        return ValidationUtils.requireValid(value, validator);
    }

    /**
     * Verify that the value is valid.
     *
     * @param <T> value type
     * @param value value that to be validated
     * @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
     * @return value as is that received by argument {@code value}
     * @throws NullPointerException if any argument is {@code null}
     * @throws ConstraintViolationException if {@code value} is invalid
     * @see ValidationUtils#requireValid(java.lang.Object, jakarta.validation.Validator, java.lang.Class...)
     * @since 1.0.0
     */
    public <T> T requireValid(T value, Class<?>... groups) {
        return ValidationUtils.requireValid(value, validator, groups);
    }
}
