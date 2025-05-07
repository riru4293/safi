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
package jp.mydns.projectk.safi.it;

import jakarta.enterprise.context.RequestScoped;
import jakarta.json.Json;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.Month;
import jp.mydns.projectk.safi.constant.AppConfigId;
import jp.mydns.projectk.safi.dao.AppConfigDao;
import jp.mydns.projectk.safi.entity.AppConfigEntity;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.service.RealTimeService;
import jp.mydns.projectk.safi.test.EntityFooterContextProducer;
import jp.mydns.projectk.safi.test.EntityManagerProducer;
import jp.mydns.projectk.safi.test.JndiServer;
import jp.mydns.projectk.safi.test.Values;
import jp.mydns.projectk.safi.util.JsonValueUtils;
import static org.assertj.core.api.Assertions.assertThat;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import jp.mydns.projectk.safi.value.SJson;

/**
 * Test application configuration.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@EnableWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppConfigIT {

    @WeldSetup
    WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
        .alternatives(EntityManagerProducer.class, EntityFooterContextProducer.class)
        .beanClasses(EntityManagerProducer.class, EntityFooterContextProducer.class, JndiServer.class,
            AppTimeService.Impl.class, RealTimeService.Impl.class, AppConfigDao.Impl.class)
    ).activate(RequestScoped.class).build();

    @BeforeAll
    @SuppressWarnings("unused")
    void init(JndiServer jndiSrv) {
        jndiSrv.bindBeanManager(weld.getBeanManager());
    }

    /**
     * Test create and get of the {@code AppConfigId.NOW}.
     *
     * @param em the {@code EntityManager}. This parameter resolved by CDI.
     * @param appTimeSvc the {@code AppTimeService}. This parameter resolved by CDI.
     * @since 3.0.0
     */
    @Test
    void testAppConfigNow(EntityManager em, AppTimeService appTimeSvc) {

        // Create
        {
            var e = new AppConfigEntity();

            e.setId(AppConfigId.NOW);
            e.setValidityPeriod(Values.defaultValidityPeriodEmb());
            e.setValue(SJson.of(JsonValueUtils.toJsonValue(LocalDateTime.of(2111, Month.MARCH, 12, 23, 34))));
            e.setName("Current time in application");
            e.setNote("Create new");

            em.getTransaction().begin();
            em.persist(e);
            em.getTransaction().commit();
        }

        // Verification after create.
        {
            var e = em.find(AppConfigEntity.class, AppConfigId.NOW);

            assertThat(e).isNotNull()
                .returns(AppConfigId.NOW, AppConfigEntity::getId)
                .returns(Values.defaultValidityPeriodEmb(), AppConfigEntity::getValidityPeriod)
                .returns(SJson.of(Json.createValue("2111-03-12T23:34:00")), AppConfigEntity::getValue)
                .returns("Current time in application", AppConfigEntity::getName)
                .returns("Create new", AppConfigEntity::getNote)
                .returns(1, AppConfigEntity::getVersion)
                .returns(LocalDateTime.of(2000, 4, 27, 20, 34, 56), AppConfigEntity::getRegTime)
                .returns("accountId", AppConfigEntity::getRegId)
                .returns("processName", AppConfigEntity::getRegName)
                .returns(null, AppConfigEntity::getUpdTime)
                .returns(null, AppConfigEntity::getUpdId)
                .returns(null, AppConfigEntity::getUpdName);
        }

        // Get test.
        {
            var result = appTimeSvc.getOffsetNow();

            assertThat(result).isEqualTo("2111-03-12T23:34:00Z");
        }
    }
}
