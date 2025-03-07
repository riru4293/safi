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

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;

/**
 * Common named values JPA entity. This class has name and validity period.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@MappedSuperclass
public abstract class NamedEntity extends CommonEntity {

    private static final long serialVersionUID = -6229000110509598422L;

    @Column(name = "name", length = 250)
    protected String name;

    @Embedded
    protected ValidityPeriodEmb validityPeriod;

    /**
     * Get name of value.
     *
     * @return name of value. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getName() {
        return name;
    }

    /**
     * Set name of value.
     *
     * @param name name of value. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the {@code ValidityPeriodEmb}.
     *
     * @return the {@code ValidityPeriodEmb}
     * @since 3.0.0
     */
    @NotNull
    @Valid
    public ValidityPeriodEmb getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * Set the {@code ValidityPeriodEmb}.
     *
     * @param validityPeriod the {@code ValidityPeriodEmb}
     * @since 3.0.0
     */
    public void setValidityPeriod(ValidityPeriodEmb validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
}
