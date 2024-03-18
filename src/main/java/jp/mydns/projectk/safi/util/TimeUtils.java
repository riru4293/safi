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
package jp.mydns.projectk.safi.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for date and time.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class has not variable field member and it has all method is static.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class TimeUtils {

    private TimeUtils() {
    }

    /**
     * Exchange to {@code OffsetDateTime} from {@code LocalDateTime} in UTC.
     *
     * @param localDateTime the {@code LocalDateTime} in UTC. It can be set {@code null}.
     * @return the {@code OffsetDateTime}. {@code null} if {@code localDateTime} is {@code null}.
     * @since 1.0.0
     */
    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {

        if (localDateTime == null) {
            return null;
        }

        return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }

    /**
     * Parse to the {@code LocalDateTime}.
     *
     * @param localDateTime string representation of the {@code LocalDateTime}
     * @return the {@code LocalDateTime}
     * @throws NullPointerException if {@code localDateTime} is {@code null}
     * @throws DateTimeParseException if failed parse to the {@code LocalDateTime}
     * @see DateTimeFormatter#ISO_LOCAL_DATE_TIME
     * @since 1.0.0
     */
    public static LocalDateTime toLocalDateTime(String localDateTime) {
        return LocalDateTime.parse(Objects.requireNonNull(localDateTime), ISO_LOCAL_DATE_TIME);
    }

    /**
     * Parse to the {@code LocalDate}.
     *
     * @param localDate string representation of the {@code LocalDate}
     * @return the {@code LocalDate}
     * @throws NullPointerException if {@code localDate} is {@code null}
     * @throws DateTimeParseException if failed parse to the {@code LocalDate}
     * @see DateTimeFormatter#ISO_LOCAL_DATE
     * @since 1.0.0
     */
    public static LocalDate toLocalDate(String localDate) {
        return LocalDate.parse(Objects.requireNonNull(localDate), ISO_LOCAL_DATE);
    }

    /**
     * Parse to the {@code LocalTime}.
     *
     * @param localTime string representation of the {@code LocalTime}
     * @return the {@code LocalTime}
     * @throws NullPointerException if {@code localTime} is {@code null}
     * @throws DateTimeParseException if failed parse to the {@code LocalTime}
     * @see DateTimeFormatter#ISO_LOCAL_TIME
     * @since 1.0.0
     */
    public static LocalTime toLocalTime(String localTime) {
        return LocalTime.parse(Objects.requireNonNull(localTime), ISO_LOCAL_TIME);
    }

    /**
     * Try parse to the {@code LocalDateTime}.
     *
     * @param localDateTime string representation of the {@code LocalDateTime}
     * @return the {@code LocalDateTime}. Empty if failed parse to the {@code LocalDateTime}.
     * @see DateTimeFormatter#ISO_LOCAL_DATE_TIME
     * @since 1.0.0
     */
    public static Optional<LocalDateTime> tryToLocalDateTime(String localDateTime) {
        try {
            return Optional.of(toLocalDateTime(localDateTime));
        } catch (RuntimeException ignore) {
            return Optional.empty();
        }
    }

    /**
     * Try parse to the {@code LocalDate}.
     *
     * @param localDate string representation of the {@code LocalDate}
     * @return the {@code LocalDate}. Empty if failed parse to the {@code LocalDate}.
     * @see DateTimeFormatter#ISO_LOCAL_DATE
     * @since 1.0.0
     */
    public static Optional<LocalDate> tryToLocalDate(String localDate) {
        try {
            return Optional.of(toLocalDate(localDate));
        } catch (RuntimeException ignore) {
            return Optional.empty();
        }
    }

    /**
     * Try parse to the {@code LocalTime}.
     *
     * @param localTime string representation of the {@code LocalTime}
     * @return the {@code LocalTime}. Empty if failed parse to the {@code LocalTime}.
     * @see DateTimeFormatter#ISO_LOCAL_TIME
     * @since 1.0.0
     */
    public static Optional<LocalTime> tryToLocalTime(String localTime) {
        try {
            return Optional.of(toLocalTime(localTime));
        } catch (RuntimeException ignore) {
            return Optional.empty();
        }
    }

    /**
     * Exchange to {@code LocalDateTime} in UTC from {@code OffsetDateTime}.
     *
     * @param offsetDateTime the {@code OffsetDateTime}. It can be set {@code null}.
     * @return the {@code LocalDateTime} in UTC. {@code null} if {@code offsetDateTime} is {@code null}.
     * @since 1.0.0
     */
    public static LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {

        if (offsetDateTime == null) {
            return null;
        }

        return offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();

    }
}
