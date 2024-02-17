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
package trial;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.RecordableValue;

/**
 * Value of importation. Wrapped content and source value. And also represents whether content is explicitly removed.
 *
 * @param <T> content type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImportationValue<T extends ContentValue<T>> implements RecordableValue, Map.Entry<String, ImportationValue<T>> {

    private final boolean explicitDeletion;
    private final T content;
    private final Map<String, String> source;

    /**
     * Constructor.
     *
     * @param content content value
     * @param source source of content
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public ImportationValue(T content, Map<String, String> source) {
        this(false, content, source);
    }

    /**
     * Constructor.
     *
     * @param explicitDeletion {@code true} if explicit deletion, otherwise {@code false}
     * @param content content value
     * @param source source of content
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public ImportationValue(boolean explicitDeletion, T content, Map<String, String> source) {
        this.explicitDeletion = explicitDeletion;
        this.content = Objects.requireNonNull(content);
        this.source = Objects.requireNonNull(source);
    }

    /**
     * Indicate that explicit deletion.
     *
     * @return {@code true} if do force delete, otherwise {@code false}.
     * @since 1.0.0
     */
    public boolean isExplicitDeletion() {
        return explicitDeletion;
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
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
    @JsonbTransient
    @Override
    public String getKey() {
        return getId();
    }

    /**
     * Get this.
     *
     * @return this
     * @since 1.0.0
     */
    @JsonbTransient
    @Override
    public ImportationValue<T> getValue() {
        return this;
    }

    /**
     * Unsupported.
     *
     * @param value no use
     * @return nothing
     * @throws UnsupportedOperationException always
     * @since 1.0.0
     */
    @Override
    public ImportationValue<T> setValue(ImportationValue<T> value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get this as {@code Map.Entry<String, ImportationValue<T>>}.
     *
     * @return this
     * @since 1.0.0
     */
    public Map.Entry<String, ImportationValue<T>> asEntry() {
        return this;
    }

    /**
     * Get source of content.
     *
     * @return source of content
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
}
