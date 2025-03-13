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
package jp.mydns.projectk.safi.dxo;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.trial.ValidationService;
import jp.mydns.projectk.safi.value.JobdefValue;

/**
 * Data exchange processing for <i>Job definition</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class JobdefDxo {

    private final ValidationService validationSvc;

    private final JsonService jsonSvc;

    /**
     * Constructor.
     *
     * @param validationSvc the {@code ValidationService}
     * @param jsonSvc the {@code JsonService}
     * @since 3.0.0
     */
    @Inject
    public JobdefDxo(ValidationService validationSvc, JsonService jsonSvc) {
        this.validationSvc = validationSvc;
        this.jsonSvc = jsonSvc;
    }

    /**
     * Exchange to value object from JSON.
     *
     * @param jobdef job definition as JSON
     * @return the {@code Jobdef}
     * @throws NullPointerException if {@code jobdefAsJson} is {@code null}
     * @throws ConstraintViolationException if {@code jobdefAsJson} has constraint violation
     * @since 3.0.0
     */
    public JobdefValue toValue(JsonObject jobdef) {
        return validationSvc.requireValid(jsonSvc.fromJsonObject(Objects.requireNonNull(jobdef), JobdefValue.class));
    }

    /**
     * Exchange to value object from entity.
     *
     * @param entity the {@code JobdefEntity}
     * @return the {@code Jobdef}
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws ConstraintViolationException if {@code entity} has constraint violation
     * @since 3.0.0
     */
    public JobdefValue toValue(JobdefEntity entity) {
        return validationSvc.requireValid(jsonSvc.convertViaJson(Objects.requireNonNull(entity), JobdefValue.class));
    }
}
