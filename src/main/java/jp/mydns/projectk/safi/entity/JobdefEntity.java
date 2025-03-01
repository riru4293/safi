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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.value.JsonObjectValue;

/**
 * JPA entity for the <i>m_jobdef</i> table.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Entity
@Cacheable(false)
@Table(name = "m_jobdef")
public class JobdefEntity extends NamedEntity {

    private static final long serialVersionUID = -8597141002815361653L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Basic(optional = false)
    @Column(name = "job_kind", nullable = false, updatable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobKind jobKind;

    @Basic(optional = false)
    @Column(name = "job_target", nullable = false, updatable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobTarget jobTarget;

    @Basic(optional = false)
    @Column(name = "timeout", nullable = false, length = 20)
    private Duration timeout;

    @Column(name = "plugin", length = 50)
    private String pluginName;

    @Column(name = "trnsdef")
    private JsonObjectValue trnsdef;

    @Column(name = "filtdef")
    private JsonObjectValue filtdef;

    @Basic(optional = false)
    @Column(name = "job_props", nullable = false)
    private JsonObjectValue jobProperties;

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    public String getId() {
        return id;
    }

    /**
     * Set job definition id.
     *
     * @param id job definition id. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get job kind.
     *
     * @return job kind
     * @since 3.0.0
     */
    @NotNull
    public JobKind getJobKind() {
        return jobKind;
    }

    /**
     * Set job kind.
     *
     * @param jobKind job kind. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setJobKind(JobKind jobKind) {
        this.jobKind = jobKind;
    }

    /**
     * Get job target.
     *
     * @return job target
     * @since 3.0.0
     */
    @NotNull
    public JobTarget getJobTarget() {
        return jobTarget;
    }

    /**
     * Set job target.
     *
     * @param jobTarget job target. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setJobTarget(JobTarget jobTarget) {
        this.jobTarget = jobTarget;
    }

    /**
     * Set job processing timeout.
     *
     * @return job processing timeout
     * @since 3.0.0
     */
    @NotNull
    @TimeAccuracy
    public Duration getTimeout() {
        return timeout;
    }

    /**
     * Set job processing timeout.
     *
     * @param timeout job processing timeout
     * @since 3.0.0
     */
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    /**
     * Get plugin name.
     *
     * @return plugin name. It may be {@code null}.
     * @since 3.0.0
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Set plugin name.
     *
     * @param pluginName plugin name. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Get transform definition.
     *
     * @return transform definition. It may be {@code null}.
     * @since 3.0.0
     */
    public JsonObjectValue getTrnsdef() {
        return trnsdef;
    }

    /**
     * Set transform definition.
     *
     * @param trnsdef transform definition. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setTrnsdef(JsonObjectValue trnsdef) {
        this.trnsdef = trnsdef;
    }

    /**
     * Get filtering definition.
     *
     * @return filtering definition. It may be {@code null}.
     * @since 3.0.0
     */
    public JsonObjectValue getFiltdef() {
        return filtdef;
    }

    /**
     * Set filtering definition.
     *
     * @param filtdef filtering definition. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setFiltdef(JsonObjectValue filtdef) {
        this.filtdef = filtdef;
    }

    /**
     * Get job properties.
     *
     * @return job properties
     * @since 3.0.0
     */
    @NotNull
    public JsonObjectValue getJobProperties() {
        return jobProperties;
    }

    /**
     * Set job properties.
     *
     * @param jobProperties job properties
     * @since 3.0.0
     */
    public void setJobProperties(JsonObjectValue jobProperties) {
        this.jobProperties = jobProperties;
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
        return other instanceof JobdefEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "JobdefEntity{" + "id=" + id + ", validityPeriod=" + validityPeriod + ", jobKind=" + jobKind
            + ", jobTarget=" + jobTarget + ", timeout=" + timeout + ", name=" + name + ", pluginName=" + pluginName
            + ", trnsdef=" + trnsdef + ", filtdef=" + filtdef + ", jobProperties=" + jobProperties + '}';
    }
}
