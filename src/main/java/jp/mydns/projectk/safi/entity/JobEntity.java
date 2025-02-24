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
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * JPA entity for the <i>t_job</i> table.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Entity
@Cacheable(false)
@Table(name = "t_job")
public class JobEntity implements Serializable {
    
    private static final long serialVersionUID = -1878103273727614325L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;
    
    /**
     * Get job id.
     *
     * @return job id
     * @since 3.0.0
     */
    @NotBlank
    @Size(max = 36)
    public String getId() {
        return id;
    }

    /**
     * Set job id.
     *
     * @param id job id. Cannot update persisted value.
     * @since 3.0.0
     */
    public void setId(String id) {
        this.id = id;
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
        return other instanceof JobEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "JobEntity{" + "id=" + id + '}';
    }
}
