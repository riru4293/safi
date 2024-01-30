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

import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.plugin.NoSuchPluginException;
import jp.mydns.projectk.plugin.PluginStorage;
import jp.mydns.projectk.plugin.impl.PluginLoaderImpl;
import jp.mydns.projectk.safi.plugin.SafiPlugin;

/**
 * A plug-in loading facility for <i>SAFI</i>.
 *
 * @param <T> plug-in interface type
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class SafiPluginLoader<T extends SafiPlugin> extends PluginLoaderImpl<T> {

    /**
     * Construct from the {@code PluginStorage}.
     *
     * @param clazz plug-in type
     * @param storage the {@code PluginStorage}
     * @throws NullPointerException if any argument is {@code null}
     * @since 1.0.0
     */
    public SafiPluginLoader(Class<T> clazz, PluginStorage storage) {
        super(clazz, storage);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException {@inheritDoc}
     * @throws NoSuchPluginException {@inheritDoc}
     * @throws PluginLoadingException {@inheritDoc}
     * @since 1.0.0
     */
    @Override
    public T load(String name) {
        return Optional.ofNullable(getSuppliers().get(Objects.requireNonNull(name))).orElseThrow(() -> new NoSuchPluginException(
                "No such a plug-in [%s]. Availables are %s.".formatted(name, getSuppliers().keySet()))).get();
    }
}
