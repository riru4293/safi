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

import io.azam.ulidj.ULID;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.entity.JobEntity;
import jp.mydns.projectk.safi.entity.JobrecEntity;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.ValidationService;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.ContentRecord;
import jp.mydns.projectk.safi.value.Filtdef;
import jp.mydns.projectk.safi.value.Job;
import jp.mydns.projectk.safi.value.JsonArrayVo;
import jp.mydns.projectk.safi.value.JsonObjectVo;
import jp.mydns.projectk.safi.value.Plugdef;

/**
 * Data exchange processing for <i>Job</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class JobDxo {

    @Inject
    private JsonService jsonSvc;

    @Inject
    private ValidationService validSvc;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected JobDxo() {
    }

    /**
     * Build a value based on the entity.
     *
     * @param entity the {@code JobEntity}
     * @return the {@code Job} that built
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws ConstraintViolationException if built value has constraint violation
     * @since 1.0.0
     */
    public Job toValue(JobEntity entity) {
        Objects.requireNonNull(entity);

        var builder = new Job.Builder();
        Optional<JobEntity> optEntity = Optional.of(entity);
        Optional.ofNullable(entity.getPlugdef()).map(v -> jsonSvc.convertViaJson(v, Plugdef.class)).ifPresent(builder::withPlugdef);
        Optional.ofNullable(entity.getFiltdef()).map(v -> jsonSvc.convertViaJson(v, Filtdef.class)).ifPresent(builder::withFiltdef);
        Optional.ofNullable(entity.getTrnsdef()).map(jsonSvc::toStringMap).ifPresent(builder::withTrnsdef);
        optEntity.map(JobEntity::getBeginTime).map(TimeUtils::toOffsetDateTime).ifPresent(builder::withBeginTime);
        optEntity.map(JobEntity::getEndTime).map(TimeUtils::toOffsetDateTime).ifPresent(builder::withEndTime);
        optEntity.map(JobEntity::getMessages).map(jsonSvc::toStringList).ifPresent(builder::withMessages);
        optEntity.map(JobEntity::getSchedefId).ifPresent(builder::withSchedefId);

        return builder
                .withId(entity.getId())
                .withStatus(entity.getStatus())
                .withScheduleTime(TimeUtils.toOffsetDateTime(entity.getScheduleTime()))
                .withLimitTime(TimeUtils.toOffsetDateTime(entity.getLimitTime()))
                .withKind(entity.getKind())
                .withContentKind(entity.getContentKind())
                .withOptions(entity.getOptions())
                .withPersistenceContext(entity.toPersistenceContext())
                .withNote(entity.getNote())
                .build(validSvc.getValidator());
    }

    /**
     * Convert to entity from value.
     *
     * @param value the {@code Job}
     * @return the {@code JobEntity}
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws ConstraintViolationException if {@code value} has constraint violation
     * @since 1.0.0
     */
    public JobEntity toEntity(Job value) {
        Optional<Job> optValue = Optional.of(validSvc.requireValid(Objects.requireNonNull(value)));

        var entity = new JobEntity();
        entity.setId(value.getId());
        entity.setStatus(value.getStatus());
        entity.setScheduleTime(TimeUtils.toLocalDateTime(value.getScheduleTime()));
        entity.setLimitTime(TimeUtils.toLocalDateTime(value.getLimitTime()));
        entity.setKind(value.getKind());
        entity.setContentKind(value.getContentKind());
        optValue.map(Job::getPlugdef).map(jsonSvc::toJsonObjectVo).ifPresent(entity::setPlugdef);
        optValue.map(Job::getFiltdef).map(jsonSvc::toJsonObjectVo).ifPresent(entity::setFiltdef);
        optValue.map(Job::getTrnsdef).map(jsonSvc::toJsonObjectVo).ifPresent(entity::setTrnsdef);
        optValue.flatMap(Job::getBeginTime).map(TimeUtils::toLocalDateTime).ifPresent(entity::setBeginTime);
        optValue.flatMap(Job::getEndTime).map(TimeUtils::toLocalDateTime).ifPresent(entity::setEndTime);
        optValue.flatMap(Job::getMessages).map(jsonSvc::toJsonStringList).map(JsonArrayVo::new).ifPresent(entity::setMessages);
        optValue.flatMap(Job::getSchedefId).ifPresent(entity::setSchedefId);
        entity.setOptions(jsonSvc.toJsonObjectVo(value.getOptions()));
        optValue.flatMap(Job::getPersistenceContext).ifPresent(entity::applyPersistenceContext);
        entity.setNote(value.getNote());

        return entity;
    }

    /**
     * Convert to {@code JobrecEntity} from {@code ContentRecord}.
     *
     * @param jobId job id
     * @param record the {@code ContentRecord}
     * @return the {@code JobrecEntity}
     * @throws NullPointerException if any argument is {@code null}
     * @throws ConstraintViolationException if built entity has constraint violation
     * @since 1.0.0
     */
    public JobrecEntity toRecoedEntity(String jobId, ContentRecord record) {
        Objects.requireNonNull(jobId);
        Objects.requireNonNull(record);

        var entity = new JobrecEntity();
        entity.setId(generateJobrecId());
        entity.setJobId(jobId);
        entity.setContentId(record.getId());
        entity.setContentValue(new JsonObjectVo(record.getValue()));
        entity.setContentFormat(record.getFormat());
        entity.setKind(record.getKind());
        entity.setFailurePhase(record.getFailurePhase());
        entity.setMessage(record.getMessage());

        return validSvc.requireValid(entity);
    }

    String generateJobrecId() {
        return ULID.random();
    }
}
