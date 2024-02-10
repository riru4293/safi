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
import jakarta.json.bind.Jsonb;
import jakarta.validation.Validator;
import java.io.IOException;
import jp.mydns.projectk.safi.producer.RequestContextProducer;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.service.ImporterService;
import jp.mydns.projectk.safi.service.ImporterService.Importer;
import jp.mydns.projectk.safi.service.TransformationService;
import jp.mydns.projectk.safi.service.TransformationService.Transformer;
import trial.ImportContext;
import trial.ImportationFacade;
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
    private ImportationFacade importationFcd;

    @Inject
    private Validator validator;

    @Inject
    private ConfigService confSvc;

    @Inject
    private Jsonb jsonb;

    @Inject
    private TransformationService transSvc;

    @Override
    public String mainProcess() throws InterruptedException, IOException {
        reqCtxPrd.setup("Importer");

        Importer importer = importerSvc.buildImporter(getWrkDir(), getPlugdef(), getJobOptions());
        Transformer transformer = transSvc.buildTransformer(getTrnsdef());

        ImportContext importCtx = new ImportContext.Builder().withImporter(importer).withTransformer(transformer)
                .withJobOptions(getJobOptions()).build(validator);

        try (var s = importer.fetch();) {
            importationFcd.importContents(importCtx);
        }

        try (var r = recSvc.playRecords();) {
            importer.doPostProcess(new ImportationRecordMap(r, confSvc.getTmpDir(), jsonb));
        }

        return BatchStatus.COMPLETED.name();
    }
}
