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
import jakarta.json.JsonValue;
import java.io.IOException;
import java.lang.System.Logger;
import jp.mydns.projectk.plugin.PluginLoader;
import jp.mydns.projectk.safi.plugin.ImporterPlugin;
import jp.mydns.projectk.safi.producer.RequestContextProducer;

@Named
@Dependent
public class ImporterBatchlet extends JobBatchlet {

    @Inject
    private RequestContextProducer reqCtxPrd;

    @Inject
    private PluginLoader<ImporterPlugin> plgLdr;

    @Inject
    private Logger logger;

    @Override
    public String mainProcess() throws InterruptedException, IOException {
        reqCtxPrd.setup("Importer");
        ImporterPlugin plugin = plgLdr.load("example");
        plugin.setPluginProperties(JsonValue.EMPTY_JSON_OBJECT);
        plugin.setReporter(s -> logger.log(Logger.Level.INFO, s));
        plugin.fetch(m -> {
        });
        return BatchStatus.COMPLETED.name();
    }
}
