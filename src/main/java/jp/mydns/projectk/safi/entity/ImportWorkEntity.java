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

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * JPA entity for the <i>w_import</i> table. Used only to compare ingested content with registered content. Updates are
 * not possible, and only bulk registration and deletion are supported.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity
@Table(name = "w_import")
public class ImportWorkEntity implements Serializable {

    private static final long serialVersionUID = 7862710634490210812L;

    @OneToOne(mappedBy = "importWorkEntity", fetch = FetchType.LAZY)
    private UserEntity userEntity;

//    @OneToOne(mappedBy = "importWorkEntity", fetch = FetchType.LAZY)
//    private MediumEntity mediumEntity;
//
//    @OneToOne(mappedBy = "importWorkEntity", fetch = FetchType.LAZY)
//    private BelongOrgEntity belongOrgEntity;
//
//    @OneToOne(mappedBy = "importWorkEntity", fetch = FetchType.LAZY)
//    private Org1Entity org1Entity;
//
//    @OneToOne(mappedBy = "importWorkEntity", fetch = FetchType.LAZY)
//    private Org2Entity org2Entity;
//
//    @OneToOne(mappedBy = "importWorkEntity", fetch = FetchType.LAZY)
//    private BelongGroupEntity belongGroupEntity;
//
//    @OneToOne(mappedBy = "importWorkEntity", fetch = FetchType.LAZY)
//    private GroupEntity groupEntity;
    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Basic(optional = false)
    @Column(nullable = false, updatable = false, length = 128)
    private String digest;

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
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
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get content digest value.
     *
     * @return content digest value
     * @since 1.0.0
     */
    @NotBlank
    @Size(max = 128)
    public String getDigest() {
        return digest;
    }

    /**
     * Set content digest value.
     *
     * @param digest content digest value. Cannot update persisted value.
     * @since 1.0.0
     */
    public void setDigest(String digest) {
        this.digest = digest;
    }

    /**
     * Get the {@code UserEntity}.
     *
     * @return the {@code UserEntity}
     * @since 1.0.0
     */
    @JsonbTransient
    public UserEntity getUserEntity() {
        return userEntity;
    }

//    /**
//     * Get the {@code MediumEntity}.
//     *
//     * @return the {@code MediumEntity}
//     * @since 1.0.0
//     */
//    @JsonbTransient
//    public MediumEntity getMediumEntity() {
//        return mediumEntity;
//    }
//
//    /**
//     * Get the {@code BelongOrgEntity}.
//     *
//     * @return the {@code BelongOrgEntity}
//     * @since 1.0.0
//     */
//    @JsonbTransient
//    public BelongOrgEntity getBelongOrgEntity() {
//        return belongOrgEntity;
//    }
//
//    /**
//     * Get the {@code Org1Entity}.
//     *
//     * @return the {@code Org1Entity}
//     * @since 1.0.0
//     */
//    @JsonbTransient
//    public Org1Entity getOrg1Entity() {
//        return org1Entity;
//    }
//
//    /**
//     * Get the {@code Org2Entity}.
//     *
//     * @return the {@code Org2Entity}
//     * @since 1.0.0
//     */
//    @JsonbTransient
//    public Org2Entity getOrg2Entity() {
//        return org2Entity;
//    }
//
//    /**
//     * Get the {@code BelongGroupEntity}.
//     *
//     * @return the {@code BelongGroupEntity}
//     * @since 1.0.0
//     */
//    @JsonbTransient
//    public BelongGroupEntity getBelongGroupEntity() {
//        return belongGroupEntity;
//    }
//
//    /**
//     * Get the {@code GroupEntity}.
//     *
//     * @return the {@code GroupEntity}
//     * @since 1.0.0
//     */
//    @JsonbTransient
//    public GroupEntity getGroupEntity() {
//        return groupEntity;
//    }
    /**
     * Returns a hash code value.
     *
     * @return a hash code value. It is generated from the primary key value.
     * @since 1.0.0
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
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ImportWorkEntity o && Objects.equals(id, o.id);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "ImportWorkEntity{" + "id=" + id + ", digest=" + digest + '}';
    }
}
