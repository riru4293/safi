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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jp.mydns.projectk.safi.value.SJson;

/**
 * Common content JPA entity. This class has id, enabled status, properties and digest value.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@MappedSuperclass
public abstract class ContentEntity extends NamedEntity {

    @java.io.Serial
    private static final long serialVersionUID = -4573193428404973991L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    protected String id;

    @Column(name = "enabled", nullable = false)
    protected boolean enabled;

    @Basic(optional = false)
    @Column(name = "props")
    protected SJson properties;

    @Basic(optional = false)
    @Column(name = "digest", nullable = false, length = 128)
    protected String digest;

    /**
     * Get content id.
     *
     * @return content id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    public String getId() {
        return id;
    }

    /**
     * Set content id.
     *
     * @param id content id. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the enabled state.
     *
     * @return {@code true} if enabled, otherwise {@code false}.
     * @since 3.0.0
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the enabled state.
     *
     * @param enabled {@code true} if enabled, otherwise {@code false}.
     * @since 3.0.0
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get content properties.
     *
     * @return content properties
     * @since 3.0.0
     */
    @NotNull
    public SJson getProperties() {
        return properties;
    }

    /**
     * Set content properties.
     *
     * @param properties content properties
     * @since 3.0.0
     */
    public void setProperties(SJson properties) {
        this.properties = properties;
    }

    /**
     * Get digest value of this entity.
     *
     * @return digest value
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 128)
    public String getDigest() {
        return digest;
    }

    /**
     * Set digest value of this entity.
     *
     * @param digest digest value
     * @since 3.0.0
     */
    public void setDigest(String digest) {
        this.digest = digest;
    }
}
