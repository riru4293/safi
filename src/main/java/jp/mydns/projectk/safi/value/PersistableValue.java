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
package jp.mydns.projectk.safi.value;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Objects;
import jp.mydns.projectk.safi.validator.Strict;

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
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PersistableValue {

    /**
     * Get note for this value.
     *
     * @return note for this value. It may be {@code null}.
     * @since 1.0.0
     */
    @Schema(description = "Note for this value.")
    String getNote();

    /**
     * Get version number for this value. Used for optimistic locking when updating this value. Incremented after
     * update.
     *
     * @return version number. 0 means new value.
     * @since 1.0.0
     */
    @Schema(description = "Version number for this value.")
    @PositiveOrZero(groups = {Strict.class})
    int getVersion();

    /**
     * Abstract builder of the {@link Persistable}.
     *
     * @param <B> Builder type
     * @param <V> Value type
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends PersistableValue> {

        protected final Class<B> builderType;
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
         * @since 1.0.0
         */
        public B with(V src) {

            Objects.requireNonNull(src);

            this.note = src.getNote();
            this.version = src.getVersion();

            return builderType.cast(this);

        }

        /**
         * Set note for this value.
         *
         * @param note note for this value. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
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
         * @since 1.0.0
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
         * @since 1.0.0
         */
        public abstract V build(Validator validator, Class<?>... groups);

        protected abstract static class AbstractBean implements PersistableValue {

            protected String note;
            protected int version;

            protected AbstractBean() {
            }

            protected AbstractBean(AbstractBuilder<?, ?> builder) {
                this.note = builder.note;
                this.version = builder.version;
            }

            @Override
            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            @Override
            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }
        }
    }
}
