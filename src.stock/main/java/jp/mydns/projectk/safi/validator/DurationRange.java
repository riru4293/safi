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
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;

/**
 Validates that the duration is in the specified range. Supported type is {@code Duration}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Target({METHOD, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {DurationRange.Validator.class})
public @interface DurationRange {

String message() default "{jp.mydns.projectk.safi.validator.DurationRange.message}";

Class<?>[] groups() default {};

Class<? extends Payload>[] payload() default {};

/**
 Minimum of range. It is second. Default value is 0.

 @return second the element must be higher or equal to
 */
long minSecond() default 0L;

/**
 Maximum of range. It is second. Default value is 9223372036854775807.

 @return second the element must be lower or equal to
 */
long maxSecond() default Long.MAX_VALUE;

/**
 A validator that checks that the time is in the specified range. Default range is 0 to
 9223372036854775807.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
class Validator implements ConstraintValidator<DurationRange, Duration> {

private long min;
private long max;

/**
 Initialize the minimum and maximum values.

 @param annon the {@code DurationRange}
 @since 3.0.0
 */
@Override
public void initialize(DurationRange annon) {
    this.min = annon.minSecond();
    this.max = annon.maxSecond();
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public boolean isValid(Duration value, ConstraintValidatorContext ctx) {

    if (value == null) {
        return true;
    }

    long seconds = value.getSeconds();
    return seconds >= min && seconds <= max;
}

}

}
