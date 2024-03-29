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
package jp.mydns.projectk.safi.constant;

import jp.mydns.projectk.safi.value.ContentRecord;

/**
 * Kind definitions of the {@link ContentRecord}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RecordKind {
    /**
     * Indicates that the content was successfully registered.
     *
     * @since 1.0.0
     */
    REGISTER,
    /**
     * Indicates that the content was successfully deleted.
     *
     * @since 1.0.0
     */
    DELETION,
    /**
     * Indicates that processing for content has terminated abnormally.
     *
     * @since 1.0.0
     */
    FAILURE;

    /**
     * Returns a {@code true} if processing for content has terminated normally, otherwise {@code false}.
     *
     * @return {@code true} if processing for content has terminated normally, otherwise {@code false}
     * @since 1.0.0
     */
    public boolean isSuccessful() {
        return this != FAILURE;
    }
}
