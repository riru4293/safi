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
package jp.mydns.projectk.safi.dxo;

import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.ValidityPeriodValue;

/**
 * Data exchange processing for validity period.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public abstract class ValidityPeriodDxo {

    /**
     * Exchange to value object from built-in part of JPA entity.
     *
     * @param emb the {@code ValidityPeriodEmb}. Constraint violations must be none.
     * @return the {@code ValidityPeriodValue}
     * @throws NullPointerException if {@code emd} is {@code null}
     * @since 3.0.0
     */
    protected ValidityPeriodValue toValidityPeriodValue(ValidityPeriodEmb emb) {
        return new ValidityPeriodValue.Builder()
            .withFrom(TimeUtils.toOffsetDateTime(emb.getFrom()))
            .withTo(TimeUtils.toOffsetDateTime(emb.getTo()))
            .withIgnored(emb.isIgnored())
            .unsafeBuild();
    }
}
