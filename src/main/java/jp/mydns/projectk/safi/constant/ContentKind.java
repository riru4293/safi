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

import jp.mydns.projectk.safi.value.ContentValue;

/**
 * Kind definitions of the {@link ContentValue}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ContentKind {
    /**
     * Represents the <i>ID-Content</i> of the <i>User</i>.
     *
     * @since 1.0.0
     */
    USER,
    /**
     * Represents the <i>ID-Content</i> of the <i>Authentication-Medium</i>.
     *
     * @since 1.0.0
     */
    MEDIUM,
    /**
     * Represents the <i>ID-Content</i> of the <i>Organization</i> #1.
     *
     * @since 1.0.0
     */
    ORG1,
    /**
     * Represents the <i>ID-Content</i> of the <i>Organization</i> #2.
     *
     * @since 1.0.0
     */
    ORG2,
    /**
     * Represents the <i>ID-Content</i> of the <i>Belong-Organization</i>.
     *
     * @since 1.0.0
     */
    BELONG_ORG,
    /**
     * Represents the <i>ID-Content</i> of the <i>Group</i>.
     */
    GROUP,
    /**
     * Represents the <i>ID-Content</i> of the <i>Belong-Group</i>.
     *
     * @since 1.0.0
     */
    BELONG_GROUP,
}
