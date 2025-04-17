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
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import jp.mydns.projectk.safi.constant.AppConfigId;
import jp.mydns.projectk.safi.dao.AppConfigDao;
import jp.mydns.projectk.safi.entity.AppConfigEntity;
import jp.mydns.projectk.safi.util.JsonValueUtils;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.SJson;

/**
 Provides a time inside the application.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface AppTimeService {

/**
 Get current time. Accuracy is seconds. The first value is remembered so that subsequent times the
 same value is returned.

 @return current time inside the application.
 @throws PersistenceException if failed database operation
 @since 3.0.0
 */
OffsetDateTime getOffsetNow();

/**
 Get current time. Accuracy is seconds. The first value is remembered so that subsequent times the
 same value is returned.

 @return current time inside the application, in that case timezone is UTC.
 @throws PersistenceException if failed database operation
 @since 3.0.0
 */
LocalDateTime getLocalNow();

/**
 Implements of the {@code AppTimeService}

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(AppTimeService.class)
@RequestScoped
class Impl implements AppTimeService {

private final AtomicReference<LocalDateTime> cached = new AtomicReference<>();
private final RealTimeService realTimeSvc;
private final AppConfigDao appConfigDao;

@Inject
@SuppressWarnings("unused")
Impl(RealTimeService realTimeSvc, AppConfigDao appConfigDao) {
    this.realTimeSvc = realTimeSvc;
    this.appConfigDao = appConfigDao;
}

/**
 {@inheritDoc}

 @throws PersistenceException if failed database operation
 @since 3.0.0
 */
@Override
public OffsetDateTime getOffsetNow() {
    return TimeUtils.toOffsetDateTime(getLocalNow());
}

/**
 {@inheritDoc}

 @return current time inside the application, in that case timezone is UTC.
 @throws PersistenceException if failed database operation
 @since 3.0.0
 */
@Override
public LocalDateTime getLocalNow() {
    cached.compareAndSet(null, appConfigDao.getAppConfig(AppConfigId.NOW).filter(this::isEnabled)
        .map(AppConfigEntity::getValue).map(SJson::unwrap).map(JsonValueUtils::toString)
        .map(TimeUtils::toLocalDateTime).orElseGet(realTimeSvc::getLocalNow));
    return cached.get();
}

private boolean isEnabled(AppConfigEntity e) {
    return !e.getValidityPeriod().isIgnored()
        && !e.getValidityPeriod().getFrom().isAfter(realTimeSvc.getLocalNow())
        && !e.getValidityPeriod().getTo().isBefore(realTimeSvc.getLocalNow());
}

}

}
