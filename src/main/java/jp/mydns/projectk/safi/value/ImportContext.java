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
package jp.mydns.projectk.safi.value;

import jp.mydns.projectk.safi.service.ImporterService.Importer;
import jp.mydns.projectk.safi.service.TransformerService.Transformer;

/**
 * Provides information used in the content import process.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportContext {

    /**
     * Get the {@code Importer}.
     *
     * @return the {@code Importer}
     * @since 1.0.0
     */
    Importer getImporter();

    /**
     * Get the {@code Transformer}.
     *
     *
     * @return the {@code Transformer}
     * @since 1.0.0
     */
    Transformer getTransformer();

    /**
     * Indicates permission for implicit deletion of lost content.
     *
     * @return {@code true} if allow implicit deletion on import. Default is {@code false}.
     * @since 1.0.0
     */
    boolean isAllowedImplicitDeletion();

    /**
     * Provides an additional filtering condition for content to be implicitly deleted on import.
     *
     * @return additional filtering condition. Default is no condition.
     * @since 1.0.0
     */
    Condition getAdditionalConditionForImplicitDeletion();

    /**
     * Provides a limit number of implicit deletions. If the number of implicit deletes exceeds this limit, no implicit
     * deletes will occur.
     *
     * @return limit number. Default is {@value Long#MAX_VALUE}.
     * @since 1.0.0
     */
    long getLimitNumberOfImplicitDeletion();
}
