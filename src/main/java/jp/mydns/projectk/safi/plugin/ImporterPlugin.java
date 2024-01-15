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
package jp.mydns.projectk.safi.plugin;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import jp.mydns.projectk.plugin.PluginExecutionException;
import jp.mydns.projectk.safi.value.ContentRecord;

/**
 * Import the outer-content. Provides processing specific to the import destination in the process of importing contents
 * from the outside.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImporterPlugin extends SafiPlugin {

    /**
     * Fetch content values from a data source.
     *
     * @param entrance carry-in entrance of the fetched content. One carry-in represents for one content.
     * @return processing result message for the entire import processing
     * @throws PluginExecutionException if processing cannot be continued
     * @throws InterruptedException if interrupted
     * @since 1.0.0
     */
    List<String> fetch(Consumer<Map<String, String>> entrance) throws InterruptedException;

    /**
     * Perform post-import processing. Use the import results to respond to the data source.
     *
     * @param records content import record values.
     * @return processing result message for the entire import processing
     * @throws PluginExecutionException if processing cannot be continued
     * @throws InterruptedException if interrupted
     * @since 1.0.0
     */
    List<String> doPost(Stream<ContentRecord> records) throws InterruptedException;

    /**
     * Abstract implements of the {@code ImporterPlugin}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractImporterPlugin implements ImporterPlugin {

        /**
         * {@inheritDoc}
         *
         * @throws PluginExecutionException if processing cannot be continued
         * @since 1.0.0
         */
        @Override
        public final List<String> fetch(Consumer<Map<String, String>> entrance) throws InterruptedException {
            try {
                return fetchContents(entrance);
            } catch (PluginExecutionException | InterruptedException ex) {
                throw ex;
            } catch (Throwable ignore) {
                // Note:
                // Cause exception does not wrap because it may be contaminated by an exception class
                // loaded with another class loader.
                throw new PluginUnknownException();
            }
        }

        /**
         * {@inheritDoc}
         *
         * @throws PluginExecutionException if processing cannot be continuedExecute post-processing
         * @since 1.0.0
         */
        @Override
        public final List<String> doPost(Stream<ContentRecord> records) throws InterruptedException {
            try {
                return doPostProcessing(records);
            } catch (PluginExecutionException | InterruptedException ex) {
                throw ex;
            } catch (Throwable ignore) {
                // Note:
                // Cause exception does not wrap because it may be contaminated by an exception class
                // loaded with another class loader.
                throw new PluginUnknownException();
            }
        }

        /**
         * Fetch content values from a data source.
         *
         * @param entrance carry-in entrance of the fetched content. One carry-in represents for one content.
         * @return processing result message for the entire import processing
         * @throws PluginExecutionException if processing cannot be continued
         * @throws InterruptedException if interrupted
         * @since 1.0.0
         */
        abstract List<String> fetchContents(Consumer<Map<String, String>> entrance) throws InterruptedException;

        /**
         * Perform post-import processing. Use the import results to respond to the data source.
         *
         * @param records content import record values.
         * @return processing result message for the entire import processing
         * @throws PluginExecutionException if processing cannot be continued
         * @throws InterruptedException if interrupted
         * @since 1.0.0
         */
        abstract List<String> doPostProcessing(Stream<ContentRecord> records) throws InterruptedException;
    }
}
