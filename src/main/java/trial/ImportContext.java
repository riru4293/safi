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

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import jp.mydns.projectk.safi.service.ImporterService.Importer;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.JobOptions;

/**
 * Information for import processing.
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
     * Get content transform definition.
     *
     *
     * @return transform definition
     * @since 1.0.0
     */
    @NotNull
    Map<String, String> getTrnsdef();

    /**
     * Return {@code true} to allow implicit deletion on import.
     *
     * @return {@code true} if allow implicit deletion on import. Default is {@code false}.
     * @since 1.0.0
     */
    boolean allowImplicitDeletion();

    /**
     * Provides a filtering condition for content to be implicitly deleted on import.
     *
     * @return filtering condition for content to be implicitly deleted on import. Default is no condition.
     * @since 1.0.0
     */
    Condition conditionOfImplicitDeletion();

    /**
     * Return {@code true} if processing includes add content.
     *
     * @return {@code true} if processing includes add content. Default is {@code true}.
     * @since 1.0.0
     */
    boolean containAddition();

    /**
     * Return {@code true} if processing includes delete content.
     *
     * @return {@code true} if processing includes delete content. Default is {@code false}.
     * @since 1.0.0
     */
    boolean containDeletion();

    /**
     * Return {@code true} if processing includes unchanged content.
     *
     * @return {@code true} if processing includes unchanged content. Default is {@code false}.
     * @since 1.0.0
     */
    boolean containUnchanging();

    /**
     * Return {@code true} if processing includes update content.
     *
     * @return {@code true} if processing includes update content. Default is {@code true}.
     * @since 1.0.0
     */
    boolean containUpdate();

    /**
     * Provides a limit number of deletions.
     *
     * @return limit number of deletions. Default is {@value Long#MAX_VALUE}.
     * @since 1.0.0
     */
    long limitOfDeletion();

    /**
     * Builder of the {@link ImportContext}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Builder {

        private Importer importer;
        private Map<String, String> trnsdef;
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
         * Set the transform definition.
         *
         * @param trnsdef the transform definition
         * @return updated this
         * @since 1.0.0
         */
        public Builder withTrnsdef(Map<String, String> trnsdef) {
            this.trnsdef = trnsdef;
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
        protected static class ImportContextImpl implements ImportContext {

            private final Importer importer;
            private final Map<String, String> trnsdef;
            private final JobOptions jobOptions;

            /**
             * Constructor.
             *
             * @param builder the {@code ImportContext.Builder}
             * @since 1.0.0
             */
            protected ImportContextImpl(Builder builder) {
                this.importer = builder.importer;
                this.trnsdef = builder.trnsdef;
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
            public Map<String, String> getTrnsdef() {
                return trnsdef;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean allowImplicitDeletion() {
                return jobOptions.allowImplicitDeletion();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public Condition conditionOfImplicitDeletion() {
                return jobOptions.conditionOfImplicitDeletion();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean containAddition() {
                return jobOptions.containAddition();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean containDeletion() {
                return jobOptions.containDeletion();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean containUnchanging() {
                return jobOptions.containUnchanging();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public boolean containUpdate() {
                return jobOptions.containUpdate();
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public long limitOfDeletion() {
                return jobOptions.limitOfDeletion();
            }
        }
    }
}
