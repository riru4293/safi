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
import java.time.ZoneOffset;

/**
 * Validates that the time is in the range 2000-01-01T00:00:00Z to 2999-12-31T23:59:59Z. Supported types are
 * {@code LocalDateTime} and {@code OffsetDateTime}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {
    TimeRange.LocalDateTimeValidator.class,
    TimeRange.OffsetDateTimeValidator.class})
public @interface TimeRange {

    String message() default "{jp.mydns.projectk.safi.validator.TimeRange.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        TimeRange[] value();
    }

    /**
     * A validator that checks that the time is in the range 2000-01-01T00:00:00Z to 2999-12-31T23:59:59Z.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    abstract class AbstractValidator {

        protected boolean isValid(long epochSecond) {
            return epochSecond >= 946684800L && epochSecond <= 32503679999L;
        }
    }

    /**
     * A validator that checks that the time is in the range 2000-01-01T00:00:00Z to 2999-12-31T23:59:59Z.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class LocalDateTimeValidator extends AbstractValidator implements ConstraintValidator<TimeRange, LocalDateTime> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public boolean isValid(LocalDateTime value, ConstraintValidatorContext ctx) {

            if (value == null) {
                return true;
            }

            return isValid(value.toInstant(ZoneOffset.UTC).getEpochSecond());

        }
    }

    /**
     * A validator that checks that the time is in the range 2000-01-01T00:00:00Z to 2999-12-31T23:59:59Z.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class OffsetDateTimeValidator extends AbstractValidator implements ConstraintValidator<TimeRange, OffsetDateTime> {

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public boolean isValid(OffsetDateTime value, ConstraintValidatorContext ctx) {

            if (value == null) {
                return true;
            }

            return isValid(value.toInstant().getEpochSecond());
        }
    }
}
