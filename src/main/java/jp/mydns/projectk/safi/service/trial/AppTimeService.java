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
package jp.mydns.projectk.safi.service.trial;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import jp.mydns.projectk.safi.service.RealTimeService;

/**
 * Provides a time inside the application.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface AppTimeService {

    /**
     * Get current time. Accuracy is seconds.
     *
     * @return current time inside the application.
     * @since 3.0.0
     */
     OffsetDateTime getOffsetNow();

    /**
     * Get current time. Accuracy is seconds.
     *
     * @return current time inside the application, in that case timezone is UTC.
     * @since 3.0.0
     */
     LocalDateTime getLocalNow();

    /**
     * Implements of the {@code AppTimeService}
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Typed(AppTimeService.class)
    @RequestScoped
     class Impl implements AppTimeService {

        private final RealTimeService realTimeSvc;

        /**
         * Constructor.
         *
         * @param realTimeSvc the {@code RealTimeService}
         * @since 3.0.0
         */
        @Inject
        public Impl(RealTimeService realTimeSvc) {
            this.realTimeSvc = realTimeSvc;
        }

        /**
         * {@inheritDoc}
         *
         * @since 3.0.0
         */
        @Override
        public OffsetDateTime getOffsetNow() {
            // ToDo: Must be implemented.
            return realTimeSvc.getOffsetNow();
        }

        /**
         * {@inheritDoc}
         *
         * @return current time inside the application, in that case timezone is UTC.
         * @since 3.0.0
         */
        @Override
        public LocalDateTime getLocalNow() {
            // ToDo: Must be implemented.
            return realTimeSvc.getLocalNow();
        }
    }
}
