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
package jp.mydns.projectk.safi.entity.embedded;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * Implementation of the {@code ValidityPeriod} as a built-in part of JPA entity.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Embeddable
public class ValidityPeriodEmb implements ValidityPeriod, Serializable {

    private static final long serialVersionUID = -3134392376473803641L;

    @Basic(optional = false)
    @Column(name = "from_ts", nullable = false)
    private LocalDateTime localFrom;

    @Basic(optional = false)
    @Column(name = "to_ts", nullable = false)
    private LocalDateTime localTo;

    @Column(name = "ban", nullable = false)
    private boolean ban;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public ValidityPeriodEmb() {
    }

    /**
     * Constructor.
     *
     * @param vp the {@code ValidityPeriod}
     * @throws NullPointerException if {@code vp} is {@code null}
     * @since 1.0.0
     */
    public ValidityPeriodEmb(ValidityPeriod vp) {
        Objects.requireNonNull(vp);

        localFrom = TimeUtils.toLocalDateTime(vp.getFrom());
        localTo = TimeUtils.toLocalDateTime(vp.getTo());
        ban = vp.isBan();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public OffsetDateTime getFrom() {
        return TimeUtils.toOffsetDateTime(localFrom);
    }

    /**
     * Get begin date-time of validity period.
     *
     * @return begin date-time of validity period
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getLocalFrom() {
        return localFrom;
    }

    /**
     * Set begin date-time of validity period.
     *
     * @param localFrom begin date-time of validity period
     * @since 1.0.0
     */
    public void setLocalFrom(LocalDateTime localFrom) {
        this.localFrom = localFrom;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public OffsetDateTime getTo() {
        return TimeUtils.toOffsetDateTime(localTo);
    }

    /**
     * Get end date-time of validity period.
     *
     * @return end date-time of validity period
     * @since 1.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getLocalTo() {
        return localTo;
    }

    /**
     * Set end date-time of validity period.
     *
     * @param localTo end date-time of validity period
     * @since 1.0.0
     */
    public void setLocalTo(LocalDateTime localTo) {
        this.localTo = localTo;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public boolean isBan() {
        return ban;
    }

    /**
     * Sets a flag that indicates ban of valid.
     *
     * @param ban {@code true} if forbidden to be valid, otherwise {@code false}.
     * @since 1.0.0
     */
    public void setBan(boolean ban) {
        this.ban = ban;
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(localFrom, localTo, ban);
    }

    /**
     * Indicates that other object is equal to this instance.
     *
     * @param other an any object
     * @return {@code true} if equals otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ValidityPeriod o
                && Objects.equals(localFrom, TimeUtils.toLocalDateTime(o.getFrom()))
                && Objects.equals(localTo, TimeUtils.toLocalDateTime(o.getTo())) && Objects.equals(ban, o.isBan());
    }

    /**
     * Returns a string representation.
     *
     * @return string representation of this instance
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "ValidityPeriod{" + "from=" + TimeUtils.toOffsetDateTime(localFrom)
                + ", to=" + TimeUtils.toOffsetDateTime(localTo) + ", ban=" + ban + '}';
    }
}
