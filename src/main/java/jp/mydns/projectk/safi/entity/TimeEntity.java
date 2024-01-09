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
import jp.mydns.projectk.safi.constant.TimeKind;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * JPA entity for {@code m_time} table.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "m_time")
public class TimeEntity extends CommonEntity {

    private static final long serialVersionUID = -8634128441967110929L;

    @Id
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 20)
    private TimeKind id;

    @Basic(optional = false)
    @Column(name = "value", nullable = false)
    private LocalDateTime value;

    @NotNull
    @Valid
    @Embedded
    protected ValidityPeriodEmb validityPeriod;

    /**
     * Get the {@code TimeKind}.
     *
     * @return the {@code TimeKind}
     * @since 1.0.0
     */
    @NotNull
    public TimeKind getId() {
        return id;
    }

    /**
     * Set the {@code TimeKind}.
     *
     * @param id the {@code TimeKind}
     * @since 1.0.0
     */
    public void setId(TimeKind id) {
        this.id = id;
    }

    /**
     * Get time value.
     *
     * @return time value. It may be {@code null}.
     * @since 1.0.0
     */
    @NotNull
    @TimeAccuracy
    @TimeRange
    public LocalDateTime getValue() {
        return value;
    }

    /**
     * Set time value.
     *
     * @param value time value. It can be set {@code null}.
     * @since 1.0.0
     */
    public void setValue(LocalDateTime value) {
        this.value = value;
    }

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * Set the {@code ValidityPeriod}.
     *
     * @param validityPeriod the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod != null ? new ValidityPeriodEmb(validityPeriod) : null;
    }

    /**
     * Returns a hash code value of this.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Indicates that other object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if equals, otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof TimeEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation of this.
     *
     * @return string representation of this
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "TimeEntity{" + "id=" + id + ", value=" + value + ", validityPeriod=" + validityPeriod + '}';
    }
}
