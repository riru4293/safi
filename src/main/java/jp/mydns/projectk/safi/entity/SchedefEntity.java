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
import java.util.Objects;
import jp.mydns.projectk.safi.value.JsonWrapper;

/**
 * JPA entity for the <i>m_schedef</i> table.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Entity
@Cacheable(false)
@Table(name = "m_schedef")
public class SchedefEntity extends NamedEntity {

    private static final long serialVersionUID = 6291085973948685738L;

    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Basic(optional = false)
    @Column(name = "priority", nullable = false, length = 1)
    private String priority;

    @Basic(optional = false)
    @Column(name = "val", nullable = false)
    private JsonWrapper value;

    /**
     * Get job schedule definition id.
     *
     * @return job schedule definition id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    public String getId() {
        return id;
    }

    /**
     * Set job schedule definition id.
     *
     * @param id job schedule definition id. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get schedule definition priority.
     *
     * @return schedule definition priority. In case of a conflict with another schedule definition, the one with the
     * higher priority will be used. The higher the value, the higher the priority.
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 1)
    public String getPriority() {
        return priority;
    }

    /**
     * Set schedule definition priority.
     *
     * @param priority schedule definition priority
     * @since 3.0.0
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Get job schedule definition value.
     *
     * @return job schedule definition value
     * @since 3.0.0
     */
    @NotNull
    public JsonWrapper getValue() {
        return value;
    }

    /**
     * Set job schedule definition value.
     *
     * @param value job schedule definition value
     * @since 3.0.0
     */
    public void setValue(JsonWrapper value) {
        this.value = value;
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
        return other instanceof SchedefEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "SchedefEntity{" + "id=" + id + ", validityPeriod=" + validityPeriod + ", priority=" + priority
            + ", name=" + name + ", value=" + value + '}';
    }
}
