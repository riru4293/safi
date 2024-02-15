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

import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.entity.ImportationWorkEntity;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.TransResult;
import trial.ImportationValue;

/**
 * Data exchange interface for importation processing.
 *
 * @param <E> entity type
 * @param <V> value type
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportationDxo<E extends ContentEntity, V extends ContentValue<V>> {

    /**
     * Exchange to working entity from importation value.
     *
     * @param importValue the {@code ImportationValue}
     * @return the {@code ImportationWorkEntity}
     * @throws NullPointerException if {@code importValue} is {@code null}
     * @throws IllegalArgumentException if {@code importValue} represents an explicit deletion
     * @since 1.0.0
     */
    default ImportationWorkEntity toImportationWorkEntity(ImportationValue<V> importValue) {
        Objects.requireNonNull(importValue);

        var e = new ImportationWorkEntity();
        e.setId(importValue.getId());
        e.setDigest(importValue.getContent().getDigest());

        return e;
    }

    /**
     * Build an importation value from the transform result.
     *
     * @param transResult the {@code TransResult.Success}
     * @return the {@code ImportationWorkEntity}
     * @throws NullPointerException if {@code transResult} is {@code null}
     * @throws ConstraintViolationException if occurred constraint violations when building
     * @since 1.0.0
     */
    ImportationValue<V> toImportationValue(TransResult.Success transResult);

    /**
     * Build an importation value by overwriting the importation value based on the entity.
     * <p>
     * If {@code ImportationValue} represents an explicit delete, then the built one will be in the state of a logical
     * delete. Specifically, if the end date-time of the validity period is after the current time, it will be changed
     * to the past.
     * <p>
     * Used to update persisted content with imported values.
     *
     * @param entity the {@code ContentEntity}
     * @param importValue the {@code ImportationValue}
     * @return the {@code ImportationValue} that built
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    ImportationValue<V> toImportationValue(E entity, ImportationValue<V> importValue);

    /**
     * Convert to entity from value.
     * <p>
     * If contains a paired entity instance in {@code value}, returns the entity constructed based on that instance.
     *
     * @param value the {@code ImportationValue}
     * @return the {@code ContentEntity}
     * @throws NullPointerException if {@code value} is {@code null}
     * @since 1.0.0
     */
    E toEntity(ImportationValue<V> value);
}
