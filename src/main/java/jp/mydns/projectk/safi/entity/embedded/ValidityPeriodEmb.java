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
package jp.mydns.projectk.safi.entity.embedded;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Validity period as a built-in part of JPA entity.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Embeddable
public class ValidityPeriodEmb implements Serializable {

    @java.io.Serial
    private static final long serialVersionUID = -3134392376473803641L;

    @Basic(optional = false)
    @Column(name = "from_ts", nullable = false)
    private LocalDateTime from;

    @Basic(optional = false)
    @Column(name = "to_ts", nullable = false)
    private LocalDateTime to;

    @Column(name = "ignored", nullable = false)
    private boolean ignored;

    /**
     * Get begin date-time of enabled period.
     *
     * @return begin date-time of enabled period. It timezone is UTC.
     * @since 3.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Set begin date-time of enabled period.
     *
     * @param from begin date-time of enabled period. It timezone is UTC.
     * @since 3.0.0
     */
    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    /**
     * Get end date-time of enabled period.
     *
     * @return end date-time of enabled period. It timezone is UTC.
     * @since 3.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Set end date-time of enabled period.
     *
     * @param to end date-time of enabled period. It timezone is UTC.
     * @since 3.0.0
     */
    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    /**
     * Get a flag of ignored.
     *
     * @return {@code true} if ignored, otherwise {@code false}.
     * @since 3.0.0
     */
    public boolean isIgnored() {
        return ignored;
    }

    /**
     * Set a flag of ignored.
     *
     * @param ignored {@code true} if ignored, otherwise {@code false}.
     * @since 3.0.0
     */
    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 3.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(from, to, ignored);
    }

    /**
     * Indicates that other object is equal to this instance.
     *
     * @param other an any object
     * @return {@code true} if equals otherwise {@code false}.
     * @since 3.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ValidityPeriodEmb o
            && Objects.equals(from, o.from) && Objects.equals(to, o.to) && Objects.equals(ignored, o.ignored);
    }

    /**
     * Returns a string representation.
     *
     * @return string representation of this instance
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "ValidityPeriodEmb{" + "from=" + from + ", to=" + to + ", ignored=" + ignored + '}';
    }
}
