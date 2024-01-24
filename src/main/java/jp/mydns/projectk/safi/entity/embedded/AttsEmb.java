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
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.AttKey;
import jp.mydns.projectk.safi.value.ContentValue;

/**
 * Implementation of the <i>Attribute</i> collection as a built-in part of JPA entity.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 * @see ContentValue#getAtts() <i>Attribute</i> is explained in {@code ContentValue#getAtts()}
 */
@Embeddable
public class AttsEmb implements Serializable {

    private static final long serialVersionUID = 5106454913064640730L;

    @Size(max = 200)
    @Column(name = "att01")
    private String att01;

    @Size(max = 200)
    @Column(name = "att02")
    private String att02;

    @Size(max = 200)
    @Column(name = "att03")
    private String att03;

    @Size(max = 200)
    @Column(name = "att04")
    private String att04;

    @Size(max = 200)
    @Column(name = "att05")
    private String att05;

    @Size(max = 200)
    @Column(name = "att06")
    private String att06;

    @Size(max = 200)
    @Column(name = "att07")
    private String att07;

    @Size(max = 200)
    @Column(name = "att08")
    private String att08;

    @Size(max = 200)
    @Column(name = "att09")
    private String att09;

    @Size(max = 200)
    @Column(name = "att10")
    private String att10;

    /**
     * Update all fields with the value of {@code atts}.
     *
     * @param atts <i>Attribute</i> collection
     * @return previous <i>Attribute</i> collection
     * @throws NullPointerException if {@code atts} is {@code null}
     * @since 1.0.0
     */
    public Map<AttKey, String> update(Map<AttKey, String> atts) {

        Map<AttKey, String> previous = toMap();

        att01 = atts.get(AttKey.ATT01);
        att02 = atts.get(AttKey.ATT02);
        att03 = atts.get(AttKey.ATT03);
        att04 = atts.get(AttKey.ATT04);
        att05 = atts.get(AttKey.ATT05);
        att06 = atts.get(AttKey.ATT06);
        att07 = atts.get(AttKey.ATT07);
        att08 = atts.get(AttKey.ATT08);
        att09 = atts.get(AttKey.ATT09);
        att10 = atts.get(AttKey.ATT10);

        return previous;

    }

    /**
     * Get <i>Attribute</i> collection.
     *
     * @return <i>Attribute</i> collection
     * @since 1.0.0
     */
    public Map<AttKey, String> toMap() {

        Map<AttKey, String> atts = new EnumMap<>(AttKey.class);

        atts.put(AttKey.ATT01, att01);
        atts.put(AttKey.ATT02, att02);
        atts.put(AttKey.ATT03, att03);
        atts.put(AttKey.ATT04, att04);
        atts.put(AttKey.ATT05, att05);
        atts.put(AttKey.ATT06, att06);
        atts.put(AttKey.ATT07, att07);
        atts.put(AttKey.ATT08, att08);
        atts.put(AttKey.ATT09, att09);
        atts.put(AttKey.ATT10, att10);

        atts.values().removeIf(Objects::isNull);

        return Collections.unmodifiableMap(atts);

    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(att01, att02, att03, att04, att05, att06, att07, att08, att09, att10);
    }

    /**
     * Indicates that other object is equal to this instance.
     *
     * @param other an any object
     * @return {@code true} if equals otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof AttsEmb o && Objects.equals(att01, o.att01)
                && Objects.equals(att02, o.att02) && Objects.equals(att03, o.att03) && Objects.equals(att04, o.att04)
                && Objects.equals(att05, o.att05) && Objects.equals(att06, o.att06) && Objects.equals(att07, o.att07)
                && Objects.equals(att08, o.att08) && Objects.equals(att09, o.att09) && Objects.equals(att10, o.att10);
    }

    /**
     * Returns a string representation.
     *
     * @return string representation of this instance
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "Attributes{" + AttKey.ATT01 + "=" + att01 + ", " + AttKey.ATT02 + "=" + att02
                + ", " + AttKey.ATT03 + "=" + att03 + ", " + AttKey.ATT04 + "=" + att04
                + ", " + AttKey.ATT05 + "=" + att05 + ", " + AttKey.ATT06 + "=" + att06
                + ", " + AttKey.ATT07 + "=" + att07 + ", " + AttKey.ATT08 + "=" + att08
                + ", " + AttKey.ATT09 + "=" + att09 + ", " + AttKey.ATT10 + "=" + att10 + '}';
    }
}
