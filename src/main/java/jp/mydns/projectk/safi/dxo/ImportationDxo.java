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
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.AttKey;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.entity.ImportationWorkEntity;
import jp.mydns.projectk.safi.service.AppTimeService;
import static jp.mydns.projectk.safi.util.LambdaUtils.alwaysThrow;
import static jp.mydns.projectk.safi.util.LambdaUtils.f;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.validator.Unsafe;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.TransResult;
import jp.mydns.projectk.safi.value.ValidityPeriod;
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
public interface ImportationDxo<E extends ContentEntity<E>, V extends ContentValue<V>> {

    /**
     * Build a working entity from importation value.
     *
     * @param importValue the {@code ImportationValue}
     * @return the {@code ImportationWorkEntity}
     * @throws NullPointerException if {@code importValue} is {@code null}
     * @throws IllegalArgumentException if {@code importValue} represents an explicit deletion
     * @since 1.0.0
     */
    ImportationWorkEntity toImportationWorkEntity(ImportationValue<V> importValue);

    /**
     * Build an importation value from the transform result. If {@code transResult} indicates an explicit deletion, the
     * expiration date-time is rewritten in the past, resulting in deleted content.
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
     * If contains a paired entity instance in {@code importValue}, returns the entity constructed based on that
     * instance.
     *
     * @param importValue the {@code ImportationValue}
     * @return the {@code ContentEntity}
     * @throws NullPointerException if {@code importValue} is {@code null}
     * @since 1.0.0
     */
    E toEntity(ImportationValue<V> importValue);

    abstract class AbstractImportationDxo<E extends ContentEntity<E>, V extends ContentValue<V>>
            implements ImportationDxo<E, V> {

        @Inject
        private Validator validator;

        @Inject
        private AppTimeService appTimeSvc;

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code importValue} is {@code null}
         * @throws IllegalArgumentException if {@code importValue} represents an explicit deletion
         * @since 1.0.0
         */
        @Override
        public ImportationWorkEntity toImportationWorkEntity(ImportationValue<V> importValue) {
            Objects.requireNonNull(importValue);

            var e = new ImportationWorkEntity();
            e.setId(importValue.getId());
            e.setDigest(importValue.getContent().getDigest());

            return e;
        }

        /**
         * Build the {@code ValidityPeriod} from key-value content. Use key values ​​from {@code from}, {@code to},
         * {@code ban}. Use the default value if not exists a value in key-value content. If {@code value} indicates an
         * explicit deletion, the expiration date-time is rewritten in the past.
         *
         * @param value key-value content
         * @return the {@code ValidityPeriod}. Please note, it has not been verified.
         * @throws NullPointerException if {@code value} is {@code null}
         * @see ValidityPeriod#defaultFrom()
         * @see ValidityPeriod#defaultTo()
         * @see ValidityPeriod#defaultBan()
         * @since 1.0.0
         */
        protected ValidityPeriod toValidityPeriod(Map<String, String> value) {
            Objects.requireNonNull(value);

            return new ValidityPeriod.Builder()
                    .withFrom(TimeUtils.tryToLocalDateTime(
                            value.get("from")).map(TimeUtils::toOffsetDateTime).orElseGet(ValidityPeriod::defaultFrom))
                    .withTo(!isExplicitDeletion(value)
                            ? TimeUtils.tryToLocalDateTime(value.get("to")).map(TimeUtils::toOffsetDateTime)
                                    .orElseGet(ValidityPeriod::defaultFrom)
                            : appTimeSvc.getOffsetNow().minusSeconds(1))
                    .withBan(Optional.ofNullable(
                            value.get("ban")).map(Boolean::valueOf).orElseGet(ValidityPeriod::defaultBan))
                    .build(validator, Unsafe.class);
        }

        /**
         * Determine explicit deletion. Use key values ​​from {@code doDelete}.
         *
         * @param value key-value content
         * @return {@code true} if means explicit deletion, otherwise {@code false}.
         * @since 1.0.0
         */
        private boolean isExplicitDeletion(Map<String, String> value) {
            return Boolean.parseBoolean(value.get("doDelete"));
        }

        /**
         * Build the attributes from key-value content. Use key values ​​from the {@link AttKey#toString()}.
         *
         * @param value key-value content
         * @return attributes
         * @throws NullPointerException if {@code value} is {@code null}
         * @since 1.0.0
         */
        protected Map<AttKey, String> toAtts(Map<String, String> value) {
            Objects.requireNonNull(value);

            return Stream.of(AttKey.values()).filter(p(value::containsKey, AttKey::toString))
                    .collect(collectingAndThen(toMap(identity(), f(value::get).compose(AttKey::toString),
                            alwaysThrow(), () -> new EnumMap<>(AttKey.class)), Collections::unmodifiableMap));
        }
    }
}
