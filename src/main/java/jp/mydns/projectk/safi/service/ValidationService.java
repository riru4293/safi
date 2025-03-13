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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import jp.mydns.projectk.safi.entity.NamedEntity;
import jp.mydns.projectk.safi.service.trial.AppTimeService;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.value.NamedValue;

/**
 * Provides validation of values.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class ValidationService {

    private final Validator validator;
    private final AppTimeService appTimeService;

    /**
     * Constructor.
     *
     * @param validator the {@code Validator}
     * @param appTimeService the {@code AppTimeService}
     * @since 3.0.0
     */
    @Inject
    public ValidationService(Validator validator, AppTimeService appTimeService) {
        this.validator = validator;
        this.appTimeService = appTimeService;
    }

    /**
     * Validate that it is within the validity period.
     *
     * @param value the {@code NamedValue}
     * @return {@code true} if enabled, otherwise {@code false}
     * @throws NullPointerException if {@code value} is {@code null}
     * @since 3.0.0
     */
    public boolean isEnabled(NamedValue value) {
        return isEnabled(value.getValidityPeriod().getFrom(), value.getValidityPeriod().getTo(),
            value.getValidityPeriod().isIgnored());
    }

    /**
     * Validate that it is within the validity period.
     *
     * @param entity the {@code NamedEntity}
     * @return {@code true} if enabled, otherwise {@code false}
     * @throws NullPointerException if {@code entity} is {@code null}
     * @since 3.0.0
     */
    public boolean isEnabled(NamedEntity entity) {
        return isEnabled(OffsetDateTime.of(entity.getValidityPeriod().getFrom(), ZoneOffset.UTC),
            OffsetDateTime.of(entity.getValidityPeriod().getTo(), ZoneOffset.UTC),
            entity.getValidityPeriod().isIgnored());
    }

    private boolean isEnabled(OffsetDateTime from, OffsetDateTime to, boolean ignored) {
        OffsetDateTime refTime = appTimeService.getOffsetNow();

        return !ignored && !from.isAfter(refTime) && !to.isBefore(refTime);
    }

    /**
     * Validate that the value is valid.
     *
     * @param <V> value type
     * @param value validation value
     * @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
     * @return valid value
     * @throws NullPointerException if any argument is {@code null}
     * @throws ConstraintViolationException if {@code value} has constraint violation
     * @since 3.0.0
     */
    public <V> V requireValid(V value, Class<?>... groups) {
        return ValidationUtils.requireValid(value, validator, groups);
    }
}
