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
package jp.mydns.projectk.safi.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.JsonArrayValue;
import jp.mydns.projectk.safi.value.JsonObjectValue;

/**
 * JPA entity for the <i>t_job</i> table.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Entity
@Cacheable(false)
@Table(name = "t_job")
public class JobEntity implements Serializable {

    private static final long serialVersionUID = -1878103273727614325L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Basic(optional = false)
    @Column(name = "stat", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Basic(optional = false)
    @Column(name = "kind", nullable = false, updatable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobKind kind;

    @Basic(optional = false)
    @Column(name = "target", nullable = false, updatable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobTarget target;

    @Basic(optional = false)
    @Column(name = "sche_ts", nullable = false, updatable = false)
    private LocalDateTime scheduleTime;

    @Basic(optional = false)
    @Column(name = "limit_ts", nullable = false, updatable = false)
    private LocalDateTime limitTime;

    @Column(name = "begin_ts", insertable = false)
    private LocalDateTime beginTime;

    @Column(name = "end_ts", insertable = false)
    private LocalDateTime endTime;

    @Basic(optional = false)
    @Column(name = "props", updatable = false)
    private JsonObjectValue properties;

    @Column(name = "jobdef_id", updatable = false, length = 36)
    private String jobdefId;

    @Column(name = "schedef_id", updatable = false, length = 36)
    private String schedefId;

    @Column(name = "srcdefs", updatable = false)
    private JsonObjectValue definitions;

    @Column(name = "results", insertable = false)
    private JsonArrayValue resultMessages;

    @Column(name = "note")
    private String note;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "reg_ts", updatable = false)
    private LocalDateTime registerTime;

    @Column(name = "reg_id", updatable = false, length = 250)
    private String registerAccountId;

    @Column(name = "reg_ap", updatable = false, length = 250)
    private String registerProcessName;

    @Column(name = "upd_ts", insertable = false)
    private LocalDateTime updateTime;

    @Column(name = "upd_id", insertable = false, length = 250)
    private String updateAccountId;

    @Column(name = "upd_ap", insertable = false, length = 250)
    private String updateProcessName;

    /**
     * Get job id.
     *
     * @return job id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    public String getId() {
        return id;
    }

    /**
     * Set job id.
     *
     * @param id job id. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get job status.
     *
     * @return job status
     * @since 3.0.0
     */
    @NotNull
    public JobStatus getStatus() {
        return status;
    }

    /**
     * Set job status.
     *
     * @param status job status
     * @since 3.0.0
     */
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /**
     * Get job kind.
     *
     * @return job kind
     * @since 3.0.0
     */
    @NotNull
    public JobKind getKind() {
        return kind;
    }

    /**
     * Set job kind.
     *
     * @param kind job kind. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setKind(JobKind kind) {
        this.kind = kind;
    }

    /**
     * Get job target.
     *
     * @return job target
     * @since 3.0.0
     */
    @NotNull
    public JobTarget getTarget() {
        return target;
    }

    /**
     * Set job target.
     *
     * @param target job target. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setTarget(JobTarget target) {
        this.target = target;
    }

    /**
     * Get job schedule time.
     *
     * @return job schedule time
     * @since 3.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }

    /**
     * Set job schedule time.
     *
     * @param scheduleTime job schedule time. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setScheduleTime(LocalDateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    /**
     * Get limit time at job execution.
     *
     * @return limit time
     * @since 3.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getLimitTime() {
        return limitTime;
    }

    /**
     * Set limit time at job execution.
     *
     * @param limitTime limit time. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setLimitTime(LocalDateTime limitTime) {
        this.limitTime = limitTime;
    }

    /**
     * Get begin time at job execution.
     *
     * @return begin time. {@code null} if before begin job execution. {@code null} if yet begun.
     * @since 3.0.0
     */
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    /**
     * Set begin time at job execution.
     *
     * @param beginTime begin time. Cannot insert new.
     * @since 3.0.0
     */
    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * Get end time at job execution.
     *
     * @return end time. {@code null} if before end job execution. {@code null} if yet end.
     * @since 3.0.0
     */
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Set end time at job execution. It is impossible to insert new.
     *
     * @param endTime end time. Cannot insert new.
     * @since 3.0.0
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Get job properties.
     *
     * @return job properties
     * @since 3.0.0
     */
    @NotNull
    public JsonObjectValue getProperties() {
        return properties;
    }

    /**
     * Set job properties.
     *
     * @param properties job properties. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setProperties(JsonObjectValue properties) {
        this.properties = properties;
    }

    /**
     * Get job definition id.
     *
     * @return job definition id. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 36)
    public String getJobdefId() {
        return jobdefId;
    }

    /**
     * Set job definition id.
     *
     * @param jobdefId job definition id. Cannot update persisted value. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setJobdefId(String jobdefId) {
        this.jobdefId = jobdefId;
    }

    /**
     * Get schedule definition id.
     *
     * @return schedule definition id. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 36)
    public String getSchedefId() {
        return schedefId;
    }

    /**
     * Set schedule definition id.
     *
     * @param schedefId schedule definition id. Cannot update persisted value. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setSchedefId(String schedefId) {
        this.schedefId = schedefId;
    }

    /**
     * Get source definitions.
     *
     * @return source definitions. May contain a job definition under the key <@code jobdef> and a schedule definition
     * under the key <@code schedef>.
     * @since 3.0.0
     */
    public JsonObjectValue getDefinitions() {
        return definitions;
    }

    /**
     * Set source definitions.
     *
     * @param definitions source definitions. May contain a job definition under the key <@code jobdef> and a schedule
     * definition under the key <@code schedef>.
     * @since 3.0.0
     */
    public void setDefinitions(JsonObjectValue definitions) {
        this.definitions = definitions;
    }

    /**
     * Get result messages.
     *
     * @return result messages
     * @since 3.0.0
     */
    public JsonArrayValue getResultMessages() {
        return resultMessages;
    }

    /**
     * Set result messages.
     *
     * @param resultMessages result messages
     * @since 3.0.0
     */
    public void setResultMessages(JsonArrayValue resultMessages) {
        this.resultMessages = resultMessages;
    }

    /**
     * Get a note for this entity. This value is only used to record notes about the data records represented by the
     * entity and is never used to process.
     *
     * @return note. It may be {@code null}.
     * @since 3.0.0
     */
    public String getNote() {
        return note;
    }

    /**
     * Set a note for this entity.
     *
     * @param note note. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Get entity version. This value used for optimistic locking. Will be thrown {@link OptimisticLockException} when
     * inserted or updated when the configured version number is not equal to that of the database. The version of the
     * entity before persistence is 0.
     *
     * @return entity version
     * @since 3.0.0
     */
    @PositiveOrZero
    @Version
    public int getVersion() {
        return version;
    }

    /**
     * Set entity version.
     *
     * @param version entity version
     * @since 3.0.0
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Get time the entity was persisted.
     *
     * @return persisted time. It time zone is UTC. It may be {@code null}.
     * @since 3.0.0
     */
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    /**
     * Set time the entity was persisted.
     *
     * @param registerTime persisted time. It time zone is UTC. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    /**
     * Get id of the account who made the entity persistent.
     *
     * @return persisted account id. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getRegisterAccountId() {
        return registerAccountId;
    }

    /**
     * Set id of the account who made the entity persistent.
     *
     * @param registerAccountId persisted account id. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setRegisterAccountId(String registerAccountId) {
        this.registerAccountId = registerAccountId;
    }

    /**
     * Get name of the process who made the entity persistent.
     *
     * @return persisted process name. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getRegisterProcessName() {
        return registerProcessName;
    }

    /**
     * Set name of the process who made the entity persistent.
     *
     * @param registerProcessName persisted process name. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setRegisterProcessName(String registerProcessName) {
        this.registerProcessName = registerProcessName;
    }

    /**
     * Get time the entity was last updated.
     *
     * @return last updated time. It time zone is UTC. It may be {@code null}.
     * @since 3.0.0
     */
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * Set time the entity was last updated.
     *
     * @param updateTime last updated time. It time zone is UTC. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Get id of the account who made the entity last updated.
     *
     * @return last updated account id. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getUpdateAccountId() {
        return updateAccountId;
    }

    /**
     * Set id of the account who made the entity last updated.
     *
     * @param updateAccountId last updated account id. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setUpdateAccountId(String updateAccountId) {
        this.updateAccountId = updateAccountId;
    }

    /**
     * Get name of the process who made the entity last updated.
     *
     * @return last updated process name. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getUpdateProcessName() {
        return updateProcessName;
    }

    /**
     * Set name of the process who made the entity last updated.
     *
     * @param updateProcessName last updated process name. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setUpdateProcessName(String updateProcessName) {
        this.updateProcessName = updateProcessName;
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value. It is generated from the primary key value.
     * @since 3.0.0
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Indicates that other object is equal to this instance. Equality means that can be cast to this class and primary
     * key is match.
     *
     * @param other an any object
     * @return {@code true} if equals, otherwise {@code false}.
     * @since 3.0.0
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof JobEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "JobEntity{" + "id=" + id + ", status=" + status + ", kind=" + kind + ", target=" + target
            + ", scheduleTime=" + scheduleTime + ", limitTime=" + limitTime + ", beginTime=" + beginTime
            + ", endTime=" + endTime + ", properties=" + properties + ", jobdefId=" + jobdefId
            + ", schedefId=" + schedefId + ", definitions=" + definitions + ", resultMessages=" + resultMessages + '}';
    }
}
