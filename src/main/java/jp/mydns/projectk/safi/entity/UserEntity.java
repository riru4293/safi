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
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.util.TimeUtils;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;
import jp.mydns.projectk.safi.value.JsonObjectValue;

/**
 * JPA entity for the <i>t_user</i> table.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Entity
@Cacheable(false)
@Table(name = "t_user")
public class UserEntity extends CommonEntity {

    private static final long serialVersionUID = 2711050439353117979L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Basic(optional = false)
    @Column(name = "from_ts", nullable = false)
    private LocalDateTime localFrom;

    @Basic(optional = false)
    @Column(name = "to_ts", nullable = false)
    private LocalDateTime localTo;

    @Column(name = "ignored", nullable = false)
    private boolean ignored;

    @Column(name = "name", length = 250)
    private String name;

    @Basic(optional = false)
    @Column(name = "props")
    private JsonObjectValue properties;

    @Basic(optional = false)
    @Column(name = "digest", nullable = false, length = 128)
    private String digest;

    /**
     * Get user id.
     *
     * @return user id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    public String getId() {
        return id;
    }

    /**
     * Set user id.
     *
     * @param id user id. Cannot update persisted value.
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
     * Get begin date-time of enabled period.
     *
     * @return begin date-time of enabled period
     * @since 3.0.0
     */
    public OffsetDateTime getFrom() {
        return TimeUtils.toOffsetDateTime(localFrom);
    }

    /**
     * Get begin date-time of enabled period.
     *
     * @return begin date-time of enabled period
     * @since 3.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getLocalFrom() {
        return localFrom;
    }

    /**
     * Set begin date-time of enabled period.
     *
     * @param from begin date-time of enabled period
     * @since 3.0.0
     */
    public void setLocalFrom(LocalDateTime from) {
        this.localFrom = from;
    }

    /**
     * Get end date-time of enabled period.
     *
     * @return end date-time of enabled period
     * @since 3.0.0
     */
    public OffsetDateTime getTo() {
        return TimeUtils.toOffsetDateTime(localTo);
    }

    /**
     * Get end date-time of enabled period.
     *
     * @return end date-time of enabled period
     * @since 3.0.0
     */
    @NotNull
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getLocalTo() {
        return localTo;
    }

    /**
     * Set end date-time of enabled period.
     *
     * @param to end date-time of enabled period
     * @since 3.0.0
     */
    public void setLocalTo(LocalDateTime to) {
        this.localTo = to;
    }

    /**
     * Get a flag of ignored.
     *
     * @return {@code true} if ignored, otherwise {@code false}.
     * @since 3.0.0
     */
    public boolean isIgnored() {
        return ignored;
    }

    /**
     * Set a flag of ignored.
     *
     * @param ignored {@code true} if ignored, otherwise {@code false}.
     * @since 3.0.0
     */
    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    /**
     * Get user name.
     *
     * @return user name. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getName() {
        return name;
    }

    /**
     * Set user name.
     *
     * @param name user name. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get user properties.
     *
     * @return user properties
     * @since 3.0.0
     */
    @NotNull
    public JsonObjectValue getProperties() {
        return properties;
    }

    /**
     * Set user properties.
     *
     * @param properties user properties
     * @since 3.0.0
     */
    public void setProperties(JsonObjectValue properties) {
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

    /**
     * Returns a hash code value.
     *
     * @return a hash code value. It is generated from the primary key value.
     * @since 3.0.0
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Indicates that other object is equal to this instance. Equality means that can be cast to this class and primary
     * key is match.
     *
     * @param other an any object
     * @return {@code true} if equals, otherwise {@code false}.
     * @since 3.0.0
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof UserEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "UserEntity{" + "id=" + id + ", enabled=" + enabled + ", from=" + localFrom + ", to=" + localTo
            + ", ignored=" + ignored + ", name=" + name + ", properies=" + properties + ", digest=" + digest + '}';
    }
}
