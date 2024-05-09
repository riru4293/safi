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
package jp.mydns.projectk.safi.dxo;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import jp.mydns.projectk.safi.entity.SchedefEntity;
import jp.mydns.projectk.safi.service.ValidationService;
import jp.mydns.projectk.safi.value.SchedefValue;

/**
 * Data exchange processing for scheduling definition.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class SchedefDxo {

    @Inject
    private ValidationService validSvc;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected SchedefDxo() {
    }

    /**
     * Build a value based on the entity.
     *
     * @param entity the {@code SchedefEntity}
     * @return the {@code Schedef} that built
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws ConstraintViolationException if {@code entity} has constraint violation
     * @since 1.0.0
     */
    public SchedefValue toValue(SchedefEntity entity) {
        return new SchedefValue.Builder(Objects.requireNonNull(entity)).build(validSvc.getValidator());
    }
}
