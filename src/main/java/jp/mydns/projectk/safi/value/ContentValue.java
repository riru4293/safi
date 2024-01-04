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
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import static java.util.Collections.unmodifiableMap;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.toUnmodifiableMap;
import jp.mydns.projectk.safi.constant.AttName;
import jp.mydns.projectk.safi.validator.Strict;

/**
 * {@code ContentValue} is a data that can be identified by an ID. In addition to the ID, it has an valid period and a
 * digest value. If it is outside the expiration time, the content will be treated as non-existent. Content with the
 * same ID and digest value is considered to be the same content.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ContentValue extends PersistableValue, RecordableValue {

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
    @Schema(description = "Content id.")
    @NotBlank(groups = {Strict.class})
    @Size(max = 36, groups = {Strict.class})
    @Override
    String getId();

    /**
     * Get the valid state.
     *
     * @return {@code true} if enabled, otherwise {@code false}.
     * @since 1.0.0
     */
    @Schema(description = "Valid state. true if enabled, otherwise false.")
    boolean isEnabled();

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    @NotNull(groups = {Strict.class})
    @Valid
    ValidityPeriod getValidityPeriod();

    /**
     * Get content name.
     *
     * @return content name. It may be {@code null}.
     * @since 1.0.0
     */
    @Schema(description = "Content name.")
    @Size(max = 100, groups = {Strict.class})
    String getName();

    /**
     * Get attribute values.
     *
     * @return attribute values
     * @since 1.0.0
     */
    @Schema(description = "Attribute values.")
    @NotNull(groups = {Strict.class})
    Map<AttName, @Size(max = 200, groups = {Strict.class}) String> getAtts();

    /**
     * Get digest value of this. If the contents match exactly, the same digest value can be obtained.
     *
     * @return digest value
     * @since 1.0.0
     */
    @Schema(description = "Content digest value.")
    @NotBlank(groups = {Strict.class})
    @Size(max = 128, groups = {Strict.class})
    String getDigest();

    /**
     * Abstract builder of the {@link ContentValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends ContentValue>
            extends PersistableValue.AbstractBuilder<B, V> {

        protected String id;
        protected boolean enabled;
        protected String name;
        protected Map<AttName, String> atts;
        protected ValidityPeriod validityPeriod;

        protected AbstractBuilder(Class<B> builderType) {
            super(builderType);
        }

        /**
         * Set all properties from {@code src}.
         *
         * @param src source value
         * @return updated this
         * @throws NullPointerException if {@code src} is {@code null}
         * @since 1.0.0
         */
        @Override
        public B with(V src) {

            super.with(Objects.requireNonNull(src));

            this.id = src.getId();
            this.enabled = src.isEnabled();
            this.name = src.getName();
            this.atts = src.getAtts();
            this.validityPeriod = src.getValidityPeriod();

            return builderType.cast(this);

        }

        /**
         * Set content id.
         *
         * @param id content id
         * @return updated this
         * @since 1.0.0
         */
        public B withId(String id) {
            this.id = id;
            return builderType.cast(this);
        }

        /**
         * Set valid state.
         *
         * @param enabled {@code true} if enabled, otherwise {@code false}.
         * @return updated this
         * @since 1.0.0
         */
        public B withEnabled(boolean enabled) {
            this.enabled = enabled;
            return builderType.cast(this);
        }

        /**
         * Set content name.
         *
         * @param name content name
         * @return updated this
         * @since 1.0.0
         */
        public B withName(String name) {
            this.name = name;
            return builderType.cast(this);
        }

        /**
         * Set attribute values.
         *
         * @param atts attribute values
         * @return updated this
         * @since 1.0.0
         */
        public B withAtts(Map<AttName, String> atts) {
            this.atts = atts;
            return builderType.cast(this);
        }

        /**
         * Set the {@code ValidityPeriod}.
         *
         * @param validityPeriod the {@code ValidityPeriod}
         * @return updated this
         * @since 1.0.0
         */
        public B withValidityPeriod(ValidityPeriod validityPeriod) {
            this.validityPeriod = validityPeriod;
            return builderType.cast(this);
        }

        protected abstract static class AbstractBean extends PersistableValue.AbstractBuilder.AbstractBean
                implements ContentValue {

            protected String id;
            protected boolean enabled;
            protected String name;
            @JsonbTransient // Note: When serializing an enum to JSON, I want to avoid that value becoming an enum name.
            protected Map<AttName, String> atts;
            protected ValidityPeriod validityPeriod;
            protected String digest;

            protected AbstractBean() {
            }

            protected AbstractBean(ContentValue.AbstractBuilder<?, ?> builder, String digest) {

                super(builder);

                this.id = builder.id;
                this.enabled = builder.enabled;
                this.name = builder.name;
                this.atts = builder.atts;
                this.validityPeriod = builder.validityPeriod;
                this.digest = digest;

            }

            @Override
            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            @Override
            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            @Override
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @Override
            public Map<AttName, String> getAtts() {
                return atts != null ? unmodifiableMap(atts) : null;
            }

            public Map<String, String> getAttributes() {
                return atts != null
                        ? atts.entrySet().stream().collect(toUnmodifiableMap(
                                e -> e.getKey().toString(), Map.Entry::getValue))
                        : null;
            }

            public void setAttributes(Map<String, String> atts) {
                this.atts = atts != null
                        ? atts.entrySet().stream().collect(toUnmodifiableMap(
                                e -> AttName.of(e.getKey()), Map.Entry::getValue))
                        : null;
            }

            @Override
            public ValidityPeriod getValidityPeriod() {
                return validityPeriod;
            }

            public void setValidityPeriod(ValidityPeriod validityPeriod) {
                this.validityPeriod = validityPeriod;
            }

            @Override
            public String getDigest() {
                return digest;
            }

            public void setDigest(String digest) {
                this.digest = digest;
            }
        }
    }
}
