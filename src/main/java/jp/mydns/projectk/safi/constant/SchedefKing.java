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
package jp.mydns.projectk.safi.constant;

/**
 * Kind of the <i>Schedule definition</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public enum SchedefKing {
    /**
     * Scheduling definition to execute at a specified time and on specified days.
     *
     * @since 3.0.0
     */
    DAILY,
    /**
     * Scheduling definition to execute on a specified day of the week and at a specified interval weeks.
     *
     * @since 3.0.0
     */
    WEEKLY,
    /**
     * Scheduling definition to execute on a specified day in a specified month.
     *
     * @since 3.0.0
     */
    MONTHLY_DAYS,
    /**
     * Scheduling definition to execute on a specified day of the week in a specified month.
     *
     * @since 3.0.0
     */
    MONTHLY_WEEKDAYS,
    /**
     * Scheduling definition to execute only once at a specified date and time.
     *
     * @since 3.0.0
     */
    ONCE,
    /**
     * Scheduling definition to cancel scheduling of the same job definition ID within a specified period.
     *
     * @since 3.0.0
     */
    CANCEL
}
