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
import java.util.Optional;
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
     * @return note for this value
     * @since 3.0.0
     */
    @Schema(description = "Note for this value.")
    Optional<String> getNote();

    /**
     * Get version number for this value. Used for optimistic locking when updating this value. Please set to 0 for
     * creation, set to the same value as when getting for update.
     *
     * @return version number. 0 means new value.
     * @since 3.0.0
     */
    @Schema(description = "Version number for this value.", defaultValue = "0", minimum = "0")
    @PositiveOrZero(groups = {Default.class})
    int getVersion();

    /**
     * Get registered time.
     *
     * @return registered time
     * @since 3.0.0
     */
    @Schema(description = "Registered time. Values from 2000-01-01T00:00:00Z to 2999-12-31T23:59:59Z can be specified.",
            accessMode = Schema.AccessMode.READ_ONLY)
    Optional<@TimeRange(groups = {Default.class}) @TimeAccuracy(groups = {Default.class}) OffsetDateTime> getRegisterTime();

    /**
     * Get account id who made this value.
     *
     * @return account id
     * @since 3.0.0
     */
    @Schema(description = "Account id who made this value.", accessMode = Schema.AccessMode.READ_ONLY,
            maxLength = 250)
    Optional<@Size(max = 250, groups = {Default.class}) String> getRegisterAccountId();

    /**
     * Get name of the process who made this value.
     *
     * @return process name
     * @since 3.0.0
     */
    @Schema(description = "Process name who made this value.", accessMode = Schema.AccessMode.READ_ONLY,
            maxLength = 250)
    Optional<@Size(max = 250, groups = {Default.class}) String> getRegisterProcessName();

    /**
     * Get updated time.
     *
     * @return updated time
     * @since 3.0.0
     */
    @Schema(description = "Updated time. Values from 2000-01-01T00:00:00Z to 2999-12-31T23:59:59Z can be specified.",
            accessMode = Schema.AccessMode.READ_ONLY)
    Optional<@TimeRange(groups = {Default.class}) @TimeAccuracy(groups = {Default.class}) OffsetDateTime> getUpdateTime();

    /**
     * Get account id who updated this value.
     *
     * @return account id
     * @since 3.0.0
     */
    @Schema(description = "Account id who updated this value.", accessMode = Schema.AccessMode.READ_ONLY,
            maxLength = 250)
    Optional<@Size(max = 250, groups = {Default.class}) String> getUpdateAccountId();

    /**
     * Get name of the process who made this value.
     *
     * @return process name
     * @since 3.0.0
     */
    @Schema(description = "Process name who updated this value.", accessMode = Schema.AccessMode.READ_ONLY,
            maxLength = 250)
    Optional<@Size(max = 250, groups = {Default.class}) String> getUpdateProcessName();

    /**
     * Abstract builder of the {@code PersistableValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends PersistableValue> {

        protected final Class<B> builderType;
        protected String note;
        protected int version;
        protected OffsetDateTime registerTime;
        protected String registerAccountId;
        protected String registerProcessName;
        protected OffsetDateTime updateTime;
        protected String updateAccountId;
        protected String updateProcessName;

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

            this.note = src.getNote().orElse(null);
            this.version = src.getVersion();
            this.registerTime = src.getRegisterTime().orElse(null);
            this.registerAccountId = src.getRegisterAccountId().orElse(null);
            this.registerProcessName = src.getRegisterProcessName().orElse(null);
            this.updateTime = src.getUpdateTime().orElse(null);
            this.updateAccountId = src.getUpdateAccountId().orElse(null);
            this.updateProcessName = src.getUpdateProcessName().orElse(null);

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
         * Build a new instance. It instance may not meet that constraint. Use only if the original value is completely
         * reliable.
         *
         * @return new unsafe instance
         * @since 3.0.0
         */
        public abstract V unsafeBuild();

        /**
         * Abstract implements of the {@code PersistableValue}.
         *
         * @author riru
         * @version 3.0.0
         * @since 3.0.0
         */
        protected abstract static class AbstractBean implements PersistableValue {

            protected String note;
            protected int version;
            protected OffsetDateTime registerTime;
            protected String registerAccountId;
            protected String registerProcessName;
            protected OffsetDateTime updateTime;
            protected String updateAccountId;
            protected String updateProcessName;

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
                this.note = builder.note;
                this.version = builder.version;
                this.registerTime = builder.registerTime;
                this.registerAccountId = builder.registerAccountId;
                this.registerProcessName = builder.registerProcessName;
                this.updateTime = builder.updateTime;
                this.updateAccountId = builder.updateAccountId;
                this.updateProcessName = builder.updateProcessName;
            }

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<String> getNote() {
                return Optional.ofNullable(note);
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

            /**
             * {@inheritDoc}
             *
             * @since 3.0.0
             */
            @Override
            public Optional<OffsetDateTime> getRegisterTime() {
                return Optional.ofNullable(registerTime);
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
            public Optional<String> getRegisterAccountId() {
                return Optional.ofNullable(registerAccountId);
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
            public Optional<String> getRegisterProcessName() {
                return Optional.ofNullable(registerProcessName);
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
            public Optional<OffsetDateTime> getUpdateTime() {
                return Optional.ofNullable(updateTime);
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
            public Optional<String> getUpdateAccountId() {
                return Optional.ofNullable(updateAccountId);
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
            public Optional<String> getUpdateProcessName() {
                return Optional.ofNullable(updateProcessName);
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
        }
    }
}
