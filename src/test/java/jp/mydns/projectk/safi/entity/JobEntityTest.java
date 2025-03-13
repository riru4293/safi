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
import java.time.LocalDateTime;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.constant.JobTarget;
import jp.mydns.projectk.safi.value.JsonArrayValue;
import jp.mydns.projectk.safi.value.JsonObjectValue;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class {@code JobEntity}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class JobEntityTest {

    /**
     * Test of hashCode method.
     *
     * @since 3.0.0
     */
    @Test
    void testHashCode() {
        var entity = new JobEntity();

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
        var entity = new JobEntity();

        assertThat(entity).hasSameHashCodeAs(0);
    }

    /**
     * Test of equals method.
     *
     * @since 3.0.0
     */
    @Test
    void testEquals() {
        var entity1 = new JobEntity();
        var entity2 = new JobEntity();

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
        var entity1 = new JobEntity();
        var entity2 = new JobEntity();

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
        var entity1 = new JobEntity();
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
        String tmpl = "JobEntity{id=%s, status=%s, kind=%s, target=%s, scheduleTime=%s, limitTime=%s, beginTime=%s"
            + ", endTime=%s, properties=%s, jobdefId=%s, jobdef=%s, schedefId=%s, schedef=%s, resultMessages=%s}";

        var entity = new JobEntity();
        entity.setId("job-id");
        entity.setStatus(JobStatus.SUCCESS);
        entity.setKind(JobKind.REBUILD);
        entity.setTarget(JobTarget.ASSET);
        entity.setScheduleTime(LocalDateTime.of(2000, 1, 1, 0, 0));
        entity.setLimitTime(LocalDateTime.of(2001, 2, 1, 0, 0));
        entity.setBeginTime(LocalDateTime.of(2002, 3, 1, 0, 0));
        entity.setEndTime(LocalDateTime.of(2003, 4, 1, 0, 0));
        entity.setProperties(JsonObjectValue.of(Json.createObjectBuilder().add("name", "props").build()));
        entity.setJobdefId("jobdef-id");
        entity.setJobdef(JsonObjectValue.of(Json.createObjectBuilder().add("name", "jobdef").build()));
        entity.setSchedefId("schedef-id");
        entity.setSchedef(JsonObjectValue.of(Json.createObjectBuilder().add("name", "schedef").build()));
        entity.setResultMessages(new JsonArrayValue(Json.createArrayBuilder().add("result").build()));

        assertThat(entity).hasToString(tmpl, "job-id", "SUCCESS", "REBUILD", "ASSET", "2000-01-01T00:00",
            "2001-02-01T00:00", "2002-03-01T00:00", "2003-04-01T00:00", "{\"name\":\"props\"}", "jobdef-id",
            "{\"name\":\"jobdef\"}", "schedef-id", "{\"name\":\"schedef\"}", "[\"result\"]");
    }
}
