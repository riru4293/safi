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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import jp.mydns.projectk.safi.constant.TimeKind;
import jp.mydns.projectk.safi.dao.TimeDao;
import jp.mydns.projectk.safi.entity.TimeEntity;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.ValidityPeriod;

/**
 * Provides a time inside the application.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class AppTimeService {

    @SuppressWarnings("FieldMayBeFinal")
    private AtomicReference<LocalDateTime> cached = new AtomicReference<>();

    @Inject
    private RealTimeService realTimeSvc;

    @Inject
    private TimeDao timeDao;

    /**
     * Get current time. Accuracy is seconds. The retrieved value is cached for the life of this class. If a cached
     * value exists, it returns that value.
     *
     * @return current time inside the application, in that case timezone is UTC.
     * @since 1.0.0
     */
    public OffsetDateTime getOffsetNow() {
        return getOffsetNow(false);
    }

    /**
     * Get current time. Accuracy is seconds. The retrieved value is cached for the life of this class. If a cached
     * value exists, it returns that value.
     *
     * @param ignoreCached if {@code true} don't use cached value
     * @return current time inside the application, in that case timezone is UTC.
     * @since 1.0.0
     */
    public OffsetDateTime getOffsetNow(boolean ignoreCached) {
        return TimeUtils.toOffsetDateTime(getLocalNow(ignoreCached));
    }

    /**
     * Get current time. Accuracy is seconds. The retrieved value is cached for the life of this class. If a cached
     * value exists, it returns that value.
     *
     * @return current time inside the application, in that case timezone is UTC.
     * @since 1.0.0
     */
    public LocalDateTime getLocalNow() {
        return getLocalNow(false);
    }

    /**
     * Get current time. Accuracy is seconds. The retrieved value is cached for the life of this class. If a cached
     * value exists, it returns that value.
     *
     * @param ignoreCached if {@code true} don't use cached value
     * @return current time inside the application, in that case timezone is UTC.
     * @since 1.0.0
     */
    public LocalDateTime getLocalNow(boolean ignoreCached) {

        if (ignoreCached) {
            return calculateNow();
        }

        cached.getAndUpdate(c -> c != null ? c : calculateNow());

        return cached.get();

    }

    private LocalDateTime calculateNow() {

        LocalDateTime realNow = realTimeSvc.getLocalNow();
        Predicate<ValidityPeriod> validityTester = ValidityPeriod.containsWith(realNow);

        return timeDao.getTimeEntity(TimeKind.APP_NOW).filter(p(validityTester, TimeEntity::getValidityPeriod))
                .map(TimeEntity::getValue).orElse(realNow);

    }
}
