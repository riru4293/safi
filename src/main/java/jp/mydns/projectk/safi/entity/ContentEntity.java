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
package jp.mydns.projectk.safi.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.AttKey;
import jp.mydns.projectk.safi.entity.embedded.AttsEmb;
import jp.mydns.projectk.safi.entity.embedded.NoEmbedNull;
import jp.mydns.projectk.safi.entity.embedded.TxtValidityPeriodEmb;
import jp.mydns.projectk.safi.entity.embedded.ValidityPeriodEmb;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.ValidityPeriod;
import org.eclipse.persistence.annotations.Customizer;

/**
 * An abstract implementation of the <i>ID-Content</i> entity.
 *
 * @param <T> content type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 * @see ContentValue <i>ID-Content</i> is explained in {@code ContentValue}
 */
@MappedSuperclass
@Customizer(NoEmbedNull.class)
public abstract class ContentEntity<T extends ContentEntity<T>> extends CommonEntity implements ContentValue<T> {

    private static final long serialVersionUID = -1113513901343265409L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    protected String id;

    @Column(name = "enabled", nullable = false)
    protected boolean enabled;

    @Basic(optional = false)
    @Column(name = "name", length = 100)
    protected String name;

    @NotNull
    @Valid
    @Embedded
    @SuppressWarnings("FieldMayBeFinal")
    protected AttsEmb attsEmb = new AttsEmb();

    @NotNull
    @Valid
    @Embedded
    protected ValidityPeriodEmb validityPeriod;

    @Basic(optional = false)
    @Column(name = "digest", nullable = false, length = 128)
    protected String digest;

    @Basic(optional = false)
    @Column(name = "txt_enabled", insertable = false, updatable = false)
    protected String txtEnabled;

    @Embedded
    protected TxtValidityPeriodEmb txtValidityPeriod = new TxtValidityPeriodEmb();

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Override
    public String getId() {
        return id;
    }

    /**
     * Set content id.
     *
     * @param id content id. Cannot update persisted value.
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the valid state.
     *
     * @return {@code true} if enabled, otherwise {@code false}.
     * @since 1.0.0
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the valid state.
     *
     * @param enabled {@code true} if enabled, otherwise {@code false}.
     * @since 1.0.0
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get content name.
     *
     * @return content name
     * @since 1.0.0
     */
    @Size(max = 100)
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set content name.
     *
     * @param name content name
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get <i>Attribute</i> collection.
     *
     * @return <i>Attribute</i> collection
     * @since 1.0.0
     */
    @Override
    public Map<AttKey, String> getAtts() {
        return attsEmb.toMap();
    }

    /**
     * Set <i>Attribute</i> collection.
     *
     * @param atts <i>Attribute</i> collection
     * @throws NullPointerException if {@code atts} is {@code null}
     * @since 1.0.0
     */
    public void setAtts(Map<AttKey, String> atts) {
        this.attsEmb.update(Objects.requireNonNull(atts));
    }

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    @Override
    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * Set the {@code ValidityPeriod}.
     *
     * @param validityPeriod the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod != null ? new ValidityPeriodEmb(validityPeriod) : null;
    }

    /**
     * Get digest value of this.
     *
     * @return digest value
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 128)
    @Override
    public String getDigest() {
        return digest;
    }

    /**
     * Set digest value of this.
     *
     * @param digest digest value
     * @since 1.0.0
     */
    public void setDigest(String digest) {
        this.digest = digest;
    }
}
