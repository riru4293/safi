/*
 * Copyright 2025, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 Provides a time.

 The service provides two categories of time values, all in UTC time zone. The categories are:

 <dl>
     <dt>- Application time</dt>
     <dd>The current time for this application. This value acts as an anchor time within the request scope, and the same value will be provided within that scope.</dd>

     <dt>- Real time</dt>
     <dd>The real system time. For only special purposes where real system time is needed.</dd>
 </dl>

 Implementation requirements.
 <ul>
     <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface TimeService
{
    /**
     Get the current time for the application.

     Accuracy is seconds. The value returned is the same as the first value retrieved.

     @return current time for the application
     @since 3.0.0
     */
    LocalDateTime getAppLocalNow();

    /**
     Get the real time.

     Accuracy is seconds. This is not application time and should not be used normally.

     @return system time
     @since 3.0.0
     */
    LocalDateTime getRealLocalNow();

    /**
     Get the real time.

     Accuracy is seconds. This is not application time and should not be used normally.

     @return system time
     @since 3.0.0
     */
    OffsetDateTime getRealOffsetNow();

    /**
     Internal Implementation.

     @hidden
     */
    @Typed(TimeService.class)
    @RequestScoped
    class Impl implements TimeService
    {
        private final ConfigService confSvc;

        private final AtomicReference<LocalDateTime> cachedAppLocalNow;

        @SuppressWarnings("unused")
        Impl()
        {
            // Note: The default constructor exists only to allow NetBeans to recognize the CDI bean.
            throw new UnsupportedOperationException();
        }

        @Inject
        @SuppressWarnings("unused") // Note: To be called by CDI.
        Impl(ConfigService confSvc)
        {
            this.confSvc = confSvc;
            this.cachedAppLocalNow = new AtomicReference<>();
        }

        @Override
        public LocalDateTime getAppLocalNow()
        {
            cachedAppLocalNow.getAndUpdate(c -> c != null ? c : calculateAppLocalNow());
            return cachedAppLocalNow.get();
        }

        LocalDateTime calculateAppLocalNow()
        {
            return confSvc.getFrozenTime().orElseGet(this::getRealLocalNow);
        }

        @Override
        public LocalDateTime getRealLocalNow()
        {
            return getRealOffsetNow()
                .toLocalDateTime()
                .truncatedTo(ChronoUnit.SECONDS);
        }

        @Override
        public OffsetDateTime getRealOffsetNow()
        {
            return OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC.normalized())
                .truncatedTo(ChronoUnit.SECONDS);
        }
    }
}
