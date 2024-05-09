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

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import jp.mydns.projectk.safi.constant.RecordValueFormat;
import static jp.mydns.projectk.safi.constant.RecordValueFormat.BELONG_GROUP;
import static jp.mydns.projectk.safi.constant.RecordValueFormat.BELONG_ORG;

/**
 * Value of importation. Wrapped content and source value. And also represents whether content is explicitly removed.
 *
 * @param <T> content type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImportationValue<T extends ContentValue<T>> implements RecordableValue {

    private final T content;
    private final Map<String, String> source;

    /**
     * Construct with content and content source.
     *
     * @param content content value
     * @param source source of content
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public ImportationValue(T content, Map<String, String> source) {
        this.content = Objects.requireNonNull(content);
        this.source = Objects.requireNonNull(source);
    }

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
    @Override
    @NotBlank
    @JsonbTransient
    public String getId() {
        return content.getId();
    }

    /**
     * Get a {@code Map.Entry} whose key is {@link #getId()} and whose value is this instance.
     *
     * @return the {@code Map.Entry}
     * @since 1.0.0
     */
    public Map.Entry<String, ImportationValue<T>> asEntry() {
        return new AbstractMap.SimpleImmutableEntry<>(getId(), this);
    }

    /**
     * Get content.
     *
     * @return content
     * @since 1.0.0
     */
    @Valid
    public T getContent() {
        return content;
    }

    /**
     * Get source of content.
     *
     * @return source of content
     * @since 1.0.0
     */
    public Map<String, String> getSource() {
        return Collections.unmodifiableMap(source);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public RecordValueFormat getFormat() {
        return switch (getContent().getFormat()) {
            case USER ->
                RecordValueFormat.IMPORTATION_USER;
            case MEDIUM ->
                RecordValueFormat.IMPORTATION_MEDIUM;
            case ORG ->
                RecordValueFormat.IMPORTATION_ORG;
            case BELONG_ORG ->
                RecordValueFormat.IMPORTATION_BELONG_ORG;
            case GROUP ->
                RecordValueFormat.IMPORTATION_GROUP;
            case BELONG_GROUP ->
                RecordValueFormat.IMPORTATION_BELONG_GROUP;
            default ->
                throw new IllegalStateException("Unexpected content format.");
        };
    }
}
