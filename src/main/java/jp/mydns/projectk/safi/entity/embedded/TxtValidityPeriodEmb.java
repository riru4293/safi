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
package jp.mydns.projectk.safi.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 * Text representation of the {@link ValidityPeriodEmb} as a built-in part of JPA entity.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Embeddable
public class TxtValidityPeriodEmb implements Serializable {

    private static final long serialVersionUID = -4883678074036718540L;

    @Column(name = "txt_from_ts", insertable = false, updatable = false)
    private String from;

    @Column(name = "txt_to_ts", insertable = false, updatable = false)
    private String to;

    @Column(name = "ban", insertable = false, updatable = false)
    private String ban;

    /**
     * Get begin time of valid period.
     *
     * @return begin time of valid period
     * @since 1.0.0
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get end time of valid period.
     *
     * @return end time of valid period
     * @since 1.0.0
     */
    public String getTo() {
        return to;
    }

    /**
     * Get flag indicating forced invalidity.
     *
     * @return {@code true} if force invalidity, otherwise {@code false}.
     * @since 1.0.0
     */
    public String getBan() {
        return ban;
    }
}
