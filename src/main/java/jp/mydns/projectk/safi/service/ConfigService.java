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
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Provides configuration values for this application.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@RequestScoped
public class ConfigService {

    /**
     * Get temporary directory. This directory is guaranteed to exist and be writable.
     *
     * @return temporary directory
     * @throws NoSuchElementException if no value is found. If this exception occurs, the execution environment does not
     * meet the prerequisites.
     * @since 1.0.0
     */
    public Path getTmpDir() {
        return getValueAsPath("safi.tmp.dir").orElseThrow();
    }

    /**
     * Get variable data directory. This directory is guaranteed to exist and be writable.
     *
     * @return variable data directory
     * @throws NoSuchElementException if no value is found. If this exception occurs, the execution environment does not
     * meet the prerequisites.
     * @since 1.0.0
     */
    public Path getVarDir() {
        return getValueAsPath("safi.var.dir").orElseThrow();
    }

    /**
     * Get jobs data directory. It is assumed that a folder will be created and used with the job ID for each job. This
     * directory is guaranteed to exist and be writable.
     *
     * @return jobs data directory
     * @throws NoSuchElementException if no value is found. If this exception occurs, the execution environment does not
     * meet the prerequisites.
     * @since 1.0.0
     */
    public Path getJobDir() {
        return getValueAsPath("safi.job.dir").orElseThrow();
    }

    /**
     * Get plug-in stored directory. This directory is guaranteed to exist and be readable.
     *
     * @return plug-in stored directory
     * @throws NoSuchElementException if no value is found. If this exception occurs, the execution environment does not
     * meet the prerequisites.
     * @since 1.0.0
     */
    public Path getPluginDir() {
        return getValueAsPath("safi.plugin.dir").orElseThrow();
    }

    Config getConfig() {
        return ConfigProvider.getConfig();
    }

    Optional<String> getValue(String name) {
        return getConfig().getOptionalValue(name, String.class);
    }

    List<String> getValueAsList(String name) {
        return getConfig().getOptionalValues(name, String.class).orElseGet(List::of);
    }

    Optional<Path> getValueAsPath(String name) {
        List<String> paths = getValueAsList(name);

        if (paths.isEmpty()) {
            return Optional.empty();
        }

        String first = paths.get(0);
        String[] remainings = IntStream.range(1, paths.size()).mapToObj(paths::get).toArray(String[]::new);

        return Optional.of(Path.of(first, remainings));
    }
}
