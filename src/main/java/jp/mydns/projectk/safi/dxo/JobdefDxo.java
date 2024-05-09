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
import java.util.Optional;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.ValidationService;
import jp.mydns.projectk.safi.value.Filtdef;
import jp.mydns.projectk.safi.value.Jobdef;
import jp.mydns.projectk.safi.value.Plugdef;

/**
 * Data exchange processing for <i>Job</i> definition.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class JobdefDxo {

    @Inject
    private ValidationService validSvc;

    @Inject
    private JsonService jsonSvc;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected JobdefDxo() {
    }

    /**
     * Build a value based on the entity.
     *
     * @param entity the {@code JobdefEntity}
     * @return the {@code Jobdef} that built
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws ConstraintViolationException if {@code entity} has constraint violation
     * @since 1.0.0
     */
    public Jobdef toValue(JobdefEntity entity) {
        Objects.requireNonNull(entity);

        var builder = new Jobdef.Builder();
        Optional.ofNullable(entity.getPlugdef()).map(v -> jsonSvc.convertViaJson(v, Plugdef.class)).ifPresent(builder::withPlugdef);
        Optional.ofNullable(entity.getFiltdef()).map(v -> jsonSvc.convertViaJson(v, Filtdef.class)).ifPresent(builder::withFiltdef);
        Optional.ofNullable(entity.getTrnsdef()).map(jsonSvc::toStringMap).ifPresent(builder::withTrnsdef);

        return builder
                .withId(entity.getId())
                .withJobKind(entity.getJobKind())
                .withContentKind(entity.getContentKind())
                .withName(entity.getName())
                .withTimeout(entity.getTimeout())
                .withOptions(entity.getOptions())
                .withValidityPeriod(entity.getValidityPeriod())
                .withPersistenceContext(entity.toPersistenceContext())
                .withNote(entity.getNote())
                .build(validSvc.getValidator());
    }

    /**
     * Convert to entity from value.
     *
     * @param value the {@code Jobdef}
     * @return the {@code JobdefEntity}
     * @throws NullPointerException if {@code value} is {@code null}
     * @since 1.0.0
     */
    public JobdefEntity toEntity(Jobdef value) {
        Optional<Jobdef> optValue = Optional.of(Objects.requireNonNull(value));

        var entity = new JobdefEntity();
        entity.setId(value.getId());
        entity.setJobKind(value.getJobKind());
        entity.setContentKind(value.getContentKind());
        entity.setName(value.getName());
        entity.setTimeout(value.getTimeout());
        optValue.map(Jobdef::getPlugdef).map(jsonSvc::toJsonObjectVo).ifPresent(entity::setPlugdef);
        optValue.map(Jobdef::getFiltdef).map(jsonSvc::toJsonObjectVo).ifPresent(entity::setFiltdef);
        optValue.map(Jobdef::getTrnsdef).map(jsonSvc::toJsonObjectVo).ifPresent(entity::setTrnsdef);
        entity.setOptions(jsonSvc.toJsonObjectVo(value.getOptions()));
        optValue.flatMap(Jobdef::getPersistenceContext).ifPresent(entity::applyPersistenceContext);
        entity.setNote(value.getNote());

        return entity;
    }
}
