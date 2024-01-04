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
package jp.mydns.projectk.safi.dao.criteria;

import jakarta.persistence.criteria.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Path information of the JPA criteria query.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CriteriaPathContext {

    /**
     * Returns a {@code Supplier} that throw {@code IllegalArgumentException}. Using when specified undefined element
     * name.
     *
     * @param name element name
     * @return {@code Supplier} that throw {@code IllegalArgumentException}
     * @since 1.0.0
     */
    static Supplier<IllegalArgumentException> undefinedPath(String name) {
        return () -> new IllegalArgumentException("Undefined element name: " + name);
    }

    /**
     * Resolve the element path from element name.
     *
     * @param name element name
     * @return element path
     * @since 1.0.0
     */
    Path<String> of(String name);

    /**
     * Abstract implements of the {@code CriteriaPathContext}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract static class AbstractCriteriaPathContext implements CriteriaPathContext {

        private final Map<String, Path<String>> mapping;

        /**
         * Constructor.
         *
         * @param mapping path name and path mapping
         * @throws NullPointerException if {@code mapping} is {@code null}
         * @since 1.0.0
         */
        protected AbstractCriteriaPathContext(Map<String, Path<String>> mapping) {
            this.mapping = Objects.requireNonNull(mapping);
        }

        /**
         * Resolve the element path from element name.
         *
         * @param name element name
         * @return element path
         * @throws IllegalArgumentException if {@code name} is undefined
         * @since 1.0.0
         */
        @Override
        public Path<String> of(String name) {
            return Optional.ofNullable(name).map(mapping::get).orElseThrow(undefinedPath(name));
        }
    }
}
