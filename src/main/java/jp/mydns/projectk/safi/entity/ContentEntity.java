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
import java.util.Optional;
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
public abstract class ContentEntity<T extends ContentEntity<T>> extends CommonEntity {

    private static final long serialVersionUID = -1113513901343265409L;

    /**
     * Content id.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 36)
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    protected String id;

    /**
     * Indicates that it is enable. {@code true} if enabled.
     *
     * @since 1.0.0
     */
    @Column(name = "enabled", nullable = false)
    protected boolean enabled;

    /**
     * Content name.
     *
     * @since 1.0.0
     */
    @Size(max = 255)
    @Basic(optional = false)
    @Column(name = "name", length = 255)
    protected String name;

    /**
     * The {@code AttsEmb}.
     *
     * @since 1.0.0
     */
    @NotNull
    @Valid
    @Embedded
    protected AttsEmb atts;

    /**
     * The {@code ValidityPeriodEmb}.
     *
     * @since 1.0.0
     */
    @NotNull
    @Valid
    @Embedded
    protected ValidityPeriodEmb validityPeriod;

    /**
     * Content digest value.
     *
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 128)
    @Basic(optional = false)
    @Column(name = "digest", nullable = false, length = 128)
    protected String digest;

    /**
     * {@link #enabled} in textual format.
     *
     * @since 1.0.0
     */
    @Basic(optional = false)
    @Column(name = "txt_enabled", insertable = false, updatable = false)
    protected String txtEnabled;

    /**
     * The {@code TxtValidityPeriodEmb}.
     *
     * @since 1.0.0
     */
    @Embedded
    protected TxtValidityPeriodEmb txtValidityPeriod = new TxtValidityPeriodEmb();

    /**
     * Constructs a new entity with all properties are default value.
     *
     * @since 1.0.0
     */
    public ContentEntity() {
    }

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
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
     * @return {@code true} if valid, otherwise {@code false}.
     * @since 1.0.0
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the valid state.
     *
     * @param enabled {@code true} if valid, otherwise {@code false}.
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
     * Get <i>Attribute</i> values.
     *
     * @return <i>Attribute</i> values
     * @since 1.0.0
     */
    public Map<AttKey, String> getAtts() {
        return Optional.ofNullable(this.atts).map(AttsEmb::toMap).orElseGet(Map::of);
    }

    /**
     * Set <i>Attribute</i> values.
     *
     * @param atts <i>Attribute</i> values
     * @since 1.0.0
     */
    public void setAtts(Map<AttKey, String> atts) {
        this.atts = Optional.ofNullable(atts).map(AttsEmb::new).orElse(null);
    }

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public ValidityPeriod getValidityPeriod() {
        return Optional.ofNullable(this.validityPeriod)
                .map(ValidityPeriodEmb::toValidityPeriod).orElse(null);
    }

    /**
     * Set the {@code ValidityPeriod}.
     *
     * @param validityPeriod the {@code ValidityPeriod}
     * @since 1.0.0
     */
    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriod = Optional.ofNullable(validityPeriod)
                .map(ValidityPeriodEmb::new).orElse(null);
    }

    /**
     * Get digest value of this content.
     *
     * @return digest value
     * @since 1.0.0
     */
    public String getDigest() {
        return digest;
    }

    /**
     * Set digest value of this content.
     *
     * @param digest digest value
     * @since 1.0.0
     */
    public void setDigest(String digest) {
        this.digest = digest;
    }
}
