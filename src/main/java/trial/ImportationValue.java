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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.RecordableValue;

/**
 * Value of importation.
 *
 * @param <T> content type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImportationValue<T extends ContentValue<T>> implements RecordableValue, Map.Entry<String, ImportationValue<T>> {

    private final T content;
    private final Map<String, String> source;
    private final ContentEntity entity;

    /**
     * Constructor.
     *
     * @param content content value
     * @param source source of content
     * @throws NullPointerException if any argument is {@code null}
     */
    public ImportationValue(T content, Map<String, String> source) {
        this(content, source, null);
    }

    /**
     * Constructor.
     *
     * @param content content value
     * @param source source of content
     * @param entity managed original entity. It can be set {@code null}.
     * @throws NullPointerException if {@code content} or {@code source} is {@code null}
     */
    public ImportationValue(T content, Map<String, String> source, ContentEntity entity) {
        this.content = Objects.requireNonNull(content);
        this.source = Objects.requireNonNull(source);
        this.entity = entity;
    }

    /**
     * Explicitly indicates that do delete the content.
     *
     * @return {@code true} if do force delete, otherwise {@code false}.
     */
    public boolean doDelete() {
        return Boolean.parseBoolean(source.get("doDelete"));
    }

    /**
     * Get content id.
     *
     * @return content id
     * @since 1.0.0
     */
    @Override
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
     * Get content value.
     *
     * @return content value.
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
     */
    public T getContent() {
        return content;
    }

    /**
     * Get source of content.
     *
     * @return source of content
     */
    public Map<String, String> getSource() {
        return Collections.unmodifiableMap(source);
    }

    /**
     * Get the managed original entity.
     *
     * @return managed original entity
     */
    @JsonbTransient
    public Optional<ContentEntity> getEntity() {
        return Optional.ofNullable(entity);
    }
}
