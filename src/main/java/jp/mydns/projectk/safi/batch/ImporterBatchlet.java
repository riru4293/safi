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
package jp.mydns.projectk.safi.batch;

import jakarta.batch.runtime.BatchStatus;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Objects;
import jp.mydns.projectk.safi.producer.RequestContextProducer;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.service.ImporterService;
import jp.mydns.projectk.safi.service.ImporterService.Importer;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.TransformerService;
import jp.mydns.projectk.safi.service.TransformerService.Transformer;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.ImportContext;
import trial.ImportationFacade.UserImportationFacade;
import trial.ImportationRecordMap;
import trial.JobRecordingService;

@Named
@Dependent
public class ImporterBatchlet extends JobBatchlet {

    @Inject
    private RequestContextProducer reqCtxPrd;

    @Inject
    private JobRecordingService recSvc;

    @Inject
    private ImporterService importerSvc;

    @Inject
    private UserImportationFacade userImportFacade;

    @Inject
    private Validator validator;

    @Inject
    private ConfigService confSvc;

    @Inject
    private Jsonb jsonb;

    @Inject
    private JsonService jsonSvc;

    @Inject
    private TransformerService transSvc;

    @Override
    public String mainProcess() throws InterruptedException, IOException {
        reqCtxPrd.setup("Importer");

        Importer importer = importerSvc.buildImporter(getWrkDir(), getPlugdef(), getJobOptions());
        Transformer transformer = transSvc.buildTransformer(getTrnsdef());

        ImportContext importCtx = new ImportContextImpl(importer, transformer, getJobOptions());

        try (var s = importer.fetch();) {
            switch (getContentKind()) {
                case USER ->
                    userImportFacade.importContents(importCtx);
            }
        }

        try (var r = recSvc.playRecords();) {
            importer.doPostProcess(new ImportationRecordMap(r, confSvc.getTmpDir(), jsonb));
        }

        return BatchStatus.COMPLETED.name();
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
        private final JsonObject options;

        /**
         * Constructor.
         *
         * @param importer the {@code Importer}
         * @param transformer the {@code Transformer}
         * @param options optional configurations for import process
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        public ImportContextImpl(Importer importer, Transformer transformer, JsonObject options) {
            this.importer = Objects.requireNonNull(importer);
            this.transformer = Objects.requireNonNull(transformer);
            this.options = Objects.requireNonNull(options);
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
