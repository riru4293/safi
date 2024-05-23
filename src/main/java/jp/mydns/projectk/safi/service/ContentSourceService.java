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
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import jp.mydns.projectk.plugin.NoSuchPluginException;
import jp.mydns.projectk.plugin.PluginExecutionException;
import jp.mydns.projectk.plugin.PluginLoader;
import jp.mydns.projectk.plugin.PluginLoadingException;
import jp.mydns.projectk.safi.plugin.ContentSourceReaderPlugin;
import jp.mydns.projectk.safi.service.proc.ContentSourceReader;
import jp.mydns.projectk.safi.value.PluginConfig;

/**
 * Provides a process that provides a Content-Source collection.
 *
 * @author riru
 * @version 2.0.0
 * @since 2.0.0
 */
public interface ContentSourceService {

    /**
     * Load a {@code ContentSourceReaderPlugin}.
     *
     * @param wrkDir working directory for {@code ContentSourceReaderPlugin}
     * @param plgConf the {@code PluginConfig}
     * @return the {@code ContentSourceReaderPlugin}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code wrkDir} is not an existing directory
     * @throws ConstraintViolationException if {@code plgConf} violates constraints
     * @throws NoSuchPluginException if no found plug-in indicated by {@code plgConf}
     * @throws PluginLoadingException if an error occurs while plug-in loading
     * @since 2.0.0
     */
    ContentSourceReaderPlugin newContentSourceReaderPlugin(Path wrkDir, PluginConfig plgConf);

    /**
     * Build an {@code ContentSourceReader}.
     *
     * @param wrkDir working directory for {@code ContentSourceReader}
     * @param plugin the {@code ContentSourceReaderPlugin}
     * @return the {@code ContentSourceReader}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code wrkDir} is not an existing directory
     * @since 2.0.0
     */
    ContentSourceReader newContentSourceReader(Path wrkDir, ContentSourceReaderPlugin plugin);

    /**
     * Implementation of {@code ContentSourceService}.
     *
     * @author riru
     * @version 2.0.0
     * @since 2.0.0
     */
    @RequestScoped
    class Impl implements ContentSourceService {

        @Inject
        private PluginLoader<ContentSourceReaderPlugin> plgLdr;

        @Inject
        private ValidationService validSvc;

        @Inject
        private JsonService jsonSvc;

        @Inject
        private ResultReportService repSvc;

        /**
         * Construct by CDI.
         *
         * @since 2.0.0
         */
        protected Impl() {
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if any argument is {@code null}
         * @throws IllegalArgumentException if {@code wrkDir} is not an existing directory
         * @throws ConstraintViolationException if {@code plgConf} violates constraints
         * @throws NoSuchPluginException if no found plug-in indicated by {@code plgConf}
         * @throws PluginLoadingException if an error occurs while plug-in loading
         * @since 2.0.0
         */
        @Override
        public ContentSourceReaderPlugin newContentSourceReaderPlugin(Path wrkDir, PluginConfig plgConf) {
            Objects.requireNonNull(wrkDir);
            Objects.requireNonNull(plgConf);

            if (!wrkDir.toFile().isDirectory()) {
                throw new IllegalArgumentException(String.format(
                        "No found a working directory for ContentSourceReaderPlugin. [%s]", wrkDir));
            }

            validSvc.requireValid(plgConf);

            ContentSourceReaderPlugin plugin = plgLdr.load(plgConf.getName());
            plugin.setWrkDir(wrkDir);
            plugin.setProperties(plgConf.getProperties());
            plugin.setReporter(repSvc::reportMessage);

            return plugin;
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if any argument is {@code null}
         * @throws IllegalArgumentException if {@code wrkDir} is not an existing directory
         * @since 2.0.0
         */
        @Override
        public ContentSourceReader newContentSourceReader(Path wrkDir, ContentSourceReaderPlugin plugin) {
            Objects.requireNonNull(wrkDir);
            Objects.requireNonNull(plugin);

            if (!wrkDir.toFile().isDirectory()) {
                throw new IllegalArgumentException(String.format(
                        "No found a working directory for ContentSourceReader. [%s]", wrkDir));
            }

            return new ContentSourceReaderImpl(wrkDir, plugin);
        }

        private class ContentSourceReaderImpl implements ContentSourceReader {

            private final Path storage;
            private final ContentSourceReaderPlugin plugin;

            private ContentSourceReaderImpl(Path wrkDir, ContentSourceReaderPlugin plugin) {
                this.storage = wrkDir.resolve("content-sources.json");
                this.plugin = plugin;
            }

            /**
             * {@inheritDoc}
             *
             * @throws PluginExecutionException if processing cannot be continued
             * @since 2.0.0
             */
            @Override
            public Stream<Map<String, String>> fetch() throws IOException, InterruptedException {

                try (var g = Json.createGenerator(Files.newBufferedWriter(storage, CREATE, WRITE, TRUNCATE_EXISTING));) {
                    g.writeStartArray();

                    plugin.fetch(m -> {
                        g.writeStartObject();
                        m.entrySet().stream().forEachOrdered(e -> g.write(e.getKey(), e.getValue()));
                        g.writeEnd();
                    });

                    g.writeEnd();
                }

                return jsonSvc.toStream(Files.newInputStream(storage)).map(jsonSvc::toStringMap);
            }

            /**
             * {@inheritDoc}
             *
             * @throws PluginExecutionException if processing cannot be continued
             * @since 2.0.0
             */
            @Override
            public void doPostProcess(Map<String, Boolean> results) throws IOException, InterruptedException {
                plugin.doPostProcess(results);
            }
        }
    }
}
