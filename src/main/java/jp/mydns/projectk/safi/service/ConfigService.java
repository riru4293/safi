/*
 * Copyright 2025, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;
import jp.mydns.projectk.safi.util.TimeUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 Provides configuration values for this application.

 <p>
 Configuration values can be overridden through external sources such as environment variables,
 system properties, JNDI entries, or external configuration files. This allows environment‑dependent
 settings and temporary configuration changes to be applied without modifying the application code.

 <p>
 Overrides are resolved in the following order (highest priority first):
 <ol>
     <li>System properties</li>
     <li>Environment variables</li>
     <li>JNDI entries</li>
     <li>External configuration files</li>
     <li>Built‑in default values</li>
 </ol>

 <p>
 Placeholders such as <code>${key}</code> are resolved recursively using the same priority rules.
 All changes take effect immediately.

 <h3>Configurations</h3>
 <table>
     <tr>
         <th>Key name (Environment variable name)</th>
         <th>Built‑in default value</th>
         <th>Description</th>
     </tr>
     <tr>
         <td>safi.home (SAFI_HOME)</td>
         <td><i>(none - Must be provided)</i></td>
         <td>Base directory.</td>
     </tr>
     <tr>
         <td>safi.var.dir (SAFI_VAR_DIR)</td>
         <td>${safi.home},var</td>
         <td>"${safi.home}/var" directory.</td>
     </tr>
     <tr>
         <td>safi.tmp.dir (SAFI_TMP_DIR)</td>
         <td>${safi.home},tmp</td>
         <td>"${safi.home}/tmp" directory.</td>
     </tr>
     <tr>
         <td>safi.plugin.dir (SAFI_PLUGIN_DIR)</td>
         <td>${safi.var.dir},plugin</td>
         <td>"${safi.var.dir}/plugin" directory.</td>
     </tr>
 </table>

 <p>
 Implementation requirements.
 <ul>
     <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface ConfigService
{
    /**
     Get variable data directory. This directory is guaranteed to exist and be writable.

     @return variable data directory
     @throws NoSuchElementException If not found. The application is in an invalid state due to
                                    unmet prerequisites. Processing cannot continue.
     @since 3.0.0
     */
    Path getVarDir();

    /**
     Get temporary directory. This directory is guaranteed to exist and be writable.

     @return temporary directory
     @throws NoSuchElementException If not found. The application is in an invalid state due to
                                    unmet prerequisites. Processing cannot continue.
     @since 3.0.0
     */
    Path getTmpDir();

    /**
     Get plugin stored directory. This directory is guaranteed to exist and be readable.

     @return plugin stored directory
     @throws NoSuchElementException If not found. The application is in an invalid state due to
                                    unmet prerequisites. Processing cannot continue.
     @since 3.0.0
     */
    Path getPluginDir();

    /**
     If the current time within this application is provided as a configuration, the configured
     value will be returned. In normal cases, such a configuration value is not expected. This value
     is to be provided only when a specific time should be treated as the current time. When the
     value is provided, it must always be used instead of the system's current time.
     <p>
     If the configuration value is invalid, the value is ignored and returns empty.

     @return special current time
     @since 3.0.0
     */
    Optional<LocalDateTime> getFrozenTime();

    /**
     @hidden
     */
    @Typed(ConfigService.class)
    @ApplicationScoped
    class Impl implements ConfigService
    {
        @SuppressWarnings("unused") // Note: To be called by CDI.
        Impl() {}

        @Override
        public Path getVarDir() {
            return getValueAsPath("safi.var.dir").orElseThrow();
        }

        @Override
        public Path getTmpDir() {
            return getValueAsPath("safi.tmp.dir").orElseThrow();
        }

        @Override
        public Path getPluginDir() {
            return getValueAsPath("safi.plugin.dir").orElseThrow();
        }

        @Override
        public Optional<LocalDateTime> getFrozenTime() {
            return getValue("safi.now").flatMap(TimeUtils::tryParseToLocalDateTime);
        }

        Config getConfig() {
            return ConfigProvider.getConfig();
        }

        Optional<String> getValue(String name) {
            return Optional.ofNullable(getConfig().getConfigValue(name).getRawValue());
        }

        List<String> getValueAsList(String name) {
            return getConfig().getOptionalValues(name, String.class).orElseGet(List::of);
        }

        Optional<Path> getValueAsPath(String name)
        {
            List<String> paths = getValueAsList(name);

            if (paths.isEmpty()) {
                return Optional.empty();
            }

            String first = paths.get(0);
            String[] remainings = IntStream.range(1, paths.size())
                                            .mapToObj(paths::get)
                                            .toArray(String[]::new);

            return Optional.of(Path.of(first, remainings));
        }

    }

}
