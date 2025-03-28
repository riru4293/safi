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

import io.azam.ulidj.ULID;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.util.JsonValueUtils;
import jp.mydns.projectk.safi.value.JobCreationContext;
import jp.mydns.projectk.safi.value.JobValue;
import jp.mydns.projectk.safi.value.JobdefValue;
import jp.mydns.projectk.safi.value.JsonWrapper;
import jp.mydns.projectk.safi.value.SchedefValue;

/**
 * Data exchange processing for <i>Job</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface JobDxo {

    /**
     * Exchange to new entity object from creation context.
     *
     * @param ctx the {@code JobCreationContext}. Constraint violations must be none.
     * @return new job entity. It not persisted.
     * @throws NullPointerException if {@code ctx} is {@code null}
     * @since 3.0.0
     */
    JobEntity newEntity(JobCreationContext ctx);

    /**
     * Exchange to entity object from value.
     *
     * @param value job value. Constraint violations must be none.
     * @return the {@code JobEntity}
     * @throws NullPointerException if {@code entity} is {@code null}
     * @since 3.0.0
     */
    JobEntity toEntity(JobValue value);

    /**
     * Exchange to value object from entity.
     *
     * @param entity the {@code JobEntity}. Constraint violations must be none.
     * @return the {@code JobValue}
     * @throws NullPointerException if {@code entity} is {@code null}
     * @since 3.0.0
     */
    JobValue toValue(JobEntity entity);

    /**
     * Implements of the {@code JobDxo}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Typed(JobDxo.class)
    @RequestScoped
    class Impl extends ValidityPeriodDxo implements JobDxo {

        private final JsonService jsonSvc;

        /**
         * Constructor.
         *
         * @param jsonSvc the {@code JsonService}
         * @since 3.0.0
         */
        @Inject
        public Impl(JsonService jsonSvc) {
            this.jsonSvc = jsonSvc;
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code ctx} is {@code null}
         * @since 3.0.0
         */
        @Override
        public JobEntity newEntity(JobCreationContext ctx) {
            Objects.requireNonNull(ctx);

            var entity = new JobEntity();

            entity.setId(ULID.random());
            entity.setStatus(JobStatus.SCHEDULE);
            entity.setScheduleTime(ctx.getScheduleTime());
            entity.setLimitTime(ctx.getScheduleTime().plus(ctx.getJobdef().getTimeout()));
            entity.setProperties(JsonWrapper.of(ctx.getJobdef().getJobProperties()));
            entity.setJobdef(JsonWrapper.of(jsonSvc.toJsonValue(ctx.getJobdef())));

            return entity;
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code entity} is {@code null}
         * @since 3.0.0
         */
        @Override
        public JobEntity toEntity(JobValue value) {
            Objects.requireNonNull(value);

            var entity = new JobEntity();

            entity.setId(value.getId());
            entity.setStatus(value.getStatus());
            entity.setKind(value.getKind());
            entity.setTarget(value.getTarget());
            entity.setScheduleTime(TimeUtils.toLocalDateTime(value.getScheduleTime()));
            entity.setLimitTime(TimeUtils.toLocalDateTime(value.getLimitTime()));
            entity.setBeginTime(TimeUtils.toLocalDateTime(value.getBeginTime().orElse(null)));
            entity.setEndTime(TimeUtils.toLocalDateTime(value.getEndTime().orElse(null)));
            entity.setProperties(JsonWrapper.of(value.getProperties()));
            entity.setJobdefId(value.getJobdefId());
            entity.setJobdef(JsonWrapper.of(jsonSvc.toJsonValue(value.getJobdef())));
            entity.setSchedefId(value.getSchedefId().orElse(null));
            entity.setSchedef(value.getSchedef().map(jsonSvc::toJsonValue).map(JsonWrapper::of).orElse(null));
            entity.setResultMessages(value.getResultMessages().map(jsonSvc::toJsonValue).map(JsonWrapper::of)
                .orElse(null));
            entity.setNote(value.getNote().orElse(null));
            entity.setVersion(value.getVersion());
            entity.setRegTime(value.getRegisterTime().map(TimeUtils::toLocalDateTime).orElse(null));
            entity.setRegId(value.getRegisterAccountId().orElse(null));
            entity.setRegName(value.getRegisterProcessName().orElse(null));
            entity.setUpdTime(value.getUpdateTime().map(TimeUtils::toLocalDateTime).orElse(null));
            entity.setUpdId(value.getUpdateAccountId().orElse(null));
            entity.setUpdName(value.getUpdateProcessName().orElse(null));

            return entity;
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code entity} is {@code null}
         * @since 3.0.0
         */
        @Override
        public JobValue toValue(JobEntity entity) {
            Objects.requireNonNull(entity);

            return new JobValue.Builder()
                .withId(entity.getId())
                .withStatus(entity.getStatus())
                .withKind(entity.getKind())
                .withTarget(entity.getTarget())
                .withScheduleTime(TimeUtils.toOffsetDateTime(entity.getScheduleTime()))
                .withLimitTime(TimeUtils.toOffsetDateTime(entity.getLimitTime()))
                .withBeginTime(TimeUtils.toOffsetDateTime(entity.getBeginTime()))
                .withEndTime(TimeUtils.toOffsetDateTime(entity.getEndTime()))
                .withProperties(entity.getProperties().unwrap().asJsonObject())
                .withJobdefId(entity.getJobdefId())
                .withJobdef(jsonSvc.fromJsonValue(entity.getJobdef().unwrap(), JobdefValue.class))
                .withSchedefId(entity.getSchedefId())
                .withSchedef(jsonSvc.fromJsonValue(entity.getSchedef().unwrap(), SchedefValue.class))
                .withResultMessages(entity.getResultMessages().unwrap().asJsonArray().stream()
                    .map(JsonValueUtils::toString).toList())
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
    }
}
