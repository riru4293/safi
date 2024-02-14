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
import jakarta.validation.groups.Default;
import static java.util.Collections.unmodifiableMap;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.toUnmodifiableMap;
import jp.mydns.projectk.safi.constant.AttKey;
import jp.mydns.projectk.safi.service.AppTimeService;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;

/**
 * {@code ContentValue} is data that can be identified by ID and is the main content of this application, called
 * <i>ID-Content</i>. In addition to an ID, an <i>ID-Content</i> has a {@link ValidityPeriod}(expiration time), a name,
 * a collection of
 * <i>Attribute</i>, and a digest value. If it is outside the expiration time, the content will be treated as
 * non-existent. Content with the same ID and digest value is considered to be the same content. The digest value is
 * calculated from all <i>ID-Content</i> elements except itself.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * @param <T> content type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ContentValue<T extends ContentValue<T>> extends PersistableValue, RecordableValue, Map.Entry<String, T> {

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
    @Schema(description = "Content id.")
    @NotBlank(groups = {Default.class})
    @Size(max = 36, groups = {Default.class})
    @Override
    String getId();

    /**
     * Get the valid state. It is the flag calculated from {@link #getValidityPeriod()} and
     * {@link AppTimeService#getLocalNow()}, and is the value at the time of calculation.
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
    @NotNull(groups = {Default.class})
    @Valid
    ValidityPeriod getValidityPeriod();

    /**
     * Get content name.
     *
     * @return content name. It may be {@code null}.
     * @since 1.0.0
     */
    @Schema(description = "Content name.")
    @Size(max = 100, groups = {Default.class})
    String getName();

    /**
     * Get the <i>Attribute</i> collection. <i>Attribute</i> is element of <i>ID-Content</i> and is data item that can
     * take arbitrary value. The meaning of the value is determined by the user, not by the application.
     *
     * @return <i>Attribute</i> collection
     * @since 1.0.0
     */
    @Schema(description = "Attribute values.")
    @NotNull(groups = {Default.class})
    Map<AttKey, @Size(max = 200, groups = {Default.class}) String> getAtts();

    /**
     * Get digest value of this. If the contents match exactly, the same digest value can be obtained.
     *
     * @return digest value
     * @since 1.0.0
     * @see DigestGenerator
     */
    @Schema(description = "Content digest value.")
    @NotBlank(groups = {Default.class})
    @Size(max = 128, groups = {Default.class})
    String getDigest();

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
    @JsonbTransient
    @Override
    default String getKey() {
        return getId();
    }

    /**
     * Get the this instance.
     *
     * @return the this instance
     * @since 1.0.0
     */
    @JsonbTransient
    @Override
    T getValue();

    /**
     * Unsupported.
     *
     * @param unused unused-value
     * @return none
     * @throws UnsupportedOperationException always
     * @since 1.0.0
     */
    @JsonbTransient
    @Override
    T setValue(T unused);

    /**
     * Abstract builder of the {@link ContentValue}.
     *
     * @param <B> builder type
     * @param <V> value type
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends ContentValue<V>>
            extends PersistableValue.AbstractBuilder<B, V> {

        protected String id;
        protected boolean enabled;
        protected String name;
        protected Map<AttKey, String> atts;
        protected ValidityPeriod validityPeriod;

        /**
         * Constructor.
         *
         * @param builderType the class to build by this builder
         * @since 1.0.0
         */
        protected AbstractBuilder(Class<B> builderType) {
            super(builderType);
        }

        /**
         * Set all properties from {@code src} except the digest value.
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
         * Set the <i>Attribute</i> collection.
         *
         * @param atts the <i>Attribute</i> collection
         * @return updated this
         * @since 1.0.0
         */
        public B withAtts(Map<AttKey, String> atts) {
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

        /**
         * Abstract implements of the {@code ContentValue}.
         *
         * @param <T> content type
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected abstract static class AbstractBean<T extends ContentValue<T>>
                extends PersistableValue.AbstractBuilder.AbstractBean implements ContentValue<T> {

            protected String id;
            protected boolean enabled;
            protected String name;
            @JsonbTransient // Note: When serializing an enum to JSON, I want to avoid that value becoming an enum name.
            protected Map<AttKey, String> atts;
            protected ValidityPeriod validityPeriod;
            protected String digest;

            /**
             * Constructor. Used only for deserialization from JSON.
             *
             * @since 1.0.0
             */
            protected AbstractBean() {
            }

            /**
             * Constructor.
             *
             * @param builder the {@code ContentValue.AbstractBuilder}
             * @param digest digest value. It must be provided by {@code builder}.
             * @since 1.0.0
             */
            protected AbstractBean(ContentValue.AbstractBuilder<?, ?> builder, String digest) {
                super(builder);

                this.id = builder.id;
                this.enabled = builder.enabled;
                this.name = builder.name;
                this.atts = builder.atts;
                this.validityPeriod = builder.validityPeriod;
                this.digest = digest;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getId() {
                return id;
            }

            /**
             * Set content id.
             *
             * @param id content id
             * @since 1.0.0
             */
            public void setId(String id) {
                this.id = id;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean isEnabled() {
                return enabled;
            }

            /**
             * Set the valid state.
             *
             * @param enabled valid state
             * @since 1.0.0
             */
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getName() {
                return name;
            }

            /**
             * Set content name.
             *
             * @param name content name
             * @since 1.0.0
             */
            public void setName(String name) {
                this.name = name;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Map<AttKey, String> getAtts() {
                return atts != null ? unmodifiableMap(atts) : null;
            }

            /**
             * Get the <i>Attribute</i> collection for serialization to JSON.
             *
             * @return <i>Attribute</i> collection for serialization to JSON
             * @since 1.0.0
             */
            public Map<String, String> getAttributes() {
                return atts != null
                        ? atts.entrySet().stream().filter(p(Objects::nonNull, Map.Entry::getValue))
                                .collect(toUnmodifiableMap(e -> e.getKey().toString(), Map.Entry::getValue))
                        : null;
            }

            /**
             * Set the <i>Attribute</i> collection for serialization to JSON.
             *
             * @param atts <i>Attribute</i> collection for serialization to JSON
             * @since 1.0.0
             */
            public void setAttributes(Map<String, String> atts) {
                this.atts = atts != null
                        ? atts.entrySet().stream().filter(p(Objects::nonNull, Map.Entry::getValue))
                                .collect(toUnmodifiableMap(e -> AttKey.of(e.getKey()), Map.Entry::getValue))
                        : null;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public ValidityPeriod getValidityPeriod() {
                return validityPeriod;
            }

            /**
             * Set the {@code ValidityPeriod}.
             *
             * @param validityPeriod the {@code ValidityPeriod}
             * @since 1.0.0
             */
            public void setValidityPeriod(ValidityPeriod validityPeriod) {
                this.validityPeriod = validityPeriod;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getDigest() {
                return digest;
            }

            /**
             * Set digest value.
             *
             * @param digest digest value
             * @since 1.0.0
             */
            public void setDigest(String digest) {
                this.digest = digest;
            }
        }
    }

    /**
     * Digest value generator for <i>ID-Content</i>.
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
    interface DigestGenerator {

        /**
         * Generate a digest value.
         *
         * @param sources source values. They are elements of <i>ID-Content</i> excluding the digest value.
         * @return digest value
         * @throws NullPointerException when {@code sources} is {@code null}
         * @throws IllegalArgumentException if contains an unexpected element in {@code sources}
         * @since 1.0.0
         */
        String generate(Object... sources);
    }
}
