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
package jp.mydns.projectk.safi.value;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.ChronoUnit.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;
import java.util.Objects;

/**
 * Combined value of the {@link Period} and the {@link Duration}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class PeriodDuration implements TemporalAmount, Serializable {

    private static final long serialVersionUID = 1783491523460206169L;

    private final Period period;
    private final Duration duration;

    /**
     * Construct from the {@code Period} and the {@code Duration}.
     *
     * @param period the {@code Period}
     * @param duration the {@code Duration}
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public PeriodDuration(Period period, Duration duration) {
        this.period = Objects.requireNonNull(period);
        this.duration = Objects.requireNonNull(duration);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation. This value can be used as an argument for {@link #of(java.lang.String)}.
     * @since 1.0.0
     */
    @Override
    public String toString() {
        if (period.isZero()) {
            return duration.toString();
        }

        if (duration.isZero()) {
            return period.toString();
        }

        return period.toString() + duration.toString().substring(1);
    }

    /**
     * Parse string representation as {@code PeriodDuration}.
     *
     * @param pd textual {@code PeriodDuration}
     * @return the {@code PeriodDuration}
     * @throws IllegalArgumentException if {@code pd} is malformed
     * @since 1.0.0
     */
    public static PeriodDuration of(String pd) {
        String pattern = Objects.requireNonNull(pd).toUpperCase();
        boolean isNegative = pattern.startsWith("-");
        String sign = isNegative ? "-" : "";

        if (isNegative) {
            pattern = pattern.substring(1);
        }

        try {

            if (pattern.startsWith("PT")) {
                return new PeriodDuration(Period.ZERO, Duration.parse(sign + pattern));
            }

            int posT = pattern.indexOf('T');

            if (posT < 0) {
                return new PeriodDuration(Period.parse(sign + pattern), Duration.ZERO);
            }

            return new PeriodDuration(Period.parse(sign + pattern.substring(0, posT)),
                    Duration.parse(sign + "P" + pattern.substring(posT)));

        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Must be a valid ISO8601 duration representation.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param tu the {@code TemporalUnit} for which to return the value. Supported units are {@link ChronoUnit#YEARS YEARS},
     * {@link ChronoUnit#MONTHS MONTHS}, {@link ChronoUnit#DAYS DAYS}, {@link ChronoUnit#SECONDS SECONDS},
     * {@link ChronoUnit#NANOS NANOS}.
     * @throws NullPointerException if {@code tu} is {@code null}
     * @throws DateTimeException if a value for the unit cannot be obtained
     * @throws UnsupportedTemporalTypeException if the {@code tu} is not supported
     * @since 1.0.0
     */
    @Override
    public long get(TemporalUnit tu) {
        return switch (Objects.requireNonNull(tu)) {
            case ChronoUnit cu when cu == YEARS ->
                period.getYears();
            case ChronoUnit cu when cu == MONTHS ->
                period.getMonths();
            case ChronoUnit cu when cu == DAYS ->
                period.getDays();
            case ChronoUnit cu when cu == SECONDS ->
                duration.getSeconds();
            case ChronoUnit cu when cu == NANOS ->
                duration.getNano();
            default ->
                throw new UnsupportedTemporalTypeException("Unsupported temporal type: [%s]".formatted(tu));
        };
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public List<TemporalUnit> getUnits() {
        return List.of(YEARS, MONTHS, DAYS, SECONDS, NANOS);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code tmprl} is {@code null}
     * @throws DateTimeException if unable to add
     * @throws ArithmeticException if numeric overflow occurs
     * @since 1.0.0
     */
    @Override
    public Temporal addTo(Temporal tmprl) {
        return Objects.requireNonNull(tmprl).plus(period).plus(duration);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code tmprl} is {@code null}
     * @throws DateTimeException if unable to subtract
     * @throws ArithmeticException if numeric overflow occurs
     * @since 1.0.0
     */
    @Override
    public Temporal subtractFrom(Temporal tmprl) {
        return Objects.requireNonNull(tmprl).minus(period).minus(duration);
    }
}
