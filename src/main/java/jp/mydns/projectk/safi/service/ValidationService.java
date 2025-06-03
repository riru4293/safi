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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.entity.NamedEntity;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.value.NamedValue;
import jp.mydns.projectk.safi.SafiLimited;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import static jp.mydns.projectk.safi.util.TimeUtils.toLocalDateTime;
import jp.mydns.projectk.safi.value.RequestContext;
import jp.mydns.projectk.safi.value.ValidityPeriodValue;

/**
 Provides value validation functionality.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface ValidationService {

/**
 Validate that it is within the validity period.

 @param value the {@code NamedValue}
 @return {@code true} if enabled, otherwise {@code false}
 @throws NullPointerException if {@code value} is {@code null}
 @since 3.0.0
 */
boolean isEnabled(NamedValue value);

/**
 Validate that it is within the validity period.

 @param entity the {@code NamedEntity}
 @return {@code true} if enabled, otherwise {@code false}
 @throws NullPointerException if {@code entity} is {@code null}
 @since 3.0.0
 */
boolean isEnabled(NamedEntity entity);

/**
 Validate that the value is valid. Validation is performed by <i>Jakarta Bean Validation</i>.

 @param <V> value type
 @param value validation value
 @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
 @return {@code value} as is
 @throws NullPointerException if any argument is {@code null}.
 @throws ConstraintViolationException if {@code value} has constraint violation.
 @since 3.0.0
 */
<V> V requireValid(V value, Class<?>... groups);

/**
 Implements of the {@code ValidationService}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(ValidationService.class)
@ApplicationScoped
class Impl implements ValidationService {

private final Provider<Validator> validatorPvd;
private final Provider<RequestContext> reqCtxPvd;

@Inject
@SuppressWarnings("unused")
Impl(@SafiLimited Provider<Validator> validatorPvd, Provider<RequestContext> reqCtxPvd) {
    this.validatorPvd = validatorPvd;
    this.reqCtxPvd = reqCtxPvd;
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code value} is {@code null}
 @since 3.0.0
 */
@Override
public boolean isEnabled(NamedValue value) {
    Objects.requireNonNull(value);

    ValidityPeriodValue vp = value.getValidityPeriod();

    return isEnabled(toLocalDateTime(vp.getFrom()), toLocalDateTime(vp.getTo()), vp.isIgnored());
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}
 @since 3.0.0
 */
@Override
public boolean isEnabled(NamedEntity entity) {
    Objects.requireNonNull(entity);

    ValidityPeriodEmb vp = entity.getValidityPeriod();

    return isEnabled(vp.getFrom(), vp.getTo(), vp.isIgnored());
}

private boolean isEnabled(LocalDateTime from, LocalDateTime to, boolean ignored) {

    LocalDateTime refTime = reqCtxPvd.get().getReferenceTime();

    return !ignored && !from.isAfter(refTime) && !to.isBefore(refTime);
}

/**
 {@inheritDoc}

 @throws NullPointerException if any argument is {@code null}.
 @throws ConstraintViolationException if {@code value} has constraint violation.
 @since 3.0.0
 */
@Override
public <V> V requireValid(V value, Class<?>... groups) {
    Objects.requireNonNull(value);
    Stream.of(Objects.requireNonNull(groups)).forEach(Objects::requireNonNull);

    return ValidationUtils.requireValid(value, validatorPvd.get(), groups);
}

}

}
