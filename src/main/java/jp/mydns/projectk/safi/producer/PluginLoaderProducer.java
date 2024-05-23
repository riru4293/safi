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
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.json.JsonObject;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import jp.mydns.projectk.plugin.PluginLoader;
import jp.mydns.projectk.safi.plugin.ContentSourceReaderPlugin;

/**
 * Produce the {@link PluginLoader}.
 *
 * @author riru
 * @version 2.0.0
 * @since 2.0.0
 */
public interface PluginLoaderProducer {

    /**
     * Produce the {@code PluginLoader<ContentSourceReaderPlugin>}.
     *
     * @return the {@code PluginLoader<ContentSourceReaderPlugin>}
     * @since 2.0.0
     */
    PluginLoader<ContentSourceReaderPlugin> produceContentSourceReaderPluginLoader();

    /**
     * Close the {@code PluginLoader<ContentSourceReaderPlugin>} if dispose.
     *
     * @param loader the {@code PluginLoader<ContentSourceReaderPlugin>}
     * @since 2.0.0
     */
    void closeContentSourceReaderPluginLoader(PluginLoader<ContentSourceReaderPlugin> loader);

    /**
     * Implementation of {@code PluginLoaderProducer}.
     *
     * @author riru
     * @version 2.0.0
     * @since 2.0.0
     */
    @Dependent
    class Stub implements PluginLoaderProducer {
// ToDo: Must be implement.

        @Override
        @Produces
        @RequestScoped
        public PluginLoader<ContentSourceReaderPlugin> produceContentSourceReaderPluginLoader() {
            return new PluginLoader<ContentSourceReaderPlugin>() {
                @Override
                public void close() {
                }

                @Override
                public ContentSourceReaderPlugin load(String name) {
                    return new ContentSourceReaderPlugin() {
                        @Override
                        public void fetch(Consumer<Map<String, String>> cnsmr) throws InterruptedException {
                            cnsmr.accept(Map.of("id", "33195", "name", "windows95"));
                            cnsmr.accept(Map.of("id", "hh6d9", "name", "windows2000"));
                        }

                        @Override
                        public void doPostProcess(Map<String, Boolean> map) throws InterruptedException {
                        }

                        @Override
                        public Path getWrkDir() {
                            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                        }

                        @Override
                        public void setWrkDir(Path path) {
                        }

                        @Override
                        public JsonObject getProperties() {
                            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                        }

                        @Override
                        public void setProperties(JsonObject jo) {
                        }

                        @Override
                        public Consumer<String> getReporter() {
                            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                        }

                        @Override
                        public void setReporter(Consumer<String> cnsmr) {
                        }

                        @Override
                        public String getAbout() {
                            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                        }

                        @Override
                        public String getVersion() {
                            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                        }
                    };
                }

                @Override
                public Stream<Map.Entry<String, Supplier<ContentSourceReaderPlugin>>> stream() {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }
            };
        }

        @Override
        public void closeContentSourceReaderPluginLoader(@Disposes PluginLoader<ContentSourceReaderPlugin> loader) {
        }
    }
}
