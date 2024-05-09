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
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.ContentKind;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.value.Filtdef;
import jp.mydns.projectk.safi.value.JsonObjectVo;
import jp.mydns.projectk.safi.value.Plugdef;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * JPA entity for the <i>m_jobdef</i> table.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "m_jobdef")
public class JobdefEntity extends CommonEntity {

    private static final long serialVersionUID = -7114859991686029165L;

    /**
     * Job definition id.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    /**
     * The {@code JobKind}.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "job_kind", nullable = false, updatable = false, length = 20)
    private JobKind jobKind;

    /**
     * Target content type for the Job.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "content_kind", nullable = false, updatable = false, length = 20)
    private ContentKind contentKind;

    /**
     * Job definition name.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 255)
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Duration before the Job timeout.
     *
     * @since 1.0.0
     */
    @NotNull
    @Basic(optional = false)
    @Column(name = "timeout", nullable = false, length = 20)
    private Duration timeout;

    /**
     * The {@link Plugdef} as JSON.
     *
     * @since 1.0.0
     */
    @Column(name = "plugdef")
    private JsonObjectVo plugdef;

    /**
     * The {@link Filtdef} as JSON.
     *
     * @since 1.0.0
     */
    @Column(name = "filtdef")
    private JsonObjectVo filtdef;

    /**
     * Transform definitions.
     *
     * @since 1.0.0
     */
    @Column(name = "trnsdef")
    private JsonObjectVo trnsdef;

    /**
     * Optional configuration.
     *
     * @since 1.0.0
     */
    @NotNull
    @Basic(optional = false)
    @Column(name = "options", nullable = false)
    private JsonObjectVo options;

    /**
     * The {@code ValidityPeriodEmb}.
     *
     * @since 1.0.0
     */
    @NotNull
    @Valid
    @Embedded
    protected ValidityPeriodEmb validityPeriodEmb;

    /**
     * Constructs a new entity with all properties are default value.
     *
     * @since 1.0.0
     */
    public JobdefEntity() {
    }

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 1.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * Set job definition id. It is impossible to update an already persisted value.
     *
     * @param id job definition id
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get job kind.
     *
     * @return job kind
     * @since 1.0.0
     *
     */
    public JobKind getJobKind() {
        return jobKind;
    }

    /**
     * Set job kind. It is impossible to update an already persisted value.
     *
     * @param jobKind job kind
     * @since 1.0.0
     */
    public void setJobKind(JobKind jobKind) {
        this.jobKind = jobKind;
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
     * Get job definition name.
     *
     * @return job definition name
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Set job definition name.
     *
     * @param name job definition name
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get duration before the Job timeout.
     *
     * @return duration before the Job timeout
     * @since 1.0.0
     */
    public Duration getTimeout() {
        return timeout;
    }

    /**
     * Set duration before the Job timeout.
     *
     * @param timeout duration before the Job timeout
     * @since 1.0.0
     */
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
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
     * Set the {@link Plugdef} as JSON.
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
     * Set the {@link Filtdef} as JSON.
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
     * Set transformation definition.
     *
     * @param trnsdef transformation definition. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setTrnsdef(JsonObjectVo trnsdef) {
        this.trnsdef = trnsdef;
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
     * @param options optional properties
     * @since 1.0.0
     */
    public void setOptions(JsonObjectVo options) {
        this.options = options;
    }

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public ValidityPeriod getValidityPeriod() {
        return validityPeriodEmb != null ? validityPeriodEmb.toValidityPeriod() : null;
    }

    /**
     * Set the {@code ValidityPeriod}.
     *
     * @param validityPeriod the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriodEmb = validityPeriod != null ? new ValidityPeriodEmb(validityPeriod) : null;
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
        return this == other || other instanceof JobdefEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "JobdefEntity{" + "id=" + id + ", jobKind=" + jobKind + ", contentKind=" + contentKind + ", name=" + name + '}';
    }
}
