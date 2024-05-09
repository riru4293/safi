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
import java.util.Objects;
import jp.mydns.projectk.safi.constant.SchedefKind;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.value.JsonObjectVo;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * JPA entity for the <i>m_schedef</i> table.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "m_schedef")
public class SchedefEntity extends CommonEntity {

    private static final long serialVersionUID = 7548015903437471607L;

    /**
     * Scheduling definition id.
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
     * The {@code SchedefKind}.
     *
     * @since 1.0.0
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "kind", nullable = false, updatable = false, length = 20)
    private SchedefKind kind;

    /**
     * Priority for between the scheduling definitions.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 1)
    @Basic(optional = false)
    @Column(name = "priority", nullable = false, length = 1)
    private String priority;

    /**
     * Scheduling definition name.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 255)
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Value of the scheduling definition.
     *
     * @since 1.0.0
     */
    @NotNull
    @Basic(optional = false)
    @Column(name = "value", nullable = false, updatable = false)
    private JsonObjectVo value;

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
    public SchedefEntity() {
    }

    /**
     * Get scheduling definition id.
     *
     * @return scheduling definition id
     * @since 1.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * Set scheduling definition id. It is impossible to update an already persisted value.
     *
     * @param id scheduling definition id
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
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
     * Get the {@code SchedefKind}.
     *
     * @return the {@code SchedefKind}
     * @since 1.0.0
     *
     */
    public SchedefKind getKind() {
        return kind;
    }

    /**
     * Set the {@code SchedefKind}. It is impossible to update an already persisted value.
     *
     * @param kind the {@code SchedefKind}
     * @since 1.0.0
     */
    public void setKind(SchedefKind kind) {
        this.kind = kind;
    }

    /**
     * Get priority for between the scheduling definitions.
     *
     * @return priority
     * @since 1.0.0
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Set priority for between the scheduling definitions.
     *
     * @param priority priority
     * @since 1.0.0
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Get scheduling definition name.
     *
     * @return job creation definition name
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Set scheduling definition name.
     *
     * @param name job creation definition name
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get value of the scheduling definition.
     *
     * @return definition value
     * @since 1.0.0
     */
    public JsonObjectVo getValue() {
        return value;
    }

    /**
     * Set value of the scheduling definition.
     *
     * @param value definition value
     * @since 1.0.0
     */
    public void setValue(JsonObjectVo value) {
        this.value = value;
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
        return this == other || other instanceof SchedefEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "SchedefEntity{" + "id=" + id + ", jobdefId=" + jobdefId + ", kind=" + kind + ", name=" + name + '}';
    }
}
