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
package jp.mydns.projectk.safi.entity;

import jakarta.json.Json;
import java.time.Duration;
import java.time.LocalDateTime;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.value.JsonObjectValue;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code JobdefEntity}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class JobdefEntityTest {

    /**
     * Test of hashCode method.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCode() {
        var entity = new JobdefEntity();

        entity.setId("A");

        assertThat(entity).hasSameHashCodeAs("A");
    }

    /**
     * Test of hashCode method if null id.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCodeIfNullId() {
        var entity = new JobdefEntity();

        assertThat(entity).hasSameHashCodeAs(0);
    }

    /**
     * Test of equals method.
     *
     * @since 3.0.0
     */
    @Test
    void testEquals() {
        var entity1 = new JobdefEntity();
        var entity2 = new JobdefEntity();

        entity1.setId("A");
        entity2.setId("A");

        assertThat(entity1).isEqualTo(entity2);
    }

    /**
     * Test of equals method if not same id.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfNotSameId() {
        var entity1 = new JobdefEntity();
        var entity2 = new JobdefEntity();

        entity1.setId("A");
        entity2.setId("B");

        assertThat(entity1).isNotEqualTo(entity2);
    }

    /**
     * Test of equals method if other class.
     *
     * @since 3.0.0
     */
    @Test
    void testEqualsIfOtherClass() {
        var entity1 = new JobdefEntity();
        var entity2 = new Object();

        entity1.setId("A");

        assertThat(entity1).isNotEqualTo(entity2);
    }

    /**
     * Test of toString method.
     *
     * @since 3.0.0
     */
    @Test
    void testToString() {
        String tmpl = "JobdefEntity{id=%s, validityPeriod=%s, jobKind=%s, jobTarget=%s, timeout=%s, name=%s"
            + ", pluginName=%s, trnsdef=%s, filtdef=%s, jobProperties=%s}";

        var vp = new ValidityPeriodEmb();
        vp.setIgnored(true);
        vp.setLocalFrom(LocalDateTime.of(2000, 1, 1, 0, 0));
        vp.setLocalTo(LocalDateTime.of(2000, 1, 2, 0, 0));

        var entity = new JobdefEntity();
        entity.setId("jobdef-id");
        entity.setValidityPeriod(vp);
        entity.setJobKind(JobKind.REBUILD);
        entity.setJobTarget(JobTarget.ASSET);
        entity.setTimeout(Duration.ZERO);
        entity.setName("jobdef-name");
        entity.setPluginName("plugin-name");
        entity.setTrnsdef(new JsonObjectValue(Json.createObjectBuilder().add("name", "trnsdef").build()));
        entity.setFiltdef(new JsonObjectValue(Json.createObjectBuilder().add("name", "filtdef").build()));
        entity.setJobProperties(new JsonObjectValue(Json.createObjectBuilder().add("name", "job-props").build()));

        assertThat(entity).hasToString(tmpl, "jobdef-id", vp, "REBUILD", "ASSET", "PT0S", "jobdef-name", "plugin-name",
            "{\"name\":\"trnsdef\"}", "{\"name\":\"filtdef\"}", "{\"name\":\"job-props\"}");
    }
}
