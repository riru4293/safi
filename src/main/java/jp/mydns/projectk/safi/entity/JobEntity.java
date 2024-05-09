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
package jp.mydns.projectk.safi.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.ContentKind;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.Filtdef;
import jp.mydns.projectk.safi.value.JsonArrayVo;
import jp.mydns.projectk.safi.value.JsonObjectVo;
import jp.mydns.projectk.safi.value.Plugdef;

/**
 * JPA entity for the <i>t_job</i> table.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Cacheable(false)
@Table(name = "t_job")
public class JobEntity extends CommonEntity {

    private static final long serialVersionUID = 3415007914119962988L;

    /**
     * Job id.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 26)
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 26)
    private String id;

    /**
     * Job status.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status;

    /**
     * Schedule of the job execution.
     *
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    @Basic(optional = false)
    @Column(name = "schedule_ts", nullable = false, updatable = false)
    private LocalDateTime scheduleTime;

    /**
     * Time limit of the job execution.
     *
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    @Basic(optional = false)
    @Column(name = "limit_ts", nullable = false, updatable = false)
    private LocalDateTime limitTime;

    /**
     * Job kind.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "kind", nullable = false, updatable = false, length = 20)
    private JobKind kind;

    /**
     * Content kind.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "content_kind", nullable = false, updatable = false, length = 20)
    private ContentKind contentKind;

    /**
     * The {@link Plugdef} as JSON.
     *
     * @since 1.0.0
     */
    @Basic(optional = false)
    @Column(name = "plugdef", nullable = false, updatable = false)
    private JsonObjectVo plugdef;

    /**
     * The {@link Filtdef} as JSON.
     *
     * @since 1.0.0
     */
    @Basic(optional = false)
    @Column(name = "filtdef", nullable = false, updatable = false)
    private JsonObjectVo filtdef;

    /**
     * Transform definitions.
     *
     * @since 1.0.0
     */
    @Basic(optional = false)
    @Column(name = "trnsdef", nullable = false, updatable = false)
    private JsonObjectVo trnsdef;

    /**
     * Begin time of the job execution.
     *
     * @since 1.0.0
     */
    @TimeRange
    @TimeAccuracy
    @Column(name = "begin_ts", insertable = false)
    private LocalDateTime beginTime;

    /**
     * End time of the job execution.
     *
     * @since 1.0.0
     */
    @TimeRange
    @TimeAccuracy
    @Column(name = "end_ts", insertable = false)
    private LocalDateTime endTime;

    /**
     * Result messages.
     *
     * @since 1.0.0
     */
    @Column(name = "messages", insertable = false)
    private JsonArrayVo messages;

    /**
     * Schedule-definition id.
     *
     * @since 1.0.0
     */
    @Size(max = 36)
    @Column(name = "schedef_id", updatable = false, length = 36)
    private String schedefId;

    /**
     * Job definition id.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Basic(optional = false)
    @Column(name = "jobdef_id", nullable = false, updatable = false, length = 36)
    private String jobdefId;

    /**
     * Optional configuration.
     *
     * @since 1.0.0
     */
    @NotNull
    @Basic(optional = false)
    @Column(name = "options", nullable = false, updatable = false)
    private JsonObjectVo options;

    /**
     * Constructs a new entity with all properties are default value.
     *
     * @since 1.0.0
     */
    public JobEntity() {
    }

    /**
     * Get job id.
     *
     * @return job id
     * @since 1.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * Set job id. It is impossible to update an already persisted value.
     *
     * @param id job id
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get job status.
     *
     * @return job status
     * @since 1.0.0
     */
    public JobStatus getStatus() {
        return status;
    }

    /**
     * Set job status.
     *
     * @param status job status
     * @since 1.0.0
     */
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /**
     * Get job schedule time.
     *
     * @return job schedule time
     * @since 1.0.0
     */
    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }

    /**
     * Set job schedule time. It is impossible to update an already persisted value.
     *
     * @param scheduleTime job schedule time
     * @since 1.0.0
     */
    public void setScheduleTime(LocalDateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    /**
     * Get limit time at job execution.
     *
     * @return limit time
     * @since 1.0.0
     */
    public LocalDateTime getLimitTime() {
        return limitTime;
    }

    /**
     * Set limit time at job execution. It is impossible to update an already persisted value.
     *
     * @param limitTime limit time
     * @since 1.0.0
     */
    public void setLimitTime(LocalDateTime limitTime) {
        this.limitTime = limitTime;
    }

    /**
     * Get job kind.
     *
     * @return job kind
     * @since 1.0.0
     *
     */
    public JobKind getKind() {
        return kind;
    }

    /**
     * Set job kind. It is impossible to update an already persisted value.
     *
     * @param kind job kind
     * @since 1.0.0
     */
    public void setKind(JobKind kind) {
        this.kind = kind;
    }

    /**
     * Get content kind of job.
     *
     * @return content type
     * @since 1.0.0
     */
    public ContentKind getContentKind() {
        return contentKind;
    }

    /**
     * Set content kind of job. It is impossible to update an already persisted value.
     *
     * @param contentKind content kind
     * @since 1.0.0
     */
    public void setContentKind(ContentKind contentKind) {
        this.contentKind = contentKind;
    }

    /**
     * Get the {@link Plugdef} as JSON.
     *
     * @return the {@code Plugdef} as JSON. It may be {@code null}.
     * @since 1.0.0
     */
    @NotNull
    public JsonObjectVo getPlugdef() {
        return plugdef;
    }

    /**
     * Set the {@link Plugdef} as JSON. It is impossible to update an already persisted value.
     *
     * @param plugdef the {@code Plugdef} as JSON. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setPlugdef(JsonObjectVo plugdef) {
        this.plugdef = plugdef;
    }

    /**
     * Get the {@link Filtdef} as JSON.
     *
     * @return the {@code Filtdef} as JSON. It may be {@code null}.
     * @since 1.0.0
     */
    public JsonObjectVo getFiltdef() {
        return filtdef;
    }

    /**
     * Set the {@link Filtdef} as JSON. It is impossible to update an already persisted value.
     *
     * @param filtdef the {@code Filtdef} as JSON. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setFiltdef(JsonObjectVo filtdef) {
        this.filtdef = filtdef;
    }

    /**
     * Get transformation definition.
     *
     * @return transformation definition. It may be {@code null}.
     * @since 1.0.0
     */
    public JsonObjectVo getTrnsdef() {
        return trnsdef;
    }

    /**
     * Set transformation definition. It is impossible to update an already persisted value.
     *
     * @param trnsdef transformation definition. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setTrnsdef(JsonObjectVo trnsdef) {
        this.trnsdef = trnsdef;
    }

    /**
     * Get begin time at job execution.
     *
     * @return begin time. {@code null} if before begin Job execution.
     * @since 1.0.0
     */
    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    /**
     * Set begin time at job execution. It is impossible to insert new.
     *
     * @param beginTime begin time
     * @since 1.0.0
     */
    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * Get end time at job execution.
     *
     * @return end time. {@code null} if before end Job execution.
     * @since 1.0.0
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Set end time at job execution. It is impossible to insert new.
     *
     * @param endTime end time
     * @since 1.0.0
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Get schedule definition id.
     *
     * @return schedule definition id. It may be {@code null}.
     * @since 1.0.0
     */
    @Size(min = 1, max = 36)
    public String getSchedefId() {
        return schedefId;
    }

    /**
     * Set schedule definition id. It is impossible to update an already persisted value.
     *
     * @param schedefId schedule definition id
     * @since 1.0.0
     */
    public void setSchedefId(String schedefId) {
        this.schedefId = schedefId;
    }

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 1.0.0
     */
    public String getJobdefId() {
        return jobdefId;
    }

    /**
     * Set job definition id. It is impossible to update an already persisted value.
     *
     * @param jobdefId job definition id
     * @since 1.0.0
     */
    public void setJobdefId(String jobdefId) {
        this.jobdefId = jobdefId;
    }

    /**
     * Get optional properties.
     *
     * @return optional properties
     * @since 1.0.0
     */
    public JsonObjectVo getOptions() {
        return options;
    }

    /**
     * Set optional properties.
     *
     * @param options optional properties. It is impossible to update an already persisted value.
     * @since 1.0.0
     */
    public void setOptions(JsonObjectVo options) {
        this.options = options;
    }

    /**
     * Get result messages.
     *
     * @return result messages. It may be {@code null}.
     * @since 1.0.0
     */
    public JsonArrayVo getMessages() {
        return messages;
    }

    /**
     * Set result messages. It is impossible to insert new.
     *
     * @param messages result messages
     * @since 1.0.0
     */
    public void setMessages(JsonArrayVo messages) {
        this.messages = messages;
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value. It is generated from the primary key value.
     * @since 1.0.0
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
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof JobEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "JobEntity{" + "id=" + id + ", status=" + status
                + ", scheduleTime=" + scheduleTime + ", limitTime=" + limitTime
                + ", kind=" + kind + ", contentKind=" + contentKind
                + ", beginTime=" + beginTime + ", endTime=" + endTime
                + ", schedefId=" + schedefId + ", jobdefId=" + jobdefId + '}';
    }
}
