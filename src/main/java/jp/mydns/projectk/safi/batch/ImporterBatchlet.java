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
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.plugin.ImportResult;
import jp.mydns.projectk.safi.plugin.ImportResultContainer;
import jp.mydns.projectk.safi.producer.RequestContextProducer;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.service.ImporterService;
import jp.mydns.projectk.safi.service.ImporterService.Importer;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.service.TransformerService;
import jp.mydns.projectk.safi.service.TransformerService.Transformer;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.ContentMap;
import jp.mydns.projectk.safi.value.ContentRecord;
import jp.mydns.projectk.safi.value.ImportContext;
import trial.ImportationFacade.UserImportationFacade;
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
    private ConfigService confSvc;

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

        try (var r = recSvc.playRecords(); var m = toImportationRecordMap(r);) {
            importer.doPostProcess(m);
        }

        return BatchStatus.COMPLETED.name();
    }

    private ImportationRecordMap toImportationRecordMap(Stream<ContentRecord> r) throws IOException {
        Iterator<Map.Entry<String, ImportResult>> itr = r.map(ImportResultImpl::new).map(ImportResult.class::cast)
                .map(v -> Map.entry(UUID.randomUUID().toString(), v)).iterator();
        return new ImportationRecordMap(itr, confSvc.getTmpDir(), new ImportResultConvertorImpl());
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

    /**
     * Implements of the {@code ContentMap.Convertor<ImportResult>}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    private class ImportResultConvertorImpl implements ContentMap.Convertor<ImportResult> {

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String serialize(ImportResult c) {
            return jsonSvc.toJson(c);
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public ImportResult deserialize(String s) {
            try (var r = Json.createReader(new StringReader(s))) {
                return new ImportResultImpl(r.readObject());
            }
        }

    }

    /**
     * Implements of the {@code ImportResult}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    private class ImportResultImpl implements ImportResult {

        private final boolean success;
        private final String kindName;
        private final String formatName;
        private final JsonObject value;
        private final String message;

        /**
         * Constructor.
         *
         * @param o the {@code JsonObject} representation of the {@code ImportResult}
         * @since 1.0.0
         */
        private ImportResultImpl(JsonObject o) {
            this.success = o.getBoolean("success");
            this.kindName = o.getString("kindName");
            this.formatName = o.getString("formatName");
            this.value = o.getJsonObject("value");
            this.message = o.getString("message", null);
        }

        /**
         * Constructor.
         *
         * @param rec the {@code ContentRecord}
         * @since 1.0.0
         */
        private ImportResultImpl(ContentRecord rec) {
            this.success = rec.getKind().isSuccessful();
            this.kindName = rec.getKind().name();
            this.formatName = rec.getFormat().name();
            this.value = rec.getValue();
            this.message = rec.getMessage();
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public boolean isSuccess() {
            return success;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String getKindName() {
            return kindName;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String getFormatName() {
            return formatName;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public JsonObject getValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String getMessage() {
            return message;
        }

        /**
         * Returns a string representation.
         *
         * @return a string representation
         * @since 1.0.0
         */
        @Override
        public String toString() {
            return "ImportResult{" + "success=" + success + ", kindName=" + kindName + ", formatName=" + formatName
                    + ", value=" + value + ", message=" + message + '}';
        }
    }

    /**
     * Collection of the {@code ImportResult}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    private class ImportationRecordMap extends ContentMap<ImportResult> implements ImportResultContainer {

        /**
         * Constructor.
         *
         * @param results stream of the {@code ImportResult}
         * @param tmpDir temporary directory
         * @param valueConvertor the {@code ContentMap.Convertor<>ImportResult}
         * @throws IOException if occurs I/O error
         * @since 1.0.0
         */
        private ImportationRecordMap(Iterator<Map.Entry<String, ImportResult>> results, Path tmpDir,
                ContentMap.Convertor<ImportResult> valueConvertor) throws IOException {
            super(Objects.requireNonNull(results), Objects.requireNonNull(tmpDir), valueConvertor);
        }
    }
}
