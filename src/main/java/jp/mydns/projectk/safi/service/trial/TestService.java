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
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import jp.mydns.projectk.safi.constant.AppConfigId;
import jp.mydns.projectk.safi.dao.CommonDao;
import jp.mydns.projectk.safi.entity.AppConfigEntity;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.util.JsonValueUtils;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.JsonWrapper;
import jp.mydns.projectk.safi.value.ValidityPeriodValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class TestService {

    private static final Logger log = LoggerFactory.getLogger(TestService.class);

    private CommonDao comDao;
    private EntityManager em;
    private ConfigService confSvc;

    @Inject
    public void setComDao(CommonDao comDao) {
        this.comDao = comDao;
    }

    @Inject
    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Inject
    public void setConfSvc(ConfigService confSvc) {
        this.confSvc = confSvc;
    }

    @Transactional
    public void test() {

        var e = Optional.ofNullable(em.find(AppConfigEntity.class, AppConfigId.NOW)).orElseGet(AppConfigEntity::new);

        e.setId(AppConfigId.NOW);

        var vp = new ValidityPeriodEmb();
        vp.setFrom(TimeUtils.toLocalDateTime(ValidityPeriodValue.defaultFrom()));
        vp.setTo(TimeUtils.toLocalDateTime(ValidityPeriodValue.defaultTo()));
        e.setValidityPeriod(vp);
        e.setName(UUID.randomUUID().toString());

        e.setValue(JsonWrapper.of(JsonValueUtils.toJsonValue(ValidityPeriodValue.defaultTo())));

        comDao.persistOrMerge(e);
    }

    public Path getPluginDir() {
        var p = confSvc.getPluginDir();
        log.error("[DEBUG INFO] Path is {}", p);
        return p;
    }
}
