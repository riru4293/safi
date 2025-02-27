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

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Version;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Common JPA entity. This class has one version number field qualified with {@link Version}. Thereby realizing an
 * optimistic lock of entity. And also this class implements entity's common footer items. Footer items are
 * automatically set by {@link FooterUpdater}.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@MappedSuperclass
@EntityListeners({FooterUpdater.class})
public abstract class CommonEntity implements Serializable {

    private static final long serialVersionUID = 7002393193138803696L;

    @Column(name = "note")
    private String note;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "reg_ts", updatable = false)
    private LocalDateTime regTime;

    @Column(name = "reg_id", updatable = false, length = 250)
    private String regId;

    @Column(name = "reg_ap", updatable = false, length = 250)
    private String regName;

    @Column(name = "upd_ts", insertable = false)
    private LocalDateTime updTime;

    @Column(name = "upd_id", insertable = false, length = 250)
    private String updId;

    @Column(name = "upd_ap", insertable = false, length = 250)
    private String updName;

    /**
     * Get a note for this entity. This value is only used to record notes about the data records represented by the
     * entity and is never used to process.
     *
     * @return note. It may be {@code null}.
     * @since 3.0.0
     */
    public String getNote() {
        return note;
    }

    /**
     * Set a note for this entity.
     *
     * @param note note. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Get entity version. This value used for optimistic locking. Will be thrown {@link OptimisticLockException} when
     * inserted or updated when the configured version number is not equal to that of the database. The version of the
     * entity before persistence is 0.
     *
     * @return entity version
     * @since 3.0.0
     */
    @PositiveOrZero
    @Version
    public int getVersion() {
        return version;
    }

    /**
     * Set entity version.
     *
     * @param version entity version
     * @since 3.0.0
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Get time the entity was persisted.
     *
     * @return persisted time. It time zone is UTC. It may be {@code null}.
     * @since 3.0.0
     */
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getRegTime() {
        return regTime;
    }

    /**
     * Set time the entity was persisted.
     *
     * @param regTime persisted time. It time zone is UTC. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    /**
     * Get id of the account who made the entity persistent.
     *
     * @return persisted account id. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getRegId() {
        return regId;
    }

    /**
     * Set id of the account who made the entity persistent.
     *
     * @param regId persisted account id. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setRegId(String regId) {
        this.regId = regId;
    }

    /**
     * Get name of the process who made the entity persistent.
     *
     * @return persisted process name. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getRegName() {
        return regName;
    }

    /**
     * Set name of the process who made the entity persistent.
     *
     * @param regName persisted process name. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setRegName(String regName) {
        this.regName = regName;
    }

    /**
     * Get time the entity was last updated.
     *
     * @return last updated time. It time zone is UTC. It may be {@code null}.
     * @since 3.0.0
     */
    @TimeRange
    @TimeAccuracy
    public LocalDateTime getUpdTime() {
        return updTime;
    }

    /**
     * Set time the entity was last updated.
     *
     * @param updTime last updated time. It time zone is UTC. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setUpdTime(LocalDateTime updTime) {
        this.updTime = updTime;
    }

    /**
     * Get id of the account who made the entity last updated.
     *
     * @return last updated account id. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getUpdId() {
        return updId;
    }

    /**
     * Set id of the account who made the entity last updated.
     *
     * @param updId last updated account id. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setUpdId(String updId) {
        this.updId = updId;
    }

    /**
     * Get name of the process who made the entity last updated.
     *
     * @return last updated process name. It may be {@code null}.
     * @since 3.0.0
     */
    @Size(max = 250)
    public String getUpdName() {
        return updName;
    }

    /**
     * Set name of the process who made the entity last updated.
     *
     * @param updName last updated process name. It can be set {@code null}.
     * @since 3.0.0
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }
}
