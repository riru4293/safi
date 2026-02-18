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
package jp.mydns.projectk.safi.test;

import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.value.ValidityPeriodValue;

/**
 * Provides a values for testing.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class Values {

    /**
     * Returns a default value of the {@code ValidityPeriodValue}.
     *
     * @return {@code {from=2000-01-01T00:00:00, to=2999-12-31T23:59:59, ignored=false}}
     * @since 3.0.0
     */
    public static ValidityPeriodValue defaultValidityPeriodValue() {
        return new ValidityPeriodValue.Builder()
            .withFrom(ValidityPeriodValue.defaultFrom())
            .withTo(ValidityPeriodValue.defaultTo())
            .withIgnored(ValidityPeriodValue.defaultIgnored())
            .unsafeBuild();
    }
    
    /**
     * Returns a default value of the {@code ValidityPeriodEmb}.
     *
     * @return {@code {from=2000-01-01T00:00:00, to=2999-12-31T23:59:59, ignored=false}}
     * @since 3.0.0
     */
    public static ValidityPeriodEmb defaultValidityPeriodEmb() {
        
        var value = defaultValidityPeriodValue();
        
        var emb = new ValidityPeriodEmb();
        
        emb.setFrom(TimeUtils.toLocalDateTime(value.getFrom()));
        emb.setTo(TimeUtils.toLocalDateTime(value.getTo()));
        emb.setIgnored(value.isIgnored());
        
        return emb;
    }
}
