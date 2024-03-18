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
import jakarta.inject.Inject;
import jp.mydns.projectk.plugin.PluginLoader;
import jp.mydns.projectk.plugin.impl.PluginLoaderImpl;
import jp.mydns.projectk.plugin.impl.PluginStorageImpl;
import jp.mydns.projectk.safi.plugin.FunctionPlugin;
import jp.mydns.projectk.safi.plugin.ImporterPlugin;
import jp.mydns.projectk.safi.service.ConfigService;

/**
 * Produce the {@link PluginLoader}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Dependent
public class PluginLoaderProducer {

    @Inject
    private ConfigService confSvc;

    /**
     * Construct by CDI.
     *
     * @since 1.0.0
     */
    protected PluginLoaderProducer() {
    }

    /**
     * Produce the {@code PluginLoader<FunctionPlugin>}.
     *
     * @return the {@code PluginLoader<FunctionPlugin>}
     * @since 1.0.0
     */
    @Produces
    @RequestScoped
    public PluginLoader<FunctionPlugin> produceFunctionPluginLoader() {
        return new PluginLoaderImpl<>(FunctionPlugin.class, new PluginStorageImpl(confSvc.getPluginDir()));
    }

    /**
     * Close the {@code PluginLoader<FunctionPlugin>} if dispose.
     *
     * @param loader the {@code PluginLoader<FunctionPlugin>}
     * @since 1.0.0
     */
    public void closeFunctionPluginLoader(@Disposes PluginLoader<FunctionPlugin> loader) {
        loader.close();
    }

    /**
     * Produce the {@code PluginLoader<ImporterPlugin>}.
     *
     * @return the {@code PluginLoader<ImporterPlugin>}
     * @since 1.0.0
     */
    @Produces
    @RequestScoped
    public PluginLoader<ImporterPlugin> produceImporterPluginLoader() {
        return new PluginLoaderImpl<>(ImporterPlugin.class, new PluginStorageImpl(confSvc.getPluginDir()));
    }

    /**
     * Close the {@code PluginLoader<ImporterPlugin>} if dispose.
     *
     * @param loader the {@code PluginLoader<ImporterPlugin>}
     * @since 1.0.0
     */
    public void closeImporterPluginLoader(@Disposes PluginLoader<ImporterPlugin> loader) {
        loader.close();
    }
}
