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
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * Validity period as a built-in part of JPA entity.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Embeddable
public class ValidityPeriodEmb implements Serializable {

    private static final long serialVersionUID = -3134392376473803641L;

    /**
     * The begin time of validity period.
     *
     * @since 1.0.0
     */
    @Basic(optional = false)
    @Column(name = "from_ts", nullable = false)
    private LocalDateTime from;

    /**
     * The end time of validity period.
     *
     * @since 1.0.0
     */
    @Basic(optional = false)
    @Column(name = "to_ts", nullable = false)
    private LocalDateTime to;

    /**
     * The flag that forbidden to be valid.
     *
     * @since 1.0.0
     */
    @Column(name = "ban", nullable = false)
    private boolean ban;

    /**
     * Constructs a part of entity with all properties are {@code null}.
     *
     * @since 1.0.0
     */
    public ValidityPeriodEmb() {
    }

    /**
     * Construct with {@code ValidityPeriod}.
     *
     * @param validityPeriod the {@code ValidityPeriod}
     * @throws NullPointerException if {@code validityPeriod} is {@code null}
     * @since 1.0.0
     */
    public ValidityPeriodEmb(ValidityPeriod validityPeriod) {
        Objects.requireNonNull(validityPeriod);

        from = TimeUtils.toLocalDateTime(validityPeriod.getFrom());
        to = TimeUtils.toLocalDateTime(validityPeriod.getTo());
        ban = validityPeriod.isBan();
    }

    /**
     * Get begin time of validity period.
     *
     * @return begin time
     * @since 1.0.0
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Set begin time of validity period.
     *
     * @param from begin time
     * @since 1.0.0
     */
    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    /**
     * Get end time of validity period.
     *
     * @return end time
     * @since 1.0.0
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Set end time of validity period.
     *
     * @param to end time
     * @since 1.0.0
     */
    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    /**
     * Get flag that indicates ban of valid.
     *
     * @return {@code true} if forbidden to be valid, otherwise {@code false}.
     * @since 1.0.0
     */
    public boolean isBan() {
        return ban;
    }

    /**
     * Set flag that indicates ban of valid.
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
        return Objects.hash(from, to, ban);
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
        return this == other || other instanceof ValidityPeriodEmb o && Objects.equals(from, o.getFrom())
                && Objects.equals(to, o.getTo()) && Objects.equals(ban, o.isBan());
    }

    /**
     * Returns a string representation.
     *
     * @return string representation of this instance
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "ValidityPeriodEmb{" + "from=" + from + ", to=" + to + ", ban=" + ban + '}';
    }

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public ValidityPeriod toValidityPeriod() {
        return new ValidityPeriodImpl(this);
    }

    private class ValidityPeriodImpl implements ValidityPeriod {

        private final OffsetDateTime from;
        private final OffsetDateTime to;
        private final boolean ban;

        private ValidityPeriodImpl(ValidityPeriodEmb emb) {
            this.from = TimeUtils.toOffsetDateTime(emb.from);
            this.to = TimeUtils.toOffsetDateTime(emb.to);
            this.ban = emb.ban;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public OffsetDateTime getFrom() {
            return from;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public OffsetDateTime getTo() {
            return to;
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
         * Returns a hash code value.
         *
         * @return a hash code value
         * @since 1.0.0
         */
        @Override
        public int hashCode() {
            return Objects.hash(from, to, ban);
        }

        /**
         * Indicates that specified object is equal to this one.
         *
         * @param other an any object
         * @return {@code true} if matches otherwise {@code false}.
         * @since 1.0.0
         */
        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof ValidityPeriod o && Objects.equals(from, o.getFrom())
                    && Objects.equals(to, o.getTo()) && Objects.equals(ban, o.isBan());
        }

        /**
         * Returns a string representation.
         *
         * @return a string representation
         * @since 1.0.0
         */
        @Override
        public String toString() {
            return "ValidityPeriod{" + "from=" + from + ", to=" + to + ", ban=" + ban + '}';
        }
    }
}
