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
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * @version 1.0.0
 * @since 1.0.0
 */
@MappedSuperclass
@EntityListeners({FooterUpdater.class})
public abstract class CommonEntity implements Serializable {

    private static final long serialVersionUID = 7002393193138803696L;

    @Basic(optional = false)
    @Column(name = "note")
    protected String note;

    @PositiveOrZero
    @Version
    @Column(name = "version", nullable = false)
    protected int version;

    @NotNull
    @TimeRange
    @TimeAccuracy
    @Basic(optional = false)
    @Column(name = "reg_ts", updatable = false, nullable = false)
    protected LocalDateTime registerTime;

    @NotBlank
    @Size(max = 250)
    @Basic(optional = false)
    @Column(name = "reg_id", updatable = false, nullable = false, length = 250)
    protected String registerAccountId;

    @NotBlank
    @Size(max = 250)
    @Basic(optional = false)
    @Column(name = "reg_ap", updatable = false, nullable = false, length = 250)
    protected String registerProcessName;

    @NotNull
    @TimeRange
    @TimeAccuracy
    @Basic(optional = false)
    @Column(name = "upd_ts", nullable = false)
    protected LocalDateTime updateTime;

    @NotBlank
    @Size(max = 250)
    @Basic(optional = false)
    @Column(name = "upd_id", nullable = false, length = 250)
    protected String updateAccountId;

    @NotBlank
    @Size(max = 250)
    @Basic(optional = false)
    @Column(name = "upd_ap", nullable = false, length = 250)
    protected String updateProcessName;

    /**
     * Get a note for this entity. This value is only used to record notes about the data records represented by the
     * entity and is never used to process.
     *
     * @return note. It may be {@code null}.
     * @since 1.0.0
     */
    public String getNote() {
        return note;
    }

    /**
     * Set a note for this entity.
     *
     * @param note note. It can be set {@code null}.
     * @see #getNote() Note is explained in {@code #getNote()}
     * @since 1.0.0
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
     * @since 1.0.0
     */
    public int getVersion() {
        return version;
    }

    /**
     * Set entity version.
     *
     * @param version entity version
     * @see #getVersion() Version is explained in {@code #getVersion()}
     * @since 1.0.0
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Get time the entity was persisted.
     *
     * @return persisted time. It time zone is UTC.
     * @since 1.0.0
     */
    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    /**
     * Set time the entity was persisted.
     *
     * @param registerTime persisted time. It time zone is UTC.
     * @since 1.0.0
     */
    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    /**
     * Get id of the user who made the entity persistent.
     *
     * @return persisted user id
     * @since 1.0.0
     */
    public String getRegisterAccountId() {
        return registerAccountId;
    }

    /**
     * Set id of the user who made the entity persistent.
     *
     * @param registerAccountId persisted user id
     * @since 1.0.0
     */
    public void setRegisterAccountId(String registerAccountId) {
        this.registerAccountId = registerAccountId;
    }

    /**
     * Get name of the process who made the entity persistent.
     *
     * @return persisted process name
     * @since 1.0.0
     */
    public String getRegisterProcessName() {
        return registerProcessName;
    }

    /**
     * Set name of the process who made the entity persistent.
     *
     * @param registerProcessName persisted process name
     * @since 1.0.0
     */
    public void setRegisterProcessName(String registerProcessName) {
        this.registerProcessName = registerProcessName;
    }

    /**
     * Get time the entity was last updated.
     *
     * @return last updated time. It time zone is UTC.
     * @since 1.0.0
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * Set time the entity was last updated.
     *
     * @param updateTime last updated time, It time zone is UTC.
     * @since 1.0.0
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * Get id of the user who made the entity last updated.
     *
     * @return last updated user id
     * @since 1.0.0
     */
    public String getUpdateAccountId() {
        return updateAccountId;
    }

    /**
     * Set id of the user who made the entity last updated.
     *
     * @param updateAccountId last updated user id
     * @since 1.0.0
     */
    public void setUpdateAccountId(String updateAccountId) {
        this.updateAccountId = updateAccountId;
    }

    /**
     * Get name of the process who made the entity last updated.
     *
     * @return last updated process name
     * @since 1.0.0
     */
    public String getUpdateProcessName() {
        return updateProcessName;
    }

    /**
     * Set name of the process who made the entity last updated.
     *
     * @param updateProcessName last updated process name
     * @since 1.0.0
     */
    public void setUpdateProcessName(String updateProcessName) {
        this.updateProcessName = updateProcessName;
    }
}
