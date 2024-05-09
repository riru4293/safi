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
package jp.mydns.projectk.safi.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import jp.mydns.projectk.plugin.NoSuchPluginException;
import jp.mydns.projectk.plugin.PluginExecutionException;
import jp.mydns.projectk.plugin.PluginLoader;
import jp.mydns.projectk.plugin.PluginLoadingException;
import jp.mydns.projectk.safi.plugin.ImportResultContainer;
import jp.mydns.projectk.safi.plugin.ImporterPlugin;
import jp.mydns.projectk.safi.util.JsonUtils;
import static jp.mydns.projectk.safi.util.JsonUtils.toJsonValue;
import jp.mydns.projectk.safi.value.Plugdef;
import trial.JobRecordingService;

/**
 * Provider of the {@link Importer}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class ImporterService {

    @Inject
    private PluginLoader<ImporterPlugin> plgLdr;

    @Inject
    private JobRecordingService recSvc;

    @Inject
    private AppTimeService appTimeSvc;

    @Inject
    private JsonService jsonSvc;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected ImporterService() {
    }

    /**
     * This is the processing related to <i>SAFI</i> external data in the content importation processing, and its main
     * function is fetch the data.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static interface Importer {

        /**
         * Fetch the original data for the importation content from outside <i>SAFI</i>.
         *
         * @return original data for the importation content
         * @throws InterruptedException if interrupted
         * @throws IOException if occurs I/O error
         * @since 1.0.0
         */
        Stream<Map<String, String>> fetch() throws InterruptedException, IOException;

        /**
         * After importing content into <i>SAFI</i>, use the import results to perform processing. For example,
         * deleting, moving, or updating an import source.
         *
         * @param results the {@code ImportResultContainer}
         * @throws InterruptedException if interrupted
         * @since 1.0.0
         */
        void doPostProcess(ImportResultContainer results) throws InterruptedException;
    }

    /**
     * Build the {@code Importer} that uses the {@link ImporterPlugin}. Note that the {@code Importer} may throw
     * plug-in-specific runtime exceptions that are not expressed in the Interface specification.
     *
     * @param wrkDir working directory for the {@code Importer}
     * @param plugdef the {@code Plugdef}
     * @param opts optional configuration for the {@code ImporterPlugin}
     * @return the {@code Importer}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code wrkDir} is not an existing directory
     * @throws ConstraintViolationException if the {@code plugdef} violates type constraints
     * @throws NoSuchPluginException if no found a plug-in for importation
     * @throws PluginLoadingException if an error occurs while plug-in loading
     * @since 1.0.0
     */
    public Importer buildImporter(Path wrkDir, @Valid Plugdef plugdef, JsonObject opts) {
        Objects.requireNonNull(wrkDir);
        Objects.requireNonNull(plugdef);
        Objects.requireNonNull(opts);

        if (!wrkDir.toFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("The working directory"
                    + " specified for the Importer is not an existing directory. [%s]", wrkDir));
        }

        JsonObject props = JsonUtils.merge(plugdef.getArgs(), Json.createObjectBuilder()
                .add("wrkDir", toJsonValue(wrkDir))
                .add("appNow", toJsonValue(appTimeSvc.getLocalNow()))
                .add("jobOpts", opts).build()
        );

        ImporterPlugin plugin = plgLdr.load(plugdef.getName());
        plugin.setPluginProperties(props);
        plugin.setReporter(recSvc::rec);

        return new ImporterImpl(plugin, wrkDir.resolve("contents.json"));
    }

    private class ImporterImpl implements Importer {

        private final ImporterPlugin plugin;
        private final Path storage;

        /**
         * Constructor.
         *
         * @param plugin the {@code ImporterPlugin}
         * @param storage temporary storage location for import content
         * @since 1.0.0
         */
        public ImporterImpl(ImporterPlugin plugin, Path storage) {
            this.plugin = plugin;
            this.storage = storage;
        }

        /**
         * {@inheritDoc}
         *
         * @throws PluginExecutionException if occurs an any exception within plug-in execution
         * @since 1.0.0
         */
        @Override
        public Stream<Map<String, String>> fetch() throws InterruptedException, IOException {
            try (var jg = Json.createGenerator(Files.newBufferedWriter(storage, WRITE, TRUNCATE_EXISTING));) {
                jg.writeStartArray();

                plugin.fetch(m -> {
                    jg.writeStartObject();
                    m.entrySet().stream().forEachOrdered(e -> jg.write(e.getKey(), e.getValue()));
                    jg.writeEnd();
                });

                jg.writeEnd();
            }

            return JsonUtils.toStream(Files.newInputStream(storage)).map(jsonSvc::toStringMap);
        }

        /**
         * {@inheritDoc}
         *
         * @throws PluginExecutionException if occurs an any exception within plug-in execution
         * @since 1.0.0
         */
        @Override
        public void doPostProcess(ImportResultContainer results) throws InterruptedException {
            plugin.doPost(results);
        }
    }
}
