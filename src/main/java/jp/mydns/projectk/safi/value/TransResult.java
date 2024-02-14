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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import trial.TransformerService;

/**
 * A result of the transform processing. If the transformation succeeded, contains a transformed content and a source
 * content. If the transformation failed, contains an error message and a source content.
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
 * @see TransformerService.Transformer#transform(java.util.Map) Transform processing
 */
public interface TransResult extends RecordableValue {

    /**
     * Indicates that transformation was succeeded.
     *
     * @return {@code true} in this case, otherwise {@code false}.
     * @since 1.0.0
     */
    boolean isSuccessful();

    /**
     * Returns a {@code null}.
     *
     * @return transformed content id. It may be {@code null}.
     * @since 1.0.0
     */
    @Override
    default String getId() {
        return null;
    }

    /**
     * Get transformation failure reason.
     *
     * @return failure reason
     * @throws UnsupportedOperationException if {@link #isSuccessful()} is {@code true}
     * @since 1.0.0
     */
    @JsonbTransient
    String getReason();

    /**
     * Returns a transformed result content.
     *
     * @return transformed content
     * @throws UnsupportedOperationException if {@link #isSuccessful()} is {@code false}
     * @since 1.0.0
     */
    Map<String, String> getContent();

    /**
     * Returns a transformation source content.
     *
     * @return transformation source content
     * @since 1.0.0
     */
    Map<String, String> getSource();

    /**
     * Transform result of success.
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
    public static class Success implements TransResult {

        private final Map<String, String> content;
        private final Map<String, String> source;

        /**
         * Constructor.
         *
         * @param content transformed content
         * @param source transformation source content
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        public Success(Map<String, String> content, Map<String, String> source) {
            this.content = new LinkedHashMap<>(Objects.requireNonNull(content));
            this.source = new LinkedHashMap<>(Objects.requireNonNull(source));
        }

        /**
         * {@inheritDoc}
         *
         * @return {@code true}
         * @since 1.0.0
         */
        @Override
        public boolean isSuccessful() {
            return true;
        }

        /**
         * Unsupported operation.
         *
         * @return nothing
         * @throws UnsupportedOperationException always
         * @since 1.0.0
         */
        @Override
        public String getReason() {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Map<String, String> getContent() {
            return Collections.unmodifiableMap(content);
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Map<String, String> getSource() {
            return Collections.unmodifiableMap(source);
        }

        /**
         * Returns a string representation.
         *
         * @return a string representation
         * @since 1.0.0
         */
        @Override
        public String toString() {
            return "TransResult{" + "success=true" + ", content=" + content + ", source=" + source + '}';
        }
    }

    /**
     * Transform result of failure.
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
    public static class Failure implements TransResult {

        private final String failureReason;
        private final Map<String, String> source;

        /**
         * Constructor.
         *
         * @param reason failure reasons
         * @param source transformation source content
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        public Failure(String reason, Map<String, String> source) {
            this.failureReason = Objects.requireNonNull(reason);
            this.source = new LinkedHashMap<>(Objects.requireNonNull(source));
        }

        /**
         * {@inheritDoc}
         *
         * @return {@code false}
         * @since 1.0.0
         */
        @Override
        public boolean isSuccessful() {
            return false;
        }

        /**
         * {@inheritDoc}
         *
         * @return failure reason
         * @since 1.0.0
         */
        @Override
        public String getReason() {
            return failureReason;
        }

        /**
         * Unsupported operation.
         *
         * @return nothing
         * @throws UnsupportedOperationException always
         * @since 1.0.0
         */
        @Override
        @JsonbTransient
        public Map<String, String> getContent() {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Map<String, String> getSource() {
            return Collections.unmodifiableMap(source);
        }

        /**
         * Returns a string representation.
         *
         * @return a string representation
         * @since 1.0.0
         */
        @Override
        public String toString() {
            return "TransResult{" + "success=false" + ", reason=" + failureReason + ", source=" + source + '}';
        }
    }
}
