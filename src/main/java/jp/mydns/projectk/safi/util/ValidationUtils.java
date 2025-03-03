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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Utilities for Jakarta Bean Validation.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class has not variable field member and it has all method is static.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Verify that the value is valid using {@code jakarta.validation.Validator}.
     *
     * @param <T> value type
     * @param value value that to be validated
     * @param validator the {@code Validator}
     * @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
     * @return value as is that received by argument {@code value}
     * @throws NullPointerException if any argument is {@code null}
     * @throws ConstraintViolationException if {@code value} is invalid
     * @since 3.0.0
     */
    public static <T> T requireValid(T value, Validator validator, Class<?>... groups) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(validator);
        Stream.of(Objects.requireNonNull(groups)).forEach(Objects::requireNonNull);

        Set<ConstraintViolation<T>> violations = validator.validate(value, groups);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return value;
    }
}
