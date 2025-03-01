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
package jp.mydns.projectk.safi.value;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.time.OffsetDateTime;
import java.util.Objects;
import jp.mydns.projectk.safi.validator.TimeAccuracy;
import jp.mydns.projectk.safi.validator.TimeRange;

/**
 * Indicate that it can be persist.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * </ul>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface PersistableValue {

    /**
     * Get note for this value.
     *
     * @return note for this value. It may be {@code null}.
     * @since 3.0.0
     */
    @Schema(description = "Note for this value.")
    String getNote();

    /**
     * Get version number for this value. Used for optimistic locking when updating this value. Incremented after
     * update.
     *
     * @return version number. 0 means new value.
     * @since 3.0.0
     */
    @Schema(description = "Version number for this value.")
    @PositiveOrZero(groups = {Default.class})
    int getVersion();

    /**
     * Get registered time.
     *
     * @return registered time. It may be {@code null}.
     * @since 3.0.0
     */
    @Schema(description = "Registered time.")
    @TimeRange
    @TimeAccuracy
    OffsetDateTime getRegisterTime();

    /**
     * Get account id who made this value.
     *
     * @return account id. It may be {@code null}.
     * @since 3.0.0
     */
    @Schema(description = "Account id who made this value.")
    @Size(max = 250)
    String getRegisterAccountId();

    /**
     * Get name of the process who made this value.
     *
     * @return process name. It may be {@code null}.
     * @since 3.0.0
     */
    @Schema(description = "Process name who made this value.")
    @Size(max = 250)
    String getRegisterProcessName();

    /**
     * Get updated time.
     *
     * @return updated time. It may be {@code null}.
     * @since 3.0.0
     */
    @Schema(description = "Updated time.")
    @TimeRange
    @TimeAccuracy
    OffsetDateTime getUpdateTime();

    /**
     * Get account id who updated this value.
     *
     * @return account id. It may be {@code null}.
     * @since 3.0.0
     */
    @Schema(description = "Account id who updated this value.")
    @Size(max = 250)
    String getUpdateAccountId();

    /**
     * Get name of the process who made this value.
     *
     * @return process name. It may be {@code null}.
     * @since 3.0.0
     */
    @Schema(description = "Process name who updated this value.")
    @Size(max = 250)
    String getUpdateProcessName();

    /**
     * Abstract builder of the {@link PersistableValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends PersistableValue> {

        protected final Class<B> builderType;
        protected OffsetDateTime registerTime;
        protected String registerAccountId;
        protected String registerProcessName;
        protected OffsetDateTime updateTime;
        protected String updateAccountId;
        protected String updateProcessName;
        protected String note;
        protected int version;

        protected AbstractBuilder(Class<B> builderType) {
            this.builderType = Objects.requireNonNull(builderType);
        }

        /**
         * Set all properties from {@code src}.
         *
         * @param src source value
         * @return updated this
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 3.0.0
         */
        public B with(V src) {
            Objects.requireNonNull(src);

            this.registerTime = src.getRegisterTime();
            this.registerAccountId = src.getRegisterAccountId();
            this.registerProcessName = src.getRegisterProcessName();
            this.updateTime = src.getUpdateTime();
            this.updateAccountId = src.getUpdateAccountId();
            this.updateProcessName = src.getUpdateProcessName();
            this.note = src.getNote();
            this.version = src.getVersion();

            return builderType.cast(this);
        }

        /**
         * Set registered time.
         *
         * @param registerTime registered time. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public B withRegisterTime(OffsetDateTime registerTime) {
            this.registerTime = registerTime;
            return builderType.cast(this);
        }

        /**
         * Set account id who made this value.
         *
         * @param registerAccountId account id who made this value. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public B withRegisterAccountId(String registerAccountId) {
            this.registerAccountId = registerAccountId;
            return builderType.cast(this);
        }

        /**
         * Set process name who made this value.
         *
         * @param registerProcessName process name who made this value. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public B withRegisterProcessName(String registerProcessName) {
            this.registerProcessName = registerProcessName;
            return builderType.cast(this);
        }

        /**
         * Set update time.
         *
         * @param updateTime update time. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public B withUpdateTime(OffsetDateTime updateTime) {
            this.updateTime = updateTime;
            return builderType.cast(this);
        }

        /**
         * Set account id who updated this value.
         *
         * @param updateAccountId account id who updated this value. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public B withUpdateAccountId(String updateAccountId) {
            this.updateAccountId = updateAccountId;
            return builderType.cast(this);
        }

        /**
         * Set process name who updated this value.
         *
         * @param updateProcessName process name who updated this value. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public B withUpdateProcessName(String updateProcessName) {
            this.updateProcessName = updateProcessName;
            return builderType.cast(this);
        }

        /**
         * Set note for this value.
         *
         * @param note note for this value. It can be set {@code null}.
         * @return updated this
         * @since 3.0.0
         */
        public B withNote(String note) {
            this.note = note;
            return builderType.cast(this);
        }

        /**
         * Set version number for this value.
         *
         * @param version version number. 0 means new value.
         * @return updated this
         * @since 3.0.0
         */
        public B withVersion(int version) {
            this.version = version;
            return builderType.cast(this);
        }

        /**
         * Build a new inspected instance.
         *
         * @param validator the {@code Validator}
         * @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
         * @return new inspected instance
         * @throws NullPointerException if any argument is {@code null}
         * @throws ConstraintViolationException if occurred constraint violations when building
         * @since 3.0.0
         */
        public abstract V build(Validator validator, Class<?>... groups);

        /**
         * Abstract implements of the {@code PersistableValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected abstract static class AbstractBean implements PersistableValue {

            protected OffsetDateTime registerTime;
            protected String registerAccountId;
            protected String registerProcessName;
            protected OffsetDateTime updateTime;
            protected String updateAccountId;
            protected String updateProcessName;
            protected String note;
            protected int version;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 3.0.0
             */
            protected AbstractBean() {
            }

            /**
             * Constructor.
             *
             * @param builder the {@code PersistableValue.AbstractBuilder}
             * @since 3.0.0
             */
            protected AbstractBean(AbstractBuilder<?, ?> builder) {
                this.registerTime = builder.registerTime;
                this.registerAccountId = builder.registerAccountId;
                this.registerProcessName = builder.registerProcessName;
                this.updateTime = builder.updateTime;
                this.updateAccountId = builder.updateAccountId;
                this.updateProcessName = builder.updateProcessName;
                this.note = builder.note;
                this.version = builder.version;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public OffsetDateTime getRegisterTime() {
                return registerTime;
            }

            /**
             * Set registered time.
             *
             * @param registerTime registered time. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setRegisterTime(OffsetDateTime registerTime) {
                this.registerTime = registerTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getRegisterAccountId() {
                return registerAccountId;
            }

            /**
             * Set account id who made this value.
             *
             * @param registerAccountId account id who made this value. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setRegisterAccountId(String registerAccountId) {
                this.registerAccountId = registerAccountId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getRegisterProcessName() {
                return registerProcessName;
            }

            /**
             * Set process name who made this value.
             *
             * @param registerProcessName process name who made this value. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setRegisterProcessName(String registerProcessName) {
                this.registerProcessName = registerProcessName;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public OffsetDateTime getUpdateTime() {
                return updateTime;
            }

            /**
             * Set update time.
             *
             * @param updateTime update time. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setUpdateTime(OffsetDateTime updateTime) {
                this.updateTime = updateTime;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getUpdateAccountId() {
                return updateAccountId;
            }

            /**
             * Set account id who updated this value.
             *
             * @param updateAccountId account id who updated this value. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setUpdateAccountId(String updateAccountId) {
                this.updateAccountId = updateAccountId;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getUpdateProcessName() {
                return updateProcessName;
            }

            /**
             * Set process name who updated this value.
             *
             * @param updateProcessName process name who updated this value. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setUpdateProcessName(String updateProcessName) {
                this.updateProcessName = updateProcessName;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public String getNote() {
                return note;
            }

            /**
             * Set note for this value.
             *
             * @param note for this value. It can be set {@code null}.
             * @since 3.0.0
             */
            public void setNote(String note) {
                this.note = note;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public int getVersion() {
                return version;
            }

            /**
             * Set version number for this value.
             *
             * @param version version number
             * @since 3.0.0
             */
            public void setVersion(int version) {
                this.version = version;
            }
        }
    }
}
