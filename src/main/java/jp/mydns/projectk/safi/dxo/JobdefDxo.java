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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.ValidationService;
import static jp.mydns.projectk.safi.util.CollectionUtils.convertValues;
import static jp.mydns.projectk.safi.util.CollectionUtils.toLinkedHashMap;
import jp.mydns.projectk.safi.util.JsonValueUtils;
import static jp.mydns.projectk.safi.util.LambdaUtils.f;
import static jp.mydns.projectk.safi.util.TimeUtils.toLocalDateTime;
import static jp.mydns.projectk.safi.util.TimeUtils.toOffsetDateTime;
import jp.mydns.projectk.safi.value.FiltdefValue;
import jp.mydns.projectk.safi.value.JobdefValue;
import jp.mydns.projectk.safi.value.SJson;

/**
 <i>Job definition</i> data exchange processing.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JobdefDxo {

/**
 Exchange to entity object from value.

 @param value job definition value. Constraint violations must be none.
 @return the {@code JobdefEntity}
 @throws NullPointerException if {@code entity} is {@code null}
 @since 3.0.0
 */
JobdefEntity toEntity(JobdefValue value);

/**
 Exchange to value object from JSON.

 @param json job definition as JSON.
 @return the {@code JobdefValue}.
 @throws NullPointerException if {@code json} is {@code null}.
 @throws ConstraintViolationException if {@code json} has constraint violation.
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
JobdefValue toValue(JsonObject json);

/**
 Exchange to value object from entity.

 @param entity the {@code JobdefEntity}. Constraint violations must be none.
 @return the {@code JobdefValue}.
 @throws NullPointerException if {@code entity} is {@code null}.
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
JobdefValue toValue(JobdefEntity entity);

/**
 Implements of the {@code JobdefDxo}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(JobdefDxo.class)
@ApplicationScoped
class Impl extends ValidityPeriodDxo implements JobdefDxo {

private final ValidationService validSvc;
private final JsonService jsonSvc;

@SuppressWarnings("unused")
Impl() {
    // Note: The default constructor exists only to allow NetBeans to recognize the CDI bean.
    throw new UnsupportedOperationException();
}

@Inject
Impl(ValidationService validSvc, JsonService jsonSvc) {
    this.validSvc = validSvc;
    this.jsonSvc = jsonSvc;
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}.
 @throws JsonbException if any unexpected error(s) occur(s) during conversion..
 @since 3.0.0
 */
@Override
public JobdefEntity toEntity(JobdefValue value) {
    Objects.requireNonNull(value);

    var entity = new JobdefEntity();

    entity.setId(value.getId());
    entity.setValidityPeriod(toValidityPeriodEmb(value.getValidityPeriod()));
    entity.setJobKind(value.getJobKind());
    entity.setJobTarget(value.getJobTarget());
    entity.setTimeout(value.getTimeout());
    entity.setName(value.getName().orElse(null));
    entity.setPluginName(value.getPluginName().orElse(null));
    entity.setTrnsdef(jsonSvc.toSJson(value.getTrnsdef()));
    entity.setFiltdef(jsonSvc.toSJson(value.getFiltdef()));
    entity.setJobProperties(SJson.of(value.getJobProperties()));
    entity.setNote(value.getNote().orElse(null));
    entity.setVersion(value.getVersion());
    entity.setRegTime(toLocalDateTime(value.getRegisterTime().orElse(null)));
    entity.setRegId(value.getRegisterAccountId().orElse(null));
    entity.setRegName(value.getRegisterProcessName().orElse(null));
    entity.setUpdTime(toLocalDateTime(value.getUpdateTime().orElse(null)));
    entity.setUpdId(value.getUpdateAccountId().orElse(null));
    entity.setUpdName(value.getUpdateProcessName().orElse(null));

    return entity;
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code json} is {@code null}.
 @throws ConstraintViolationException if {@code json} has constraint violation.
 @throws JsonbException if any unexpected error(s) occur(s) during conversion.
 @since 3.0.0
 */
@Override
public JobdefValue toValue(JsonObject json) {
    return validSvc.requireValid(jsonSvc.fromJsonValue(Objects.requireNonNull(json),
        JobdefValue.class));
}

/**
 {@inheritDoc}

 @throws NullPointerException if {@code entity} is {@code null}
 @since 3.0.0
 */
@Override
public JobdefValue toValue(JobdefEntity entity) {
    Objects.requireNonNull(entity);

    return new JobdefValue.Builder()
        .withId(entity.getId())
        .withValidityPeriod(toValidityPeriodValue(entity.getValidityPeriod()))
        .withJobKind(entity.getJobKind())
        .withJobTarget(entity.getJobTarget())
        .withTimeout(entity.getTimeout())
        .withName(entity.getName())
        .withPluginName(entity.getPluginName())
        .withTrnsdef(toTrnsdef(entity.getTrnsdef()))
        .withFiltdef(toFiltdef(entity.getFiltdef()))
        .withJobProperties(toJobProps(entity.getJobProperties()))
        .withNote(entity.getNote())
        .withVersion(entity.getVersion())
        .withRegisterTime(toOffsetDateTime(entity.getRegTime()))
        .withRegisterAccountId(entity.getRegId())
        .withRegisterProcessName(entity.getRegName())
        .withUpdateTime(toOffsetDateTime(entity.getUpdTime()))
        .withUpdateAccountId(entity.getUpdId())
        .withUpdateProcessName(entity.getUpdName())
        .unsafeBuild();
}

private JsonObject toJobProps(SJson json) {
    return json.unwrap().asJsonObject();
}

private Map<String, String> toTrnsdef(SJson json) {
    return Optional.ofNullable(json)
        .map(f(JsonValue::asJsonObject).compose(SJson::unwrap))
        .map(convertValues(JsonValueUtils::toString, toLinkedHashMap()))
        .orElseGet(() -> null);
}

private FiltdefValue toFiltdef(SJson json) {
    return Optional.ofNullable(json)
        .map(f(jsonSvc.fromJsonValue(FiltdefValue.class)).compose(SJson::unwrap))
        .orElseGet(() -> null);
}

}

}
