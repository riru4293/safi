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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.RequestScoped;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Provides a real date-time.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@RequestScoped
public class RealTimeService {

    /**
     * Get current time. Accuracy is seconds.
     *
     * @return current time, in that case timezone is UTC.
     * @since 3.0.0
     */
    public OffsetDateTime getOffsetNow() {
        return getExactlyOffsetNow().truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * Get current time. Accuracy is seconds.
     *
     * @return current time, in that case timezone is UTC.
     * @since 3.0.0
     */
    public LocalDateTime getLocalNow() {
        return getOffsetNow().toLocalDateTime();
    }

    /**
     * Get exactly current time. Can only be used when {@link #getOffsetNow()} has insufficient precision.
     *
     * @return current time, in that case timezone is UTC.
     * @since 3.0.0
     */
    public OffsetDateTime getExactlyOffsetNow() {
        return OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC.normalized());
    }

    /**
     * Get exactly current time. Can only be used when {@link #getLocalNow()} has insufficient precision.
     *
     * @return current time, in that case timezone is UTC.
     * @since 3.0.0
     */
    public LocalDateTime getExactlyLocalNow() {
        return getExactlyOffsetNow().toLocalDateTime();
    }
}
