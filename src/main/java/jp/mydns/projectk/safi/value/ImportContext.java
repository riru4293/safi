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

import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jp.mydns.projectk.safi.service.ImporterService.Importer;
import jp.mydns.projectk.safi.service.TransformerService.Transformer;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 * Provides information used in the content import process.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportContext {

    /**
     * Get the {@code Importer}.
     *
     * @return the {@code Importer}
     * @since 1.0.0
     */
    @NotNull
    Importer getImporter();

    /**
     * Get the {@code Transformer}.
     *
     *
     * @return the {@code Transformer}
     * @since 1.0.0
     */
    @NotNull
    Transformer getTransformer();

    /**
     * Indicates permission for implicit deletion of lost content.
     *
     * @return {@code true} if allow implicit deletion on import. Default is {@code false}.
     * @since 1.0.0
     */
    boolean isAllowedImplicitDeletion();

    /**
     * Provides an additional filtering condition for content to be implicitly deleted on import.
     *
     * @return additional filtering condition. Default is no condition.
     * @since 1.0.0
     */
    Condition getAdditionalConditionForExtractingImplicitDeletion();

    /**
     * Provides a limit number of implicit deletions. If the number of implicit deletes exceeds this limit, no implicit
     * deletes will occur.
     *
     * @return limit number. Default is {@value Long#MAX_VALUE}.
     * @since 1.0.0
     */
    long getLimitNumberOfImplicitDeletion();

    /**
     * Builder of the {@link ImportContext}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private Importer importer;
        private Transformer transformer;
        private JobOptions jobOptions;

        /**
         * Set the {@code Importer}.
         *
         * @param importer the {@code Importer}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withImporter(Importer importer) {
            this.importer = importer;
            return this;
        }

        /**
         * Set the {@code Transformer}.
         *
         * @param transformer the {@code Transformer}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withTransformer(Transformer transformer) {
            this.transformer = transformer;
            return this;
        }

        /**
         * Set the {@code JobOptions}.
         *
         * @param jobOptions the {@code JobOptions}
         * @return updated this
         * @since 1.0.0
         */
        public Builder withJobOptions(JobOptions jobOptions) {
            this.jobOptions = jobOptions;
            return this;
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
        public ImportContext build(Validator validator, Class<?>... groups) {
            return ValidationUtils.requireValid(new ImportContextImpl(this), validator, groups);
        }

        /**
         * Implements of the {@code ImportContext}.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        private class ImportContextImpl implements ImportContext {

            private final Importer importer;
            private final Transformer transformer;
            private final JobOptions jobOptions;
            private final JsonObject options;

            /**
             * Constructor.
             *
             * @param builder the {@code ImportContext.Builder}
             * @since 1.0.0
             */
            private ImportContextImpl(Builder builder) {
                this.importer = builder.importer;
                this.transformer = builder.transformer;
                this.jobOptions = builder.jobOptions;
            }

            /**
             * Get the {@code JobOption}.
             *
             * @return the {@code JobOption}
             * @since 1.0.0
             */
            @NotNull
            public JobOptions getJobOptions() {
                return jobOptions;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Importer getImporter() {
                return importer;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Transformer getTransformer() {
                return transformer;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean isAllowedImplicitDeletion() {
                return options.getBoolean("allowImplicitDeletion", false);
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Condition getAdditionalConditionForExtractingImplicitDeletion() {
                try {
                    return jsonSvc.convertViaJson(options.get("conditionOfImplicitDeletion"), Condition.class);
                } catch (RuntimeException ignore) {
                    return Condition.emptyCondition();
                }
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public long getLimitNumberOfImplicitDeletion() {
                try {
                    return JsonNumber.class.cast(options.get("limitOfDeletion")).longValueExact();
                } catch (RuntimeException ignore) {
                    return Long.MAX_VALUE;  // Note: Means unlimited.
                }
            }
        }
    }
}
