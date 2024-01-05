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
import jp.mydns.projectk.safi.constant.AttName;

/**
 * Attribute values.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
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
     * Update all fields of this from specified attributes.
     *
     * @param atts attributes
     * @return previous values
     * @throws NullPointerException if {@code atts} is {@code null}
     * @since 1.0.0
     */
    public Map<AttName, String> update(Map<AttName, String> atts) {

        Map<AttName, String> previous = toMap();

        att01 = atts.get(AttName.ATT01);
        att02 = atts.get(AttName.ATT02);
        att03 = atts.get(AttName.ATT03);
        att04 = atts.get(AttName.ATT04);
        att05 = atts.get(AttName.ATT05);
        att06 = atts.get(AttName.ATT06);
        att07 = atts.get(AttName.ATT07);
        att08 = atts.get(AttName.ATT08);
        att09 = atts.get(AttName.ATT09);
        att10 = atts.get(AttName.ATT10);

        return previous;

    }

    /**
     * Get attributes.
     *
     * @return attributes
     * @since 1.0.0
     */
    public Map<AttName, String> toMap() {

        Map<AttName, String> atts = new EnumMap<>(AttName.class);

        atts.put(AttName.ATT01, att01);
        atts.put(AttName.ATT02, att02);
        atts.put(AttName.ATT03, att03);
        atts.put(AttName.ATT04, att04);
        atts.put(AttName.ATT05, att05);
        atts.put(AttName.ATT06, att06);
        atts.put(AttName.ATT07, att07);
        atts.put(AttName.ATT08, att08);
        atts.put(AttName.ATT09, att09);
        atts.put(AttName.ATT10, att10);

        atts.values().removeIf(Objects::isNull);

        return Collections.unmodifiableMap(atts);

    }

    /**
     * Returns a hash code value of this.
     *
     * @return a hash code value. It is generated from all the field values.
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(att01, att02, att03, att04, att05, att06, att07, att08, att09, att10);
    }

    /**
     * Indicates that other object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if equals otherwise {@code false}
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof AttsEmb o
                && Objects.equals(att01, o.att01) && Objects.equals(att02, o.att02) && Objects.equals(att03, o.att03)
                && Objects.equals(att04, o.att04) && Objects.equals(att05, o.att05) && Objects.equals(att06, o.att06)
                && Objects.equals(att07, o.att07) && Objects.equals(att08, o.att08)
                && Objects.equals(att09, o.att09) && Objects.equals(att10, o.att10);
    }

    /**
     * Returns a string representation of this.
     *
     * @return string representation of this
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "Attributes{" + AttName.ATT01 + "=" + att01 + ", " + AttName.ATT02 + "=" + att02
                + ", " + AttName.ATT03 + "=" + att03 + ", " + AttName.ATT04 + "=" + att04
                + ", " + AttName.ATT05 + "=" + att05 + ", " + AttName.ATT06 + "=" + att06
                + ", " + AttName.ATT07 + "=" + att07 + ", " + AttName.ATT08 + "=" + att08
                + ", " + AttName.ATT09 + "=" + att09 + ", " + AttName.ATT10 + "=" + att10 + '}';
    }
}
