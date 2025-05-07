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
package jp.mydns.projectk.safi.value;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of class {@code JobCreationContext}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class JobCreationContextTest {

    /**
     * Test of getJobId method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetJobId() {
        var jobdef = mock(JobdefValue.class);

        var scheduleTime = OffsetDateTime.of(2000, 1, 1, 12, 33, 55, 0, ZoneOffset.UTC);

        var instance = new JobCreationContext("job-id", scheduleTime, jobdef);

        assertThat(instance).returns("job-id", JobCreationContext::getJobId);
    }

    /**
     * Test of getScheduleTime method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetScheduleTime() {
        var jobdef = mock(JobdefValue.class);

        var scheduleTime = OffsetDateTime.of(2000, 1, 1, 12, 33, 55, 0, ZoneOffset.UTC);

        var instance = new JobCreationContext("job-id", scheduleTime, jobdef);

        assertThat(instance).returns(scheduleTime.toLocalDateTime(), JobCreationContext::getScheduleTime);
    }

    /**
     * Test of getJobdef method.
     *
     * @since 3.0.0
     */
    @Test
    void testGetJobdef() {
        var jobdef = mock(JobdefValue.class);

        var scheduleTime = OffsetDateTime.of(2000, 1, 1, 12, 33, 55, 0, ZoneOffset.UTC);

        var instance = new JobCreationContext("job-id", scheduleTime, jobdef);

        assertThat(instance).returns(jobdef, JobCreationContext::getJobdef);
    }

    /**
     * Test of toString method.
     *
     * @since 3.0.0
     */
    @Test
    void testToString() {
        String tmpl = "JobCreationContext{jobId=%s, scheduleTime=%s, jobdef=%s}";

        var jobdef = mock(JobdefValue.class);

        doReturn("jobdef").when(jobdef).toString();

        var scheduleTime = OffsetDateTime.of(2000, 1, 1, 12, 33, 55, 0, ZoneOffset.UTC);

        var instance = new JobCreationContext("job-id", scheduleTime, jobdef);

        assertThat(instance).hasToString(tmpl, "job-id", scheduleTime.toLocalDateTime(), jobdef);
    }
}
