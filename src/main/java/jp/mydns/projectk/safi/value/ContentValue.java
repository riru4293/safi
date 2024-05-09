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
import java.util.Collections;
import static java.util.Collections.unmodifiableMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;
import jp.mydns.projectk.safi.constant.AttKey;
import jp.mydns.projectk.safi.service.AppTimeService;
import static jp.mydns.projectk.safi.util.LambdaUtils.alwaysThrow;
import static jp.mydns.projectk.safi.util.LambdaUtils.f;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;

/**
 * Data with id, called <i>ID-Content</i>. The {@code ContentValue} is data that can be identified by id and is the main
 * content of this application. In addition to an ID, <i>ID-Content</i> has a {@link ValidityPeriod}(expiration time), a
 * name, some <i>Attribute</i> values, and a digest value. If it is outside the expiration time, the content will be
 * treated as non-existent. Content with the same ID and digest value is considered to be the same content. The digest
 * value is calculated from main elements of <i>ID-Content</i>.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * @param <V> content type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ContentValue<V extends ContentValue<V>> extends RecordableValue {

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
    @Schema(description = "Valid state. It is true if enabled, otherwise false.")
    boolean isEnabled();

    /**
     * Get content name.
     *
     * @return content name. It may be {@code null}.
     * @since 1.0.0
     */
    @Schema(description = "Content name.")
    @Size(max = 255, groups = {Default.class})
    String getName();

    /**
     * Get digest value of content. If the contents match exactly, the same digest value can be obtained.
     *
     * @return digest value of content.
     * @since 1.0.0
     * @see DigestGenerator
     */
    @Schema(description = "Content digest value.")
    @NotBlank(groups = {Default.class})
    @Size(max = 128, groups = {Default.class})
    String getDigest();

    /**
     * Get the <i>Attribute</i> values. <i>Attribute</i> is element of <i>ID-Content</i> and is data item that can take
     * arbitrary value. The meaning of the value is determined by the user, not by the application.
     *
     * @return <i>Attribute</i> values
     * @since 1.0.0
     */
    @Schema(description = "Attribute values.")
    @NotNull(groups = {Default.class})
    Map<AttKey, @Size(max = 255, groups = {Default.class}) String> getAtts();

    /**
     * Get the {@code ValidityPeriod}.
     *
     * @return the {@code ValidityPeriod}
     * @since 1.0.0
     */
    @Valid
    @NotNull(groups = {Default.class})
    ValidityPeriod getValidityPeriod();

    /**
     * Get the {@code PersistenceContext}.
     *
     * @return persistence information
     * @since 1.0.0
     */
    @Schema(description = "Persistence information.")
    Optional<@Valid PersistenceContext> getPersistenceContext();

    /**
     * Get note.
     *
     * @return note. It may be {@code null}.
     * @since 1.0.0
     */
    @Schema(description = "Note.")
    String getNote();

    /**
     * Get a {@code Map.Entry} whose key is {@link #getId()} and whose value is this instance.
     *
     * @return the {@code Map.Entry}
     * @since 1.0.0
     */
    Map.Entry<String, ContentValue<V>> asEntry();

    /**
     * Abstract builder of the {@link ContentValue}.
     *
     * @param <B> builder type
     * @param <V> type of value to build
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends ContentValue<V>> {

        /**
         * Type of builder implementation.
         *
         * @since 1.0.0
         */
        protected final Class<B> builderType;

        /**
         * Content id.
         *
         * @since 1.0.0
         */
        protected String id;

        /**
         * Valid state. It is {@code true} if enabled, otherwise {@code false}.
         *
         * @since 1.0.0
         */
        protected boolean enabled;

        /**
         * Content name. {@code null} allowable.
         *
         * @since 1.0.0
         */
        protected String name;

        /**
         * <i>Attribute</i> values. {@code null} allowable in entry value.
         *
         * @since 1.0.0
         */
        protected Map<AttKey, String> atts;

        /**
         * The {@code ValidityPeriod}.
         *
         * @since 1.0.0
         */
        protected ValidityPeriod validityPeriod;

        /**
         * The {@code PersistenceContext}.
         *
         * @since 1.0.0
         */
        protected PersistenceContext persistenceContext;

        /**
         * Note.
         *
         * @since 1.0.0
         */
        protected String note;

        /**
         * Constructs a new builder with all properties are {@code null}.
         *
         * @param builderType builder type
         * @throws NullPointerException if {@code builderType} is {@code null}
         * @since 1.0.0
         */
        protected AbstractBuilder(Class<B> builderType) {
            this.builderType = Objects.requireNonNull(builderType);
        }

        /**
         * Constructs a new builder with set all properties by copying them from other value.
         *
         * @param builderType builder type
         * @param src source value
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        protected AbstractBuilder(Class<B> builderType, V src) {
            this(builderType);

            Objects.requireNonNull(src);

            this.id = src.getId();
            this.enabled = src.isEnabled();
            this.name = src.getName();
            this.atts = src.getAtts();
            this.validityPeriod = src.getValidityPeriod();
            src.getPersistenceContext().ifPresent(v -> this.persistenceContext = v);
            this.note = src.getNote();
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
         * @param name content name. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public B withName(String name) {
            this.name = name;
            return builderType.cast(this);
        }

        /**
         * Set the <i>Attribute</i> values.
         *
         * @param atts the <i>Attribute</i> values
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
         * Set the {@code PersistenceContext}.
         *
         * @param persistenceContext the {@code PersistenceContext}. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public B withPersistenceContext(PersistenceContext persistenceContext) {
            this.persistenceContext = persistenceContext;
            return builderType.cast(this);
        }

        /**
         * Set note.
         *
         * @param note note. It can be set {@code null}.
         * @return updated this
         * @since 1.0.0
         */
        public B withNote(String note) {
            this.note = note;
            return builderType.cast(this);
        }

        /**
         * Abstract implements of the {@code ContentValue} as Java Beans.
         *
         * @param <V> content type
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected abstract static class AbstractBean<V extends ContentValue<V>> implements ContentValue<V> {

            /**
             * Content id.
             *
             * @since 1.0.0
             */
            protected String id;

            /**
             * Valid state. It is {@code true} if enabled, otherwise {@code false}.
             *
             * @since 1.0.0
             */
            protected boolean enabled;

            /**
             * Content name. {@code null} allowable.
             *
             * @since 1.0.0
             */
            protected String name;

            /**
             * Digest value of content.
             *
             * @since 1.0.0
             */
            protected String digest;

            /**
             * <i>Attribute</i> values. {@code null} allowable in entry value.\
             * <p>
             * This property is excluded from JSONization to avoid having its value become the enumeration name when
             * serializing the enumeration to JSON.
             *
             * @see #getAttributes() getter for serialization
             * @see #setAttributes(java.util.Map) setter for deserialization
             * @since 1.0.0
             */
            @JsonbTransient
            protected Map<AttKey, String> atts;

            /**
             * The {@code ValidityPeriod}.
             *
             * @since 1.0.0
             */
            protected ValidityPeriod validityPeriod;

            /**
             * The {@code PersistenceContext}.
             *
             * @since 1.0.0
             */
            protected PersistenceContext persistenceContext;

            /**
             * Note.
             *
             * @since 1.0.0
             */
            protected String note;

            /**
             * Constructor just for JSON deserialization.
             *
             * @since 1.0.0
             */
            protected AbstractBean() {
            }

            /**
             * Construct with set all properties from builder and digest value.
             *
             * @param builder the {@code ContentValue.AbstractBuilder}
             * @param digest digest value
             * @throws NullPointerException if any argument is {@code null}
             * @see DigestGenerator
             * @since 1.0.0
             */
            protected AbstractBean(ContentValue.AbstractBuilder<?, ?> builder, String digest) {
                Objects.requireNonNull(builder);
                Objects.requireNonNull(digest);

                this.id = builder.id;
                this.enabled = builder.enabled;
                this.name = builder.name;
                this.digest = digest;
                this.atts = builder.atts;
                this.validityPeriod = builder.validityPeriod;
                this.persistenceContext = builder.persistenceContext;
                this.note = builder.note;
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
             * Get the <i>Attribute</i> values for serialization to JSON.
             *
             * @return <i>Attribute</i> values
             * @since 1.0.0
             */
            public Map<String, String> getAttributes() {
                return atts != null
                        ? atts.entrySet().stream().filter(p(Objects::nonNull, Map.Entry::getValue))
                                .collect(toUnmodifiableMap(f(AttKey::toString).compose(Map.Entry::getKey), Map.Entry::getValue))
                        : null;
            }

            /**
             * Set the <i>Attribute</i> values for deserialization from JSON.
             *
             * @param atts <i>Attribute</i> values
             * @since 1.0.0
             */
            public void setAttributes(Map<String, String> atts) {
                this.atts = atts != null
                        ? atts.entrySet().stream().filter(p(Objects::nonNull, Map.Entry::getValue))
                                .collect(collectingAndThen(toMap(f(AttKey::of).compose(Map.Entry::getKey), Map.Entry::getValue,
                                        alwaysThrow(), () -> new EnumMap<>(AttKey.class)), Collections::unmodifiableMap))
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
            public Optional<PersistenceContext> getPersistenceContext() {
                return Optional.ofNullable(persistenceContext);
            }

            /**
             * Set the {@code PersistenceContext}.
             *
             * @param persistenceContext the {@code PersistenceContext}. It can be set {@code null}.
             * @since 1.0.0
             */
            public void setPersistenceContext(PersistenceContext persistenceContext) {
                this.persistenceContext = persistenceContext;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getNote() {
                return note;
            }

            /**
             * Set note.
             *
             * @param note note. It can be set {@code null}.
             * @since 1.0.0
             */
            public void setNote(String note) {
                this.note = note;
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
         * @param elements main elements of <i>ID-Content</i>
         * @return digest value
         * @throws NullPointerException when {@code elements} is {@code null}
         * @throws IllegalArgumentException if contains an unexpected element in {@code elements}
         * @since 1.0.0
         */
        String generate(Object... elements);
    }
}
