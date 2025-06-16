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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 Provides a real date-time, reference date-time.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface TimeService {

/**
 Gets the current time for the SAFI, which is typically the system time in UTC, but may not be the
 case depending on the configuration.

 @return reference time
 @since 3.0.0
 */
LocalDateTime getSafiTime();

/**
 Get current time. Accuracy is seconds. The first value is remembered so that subsequent times the
 same value is returned.

 @return current time, in that case timezone is UTC.
 @since 3.0.0
 */
OffsetDateTime getOffsetNow();

/**
 Get current time. Accuracy is seconds. The first value is remembered so that subsequent times the
 same value is returned.

 @return current time, in that case timezone is UTC.
 @since 3.0.0
 */
LocalDateTime getLocalNow();

/**
 Get exactly current time. Can only be used when {@link #getOffsetNow()} has insufficient precision.
 The first value is remembered so that subsequent times the same value is returned.

 @return current time, in that case timezone is UTC.
 @since 3.0.0
 */
OffsetDateTime getExactlyOffsetNow();

/**
 Get exactly current time. Can only be used when {@link #getLocalNow()} has insufficient precision.
 The first value is remembered so that subsequent times the same value is returned.

 @return current time, in that case timezone is UTC.
 @since 3.0.0
 */
LocalDateTime getExactlyLocalNow();

/**
 Implements of the {@code TimeService}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(TimeService.class)
@ApplicationScoped
class Impl implements TimeService {

private final ConfigService confSvc;

@SuppressWarnings("unused")
Impl() {
    // Note: The default constructor exists only to allow NetBeans to recognize the CDI bean.
    throw new UnsupportedOperationException();
}

@Inject
@SuppressWarnings("unused")
Impl(ConfigService confSvc) {
    this.confSvc = confSvc;
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public LocalDateTime getSafiTime() {
    return confSvc.getFrozenTime().orElseGet(this::getLocalNow);
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public OffsetDateTime getOffsetNow() {
    return getExactlyOffsetNow().truncatedTo(ChronoUnit.SECONDS);
}

/**
 {@inheritDoc}

 @return current time, in that case timezone is UTC.
 @since 3.0.0
 */
@Override
public LocalDateTime getLocalNow() {
    return getExactlyLocalNow().truncatedTo(ChronoUnit.SECONDS);
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public OffsetDateTime getExactlyOffsetNow() {
    return OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC.normalized());
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public LocalDateTime getExactlyLocalNow() {
    return getExactlyOffsetNow().toLocalDateTime();
}

}

}
