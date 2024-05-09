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
import java.util.Objects;
import jp.mydns.projectk.safi.constant.JobPhase;
import jp.mydns.projectk.safi.constant.RecordKind;
import jp.mydns.projectk.safi.constant.RecordValueFormat;
import jp.mydns.projectk.safi.value.JsonObjectVo;

/**
 * JPA entity for the <i>t_job_record</i> table.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Cacheable(false)
@Table(name = "t_job_record")
public class JobrecEntity extends CommonEntity {

    private static final long serialVersionUID = -4686993282944585639L;

    /**
     * Job record id.
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
     * Job id.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 26)
    @Basic(optional = false)
    @Column(name = "job_id", nullable = false, updatable = false, length = 26)
    private String jobId;

    /**
     * Content id.
     *
     * @since 1.0.0
     */
    @Size(max = 36)
    @Column(name = "content_id", updatable = false, length = 36)
    private String contentId;

    /**
     * Content value.
     *
     * @since 1.0.0
     */
    @NotNull
    @Basic(optional = false)
    @Column(name = "content_val", nullable = false, updatable = false)
    private JsonObjectVo contentValue;

    /**
     * The {@code RecordValueFormat}.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "content_fmt", nullable = false, updatable = false, length = 20)
    private RecordValueFormat contentFormat;

    /**
     * The {@code RecordKind}.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "kind", nullable = false, updatable = false, length = 20)
    private RecordKind kind;

    /**
     * Processing failure phase.
     *
     * @since 1.0.0
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "failure_phase", updatable = false, length = 20)
    private JobPhase failurePhase;

    /**
     * Processing result message.
     *
     * @since 1.0.0
     */
    @Column(name = "message", updatable = false)
    private String message;

    /**
     * Constructs a new entity with all properties are default value.
     *
     * @since 1.0.0
     */
    public JobrecEntity() {
    }

    /**
     * Get job record id.
     *
     * @return job record id
     * @since 1.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * Set job record id. It is impossible to update an already persisted value.
     *
     * @param id job record id
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get job id.
     *
     * @return job id
     * @since 1.0.0
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Set job id. It is impossible to update an already persisted value.
     *
     * @param jobId job id
     * @since 1.0.0
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Get content id.
     *
     * @return content id. It may be {@code null}.
     * @since 1.0.0
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * Set content id. It is impossible to update an already persisted value.
     *
     * @param contentId content id. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    /**
     * Get content value.
     *
     * @return content value
     * @since 1.0.0
     */
    public JsonObjectVo getContentValue() {
        return contentValue;
    }

    /**
     * Set content value. It is impossible to update an already persisted value.
     *
     * @param contentValue content value
     * @since 1.0.0
     */
    public void setContentValue(JsonObjectVo contentValue) {
        this.contentValue = contentValue;
    }

    /**
     * Get the content value format.
     *
     * @return the {@code RecordValueFormat}
     * @since 1.0.0
     */
    public RecordValueFormat getContentFormat() {
        return contentFormat;
    }

    /**
     * Set the content value format. It is impossible to update an already persisted value.
     *
     * @param contentFormat the {@code RecordValueFormat}
     * @since 1.0.0
     */
    public void setContentFormat(RecordValueFormat contentFormat) {
        this.contentFormat = contentFormat;
    }

    /**
     * Get the {@code RecordKind}.
     *
     * @return the {@code RecordKind}
     * @since 1.0.0
     */
    public RecordKind getKind() {
        return kind;
    }

    /**
     * Set the {@code RecordKind}. It is impossible to update an already persisted value.
     *
     * @param kind the {@code RecordKind}
     * @since 1.0.0
     */
    public void setKind(RecordKind kind) {
        this.kind = kind;
    }

    /**
     * Get the processing failure phase.
     *
     * @return the {@code JobPhase}. It may be {@code null}.
     * @since 1.0.0
     */
    public JobPhase getFailurePhase() {
        return failurePhase;
    }

    /**
     * Set the processing failure phase. It is impossible to update an already persisted value.
     *
     * @param failurePhase processing failure phase. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setFailurePhase(JobPhase failurePhase) {
        this.failurePhase = failurePhase;
    }

    /**
     * Get result message.
     *
     * @return message. It may be {@code null}.
     * @since 1.0.0
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set result message. It is impossible to update an already persisted value.
     *
     * @param message result message. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setMessage(String message) {
        this.message = message;
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
        return this == other || other instanceof JobrecEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "JobrecEntity{" + "id=" + id + ", jobId=" + jobId + ", kind=" + kind
                + ", contentId=" + contentId + ", failurePhase=" + failurePhase + '}';
    }
}
