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
package jp.mydns.projectk.safi.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Validates that no value is less than seconds. Supported types are {@code LocalDateTime} and {@code OffsetDateTime}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {TimeAccuracy.LocalDateTimeValidator.class, TimeAccuracy.OffsetDateTimeValidator.class})
public @interface TimeAccuracy {

    /**
     * Get a validation message. It value is {@code {jp.mydns.projectk.safi.validator.TimeAccuracy.message}}.
     *
     * @return message validation message
     * @since 1.0.0
     */
    String message() default "{jp.mydns.projectk.safi.validator.TimeAccuracy.message}";

    /**
     * Get a validation group. Grouping is used to order constraints.
     *
     * @return validation group
     * @since 1.0.0
     */
    Class<?>[] groups() default {};

    /**
     * Get a validation category. Category is some any meta information.
     *
     * @return validation category
     * @since 1.0.0
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Allows multiple enumerations of this annotation.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        /**
         * Get included annotations.
         *
         * @return annotations
         * @since 1.0.0
         */
        TimeAccuracy[] value();
    }

    /**
     * A validator that validates that there is no fractional seconds value.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class LocalDateTimeValidator implements ConstraintValidator<TimeAccuracy, LocalDateTime> {

        /**
         * Constructor.
         *
         * @since 1.0.0
         */
        public LocalDateTimeValidator() {
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public boolean isValid(LocalDateTime value, ConstraintValidatorContext ctx) {

            if (value == null) {
                return true;
            }

            return value.truncatedTo(ChronoUnit.SECONDS).isEqual(value);

        }
    }

    /**
     * A validator that validates that there is no fractional seconds value.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class OffsetDateTimeValidator implements ConstraintValidator<TimeAccuracy, OffsetDateTime> {

        /**
         * Constructor.
         *
         * @since 1.0.0
         */
        public OffsetDateTimeValidator() {
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public boolean isValid(OffsetDateTime value, ConstraintValidatorContext ctx) {

            if (value == null) {
                return true;
            }

            return value.truncatedTo(ChronoUnit.SECONDS).isEqual(value);

        }
    }
}
