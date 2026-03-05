/*
 * Copyright 2025, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 Validates that the time does not have fractional seconds.

 Supported types are {@code Duration}, {@code LocalDateTime} and {@code OffsetDateTime}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Target({METHOD, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {
    NoFractionalSeconds.DurationValidator.class,
    NoFractionalSeconds.LocalDateTimeValidator.class,
    NoFractionalSeconds.OffsetDateTimeValidator.class
})
public @interface NoFractionalSeconds
{
    /**
     Private constants.

     @hidden
    */
    static final String DEFAULT_MESSAGE = "{jp.mydns.projectk.safi.validator.NoFractionalSeconds.message}";

    /**
     Default validation message.

     @return {@value DEFAULT_MESSAGE}
     */
    String message() default DEFAULT_MESSAGE;

    /**
     The validation groups to which this constraint belongs.

     Groups allow selective application of constraints during validation.

     @return the groups for this constraint
     */
    Class<?>[] groups() default {};

    /**
     Payload for clients to associate metadata with this constraint.

     This is not used by the validation engine itself.

     @return the payload types
     */
    Class<? extends Payload>[] payload() default {};

    /**
     Internal Implementation.

     @hidden
     */
    class LocalDateTimeValidator implements ConstraintValidator<NoFractionalSeconds, LocalDateTime>
    {
        @Override
        public boolean isValid(LocalDateTime value, ConstraintValidatorContext ctx)
        {
            return value == null || value.truncatedTo(ChronoUnit.SECONDS).isEqual(value);
        }

    }

    /**
     Internal Implementation.

     @hidden
     */
    class OffsetDateTimeValidator implements ConstraintValidator<NoFractionalSeconds, OffsetDateTime>
    {
        @Override
        public boolean isValid(OffsetDateTime value, ConstraintValidatorContext ctx)
        {
            return value == null || value.truncatedTo(ChronoUnit.SECONDS).isEqual(value);
        }
    }

    /**
     Internal Implementation.

     @hidden
     */
    class DurationValidator implements ConstraintValidator<NoFractionalSeconds, Duration>
    {
        @Override
        public boolean isValid(Duration value, ConstraintValidatorContext ctx)
        {
            return value == null || value.truncatedTo(ChronoUnit.SECONDS).equals(value);
        }
    }
}
