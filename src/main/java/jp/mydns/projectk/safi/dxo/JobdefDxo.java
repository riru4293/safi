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
import java.util.Map;
import java.util.Objects;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.ValidationService;
import static jp.mydns.projectk.safi.util.LambdaUtils.compute;
import static jp.mydns.projectk.safi.util.LambdaUtils.toLinkedHashMap;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.util.JsonValueUtils;
import jp.mydns.projectk.safi.value.FiltdefValue;
import jp.mydns.projectk.safi.value.JobdefValue;
import jp.mydns.projectk.safi.value.JsonWrapper;

/**
 * Data exchange processing for <i>Job definition</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class JobdefDxo extends ValidityPeriodDxo {

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
     * Exchange to entity object from value.
     *
     * @param value job definition value. Constraint violations must be none.
     * @return the {@code JobdefEntity}
     * @throws NullPointerException if {@code entity} is {@code null}
     * @since 3.0.0
     */
    public JobdefEntity toEntity(JobdefValue value) {
        var entity = new JobdefEntity();

        entity.setId(value.getId());
        entity.setValidityPeriod(toValidityPeriodEmb(value.getValidityPeriod()));
        entity.setJobKind(value.getJobKind());
        entity.setJobTarget(value.getJobTarget());
        entity.setTimeout(value.getTimeout());
        entity.setName(value.getName().orElse(null));
        entity.setPluginName(value.getPluginName().orElse(null));
        entity.setTrnsdef(JsonWrapper.of(jsonSvc.toJsonValue(value.getTrnsdef())));
        entity.setFiltdef(JsonWrapper.of(jsonSvc.toJsonValue(value.getFiltdef())));
        entity.setJobProperties(JsonWrapper.of(value.getJobProperties()));
        entity.setNote(value.getNote().orElse(null));
        entity.setVersion(value.getVersion());
        entity.setRegTime(TimeUtils.toLocalDateTime(value.getRegisterTime().orElse(null)));
        entity.setRegId(value.getRegisterAccountId().orElse(null));
        entity.setRegName(value.getRegisterProcessName().orElse(null));
        entity.setUpdTime(TimeUtils.toLocalDateTime(value.getUpdateTime().orElse(null)));
        entity.setUpdId(value.getUpdateAccountId().orElse(null));
        entity.setUpdName(value.getUpdateProcessName().orElse(null));

        return entity;
    }

    /**
     * Exchange to value object from JSON.
     *
     * @param json job definition as JSON
     * @return the {@code Jobdef}
     * @throws NullPointerException if {@code json} is {@code null}
     * @throws ConstraintViolationException if {@code json} has constraint violation
     * @since 3.0.0
     */
    public JobdefValue toValue(JsonObject json) {
        return validationSvc.requireValid(jsonSvc.fromJsonValue(Objects.requireNonNull(json), JobdefValue.class));
    }

    /**
     * Exchange to value object from entity.
     *
     * @param entity the {@code JobdefEntity}. Constraint violations must be none.
     * @return the {@code Jobdef}
     * @throws NullPointerException if {@code entity} is {@code null}
     * @since 3.0.0
     */
    public JobdefValue toValue(JobdefEntity entity) {
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
            .withJobProperties(entity.getJobProperties().unwrap().asJsonObject())
            .withNote(entity.getNote())
            .withVersion(entity.getVersion())
            .withRegisterTime(TimeUtils.toOffsetDateTime(entity.getRegTime()))
            .withRegisterAccountId(entity.getRegId())
            .withRegisterProcessName(entity.getRegName())
            .withUpdateTime(TimeUtils.toOffsetDateTime(entity.getUpdTime()))
            .withUpdateAccountId(entity.getUpdId())
            .withUpdateProcessName(entity.getUpdName())
            .unsafeBuild();
    }

    private Map<String, String> toTrnsdef(JsonWrapper json) {
        return json.unwrap().asJsonObject().entrySet().stream().map(compute(JsonValueUtils::toString))
            .collect(toLinkedHashMap());
    }

    private FiltdefValue toFiltdef(JsonWrapper json) {
        return jsonSvc.fromJsonValue(jsonSvc.toJsonValue(json), FiltdefValue.class);
    }
}
