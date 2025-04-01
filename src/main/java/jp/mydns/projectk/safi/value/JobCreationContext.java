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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.util.TimeUtils;

/**
 * Information to create a <i>Job</i>.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class JobCreationContext {

    private final LocalDateTime scheduleTime;
    private final JobdefValue jobdef;

    /**
     * Constructor.
     *
     * @param scheduleTime job scheduling time
     * @param jobdef the {@code JobdefValue}. Constraint violations must be none.
     * @throws NullPointerException if any argument is {@code null}
     * @since 3.0.0
     */
    public JobCreationContext(OffsetDateTime scheduleTime, JobdefValue jobdef) {
        this.scheduleTime = TimeUtils.toLocalDateTime(Objects.requireNonNull(scheduleTime));
        this.jobdef = Objects.requireNonNull(jobdef);
    }

    /**
     * Get job schedule time. It timezone id UTC.
     *
     * @return job schedule time
     * @since 3.0.0
     */
    public LocalDateTime getScheduleTime() {
        return scheduleTime;
    }

    /**
     * Get job definition.
     *
     * @return job definition
     * @since 3.0.0
     */
    public JobdefValue getJobdef() {
        return jobdef;
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "JobCreationContext{" + ", scheduleTime=" + scheduleTime + "jobdef=" + jobdef + '}';
    }
}
