/*
 * Copyright (c) 2022, Project-K
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

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Objects;
import jp.mydns.projectk.safi.dxo.ImportationDxo.AbstractImportationDxo;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.validator.Unsafe;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.TransResult;
import jp.mydns.projectk.safi.value.UserValue;
import jp.mydns.projectk.safi.value.ValidityPeriod;
import trial.ImportationValue;

/**
 * Data exchange interface for <i>User</i> content importation processing.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserImportationDxo extends AbstractImportationDxo<UserEntity, UserValue>
        implements ImportationDxo<UserEntity, UserValue> {

    @Inject
    private ContentValue.DigestGenerator digestGen;

    @Inject
    private Validator validator;

    @Inject
    private AppTimeService appTimeSvc;

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code transResult} is {@code null}
     * @throws ConstraintViolationException if occurred constraint violations when building
     * @since 1.0.0
     */
    @Override
    public ImportationValue<UserValue> toImportationValue(TransResult.Success transResult) {
        Objects.requireNonNull(transResult);

        Map<String, String> value = transResult.getContent();
        ValidityPeriod vp = toValidityPeriod(value);

        return new ImportationValue<>(
                new UserValue.Builder(digestGen)
                        .withId(value.get("id"))
                        .withEnabled(vp.isEnabled(appTimeSvc.getLocalNow()))
                        .withName(value.get("name"))
                        .withAtts(toAtts(value))
                        .withValidityPeriod(vp)
                        .withNote(value.get("note"))
                        .build(validator),
                transResult.getSource());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    @Override
    public ImportationValue<UserValue> toImportationValue(UserEntity entity, ImportationValue<UserValue> importValue) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(importValue);

        return new ImportationValue<>(
                new UserValue.Builder(digestGen)
                        .with(importValue.getContent())
                        .withEnabled(importValue.getContent().getValidityPeriod().isEnabled(appTimeSvc.getLocalNow()))
                        .withEntity(entity)
                        .build(validator, Unsafe.class),
                importValue.getSource());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code importValue} is {@code null}
     */
    @Override
    public UserEntity toEntity(ImportationValue<UserValue> importValue) {
        Objects.requireNonNull(importValue);

        UserValue value = importValue.getContent();
        UserEntity entity = value.getEntity().orElseGet(UserEntity::new);

        entity.setId(value.getId());
        entity.setEnabled(value.isEnabled());
        entity.setName(value.getName());
        entity.setAtts(value.getAtts());
        entity.setValidityPeriod(value.getValidityPeriod());
        entity.setDigest(value.getDigest());
        entity.setNote(value.getNote());

        return entity;
    }
}
