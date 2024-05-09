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
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.TimeConfigKind;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * JPA entity for the <i>m_time</i> table.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "m_time")
public class TimeConfigEntity extends CommonEntity {

    private static final long serialVersionUID = -8634128441967110929L;

    /**
     * Configuration id.
     *
     * @since 1.0.0
     */
    @Id
    @NotNull
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "id", nullable = false, length = 20)
    private TimeConfigKind id;

    /**
     * Configuration value.
     *
     * @since 1.0.0
     */
    @TimeAccuracy
    @TimeRange
    @Column(name = "value")
    private LocalDateTime value;

    /**
     * The {@code ValidityPeriodEmb}
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
    public TimeConfigEntity() {
    }

    /**
     * Get id of the configuration.
     *
     * @return the {@code TimeConfigKind}
     * @since 1.0.0
     */
    public TimeConfigKind getId() {
        return id;
    }

    /**
     * Set id of the configuration.
     *
     * @param id the {@code TimeConfigKind}
     * @since 1.0.0
     */
    public void setId(TimeConfigKind id) {
        this.id = id;
    }

    /**
     * Get value of the configuration.
     *
     * @return the {@code LocalDateTime}. It time zone is UTC. It may be {@code null}.
     * @since 1.0.0
     */
    public LocalDateTime getValue() {
        return value;
    }

    /**
     * Set value of the configuration.
     *
     * @param value the {@code LocalDateTime}. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setValue(LocalDateTime value) {
        this.value = value;
    }

    /**
     * Get the validity-period of the configuration.
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
        return this == other || other instanceof TimeConfigEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "TimeConfigEntity{" + "id=" + id + ", value=" + value + ", validityPeriod=" + validityPeriodEmb + '}';
    }
}
