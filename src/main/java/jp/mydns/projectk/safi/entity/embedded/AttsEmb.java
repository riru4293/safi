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
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.constant.AttKey;
import jp.mydns.projectk.safi.value.ContentValue;

/**
 * <i>Attribute</i> values as a built-in part of JPA entity.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 * @see ContentValue#getAtts() <i>Attribute</i> is explained in {@code ContentValue#getAtts()}
 */
@Embeddable
public class AttsEmb implements Serializable {

    private static final long serialVersionUID = 5106454913064640730L;

    /**
     * <i>Attribute</i> value #1.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att01")
    private String att01;

    /**
     * <i>Attribute</i> value #2.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att02")
    private String att02;

    /**
     * <i>Attribute</i> value #3.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att03")
    private String att03;

    /**
     * <i>Attribute</i> value #4.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att04")
    private String att04;

    /**
     * <i>Attribute</i> value #5.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att05")
    private String att05;

    /**
     * <i>Attribute</i> value #6.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att06")
    private String att06;

    /**
     * <i>Attribute</i> value #7.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att07")
    private String att07;

    /**
     * <i>Attribute</i> value #8.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att08")
    private String att08;

    /**
     * <i>Attribute</i> value #9.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att09")
    private String att09;

    /**
     * <i>Attribute</i> value #10.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Column(name = "att10")
    private String att10;

    /**
     * Construct a new instance with all properties are {@code null}.
     *
     * @since 1.0.0
     */
    public AttsEmb() {
    }

    /**
     * Construct a new instance with set all properties by copying them from other value.
     *
     * @param src source value
     * @throws NullPointerException if {@code src} is {@code null}
     * @since 1.0.0
     */
    public AttsEmb(Map<AttKey, String> src) {
        Objects.requireNonNull(src);

        this.att01 = src.get(AttKey.ATT01);
        this.att02 = src.get(AttKey.ATT02);
        this.att03 = src.get(AttKey.ATT03);
        this.att04 = src.get(AttKey.ATT04);
        this.att05 = src.get(AttKey.ATT05);
        this.att06 = src.get(AttKey.ATT06);
        this.att07 = src.get(AttKey.ATT07);
        this.att08 = src.get(AttKey.ATT08);
        this.att09 = src.get(AttKey.ATT09);
        this.att10 = src.get(AttKey.ATT10);
    }

    /**
     * Get <i>Attribute</i> values as {@code Map<AttKey, String>}.
     *
     * @return <i>Attribute</i> values
     * @since 1.0.0
     */
    public Map<AttKey, String> toMap() {
        Map<AttKey, String> atts = new EnumMap<>(AttKey.class);

        Optional.ofNullable(this.att01).ifPresent(v -> atts.put(AttKey.ATT01, v));
        Optional.ofNullable(this.att02).ifPresent(v -> atts.put(AttKey.ATT02, v));
        Optional.ofNullable(this.att03).ifPresent(v -> atts.put(AttKey.ATT03, v));
        Optional.ofNullable(this.att04).ifPresent(v -> atts.put(AttKey.ATT04, v));
        Optional.ofNullable(this.att05).ifPresent(v -> atts.put(AttKey.ATT05, v));
        Optional.ofNullable(this.att06).ifPresent(v -> atts.put(AttKey.ATT06, v));
        Optional.ofNullable(this.att07).ifPresent(v -> atts.put(AttKey.ATT07, v));
        Optional.ofNullable(this.att08).ifPresent(v -> atts.put(AttKey.ATT08, v));
        Optional.ofNullable(this.att09).ifPresent(v -> atts.put(AttKey.ATT09, v));
        Optional.ofNullable(this.att10).ifPresent(v -> atts.put(AttKey.ATT10, v));

        return Map.copyOf(atts);
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
     * @return {@code true} if equals, otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof AttsEmb o
                && Objects.equals(this.att01, o.att01) && Objects.equals(this.att02, o.att02)
                && Objects.equals(this.att03, o.att03) && Objects.equals(this.att04, o.att04)
                && Objects.equals(this.att05, o.att05) && Objects.equals(this.att06, o.att06)
                && Objects.equals(this.att07, o.att07) && Objects.equals(this.att08, o.att08)
                && Objects.equals(this.att09, o.att09) && Objects.equals(this.att10, o.att10);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "AttsEmb{" + AttKey.ATT01 + "=" + this.att01 + ", " + AttKey.ATT02 + "=" + this.att02
                + ", " + AttKey.ATT03 + "=" + this.att03 + ", " + AttKey.ATT04 + "=" + this.att04
                + ", " + AttKey.ATT05 + "=" + this.att05 + ", " + AttKey.ATT06 + "=" + this.att06
                + ", " + AttKey.ATT07 + "=" + this.att07 + ", " + AttKey.ATT08 + "=" + this.att08
                + ", " + AttKey.ATT09 + "=" + this.att09 + ", " + AttKey.ATT10 + "=" + this.att10 + '}';
    }
}
