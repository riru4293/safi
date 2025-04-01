/*
 * Copyright (c) 2025, Project-K
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

/**
 * Target content of the <i>Job</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public enum JobTarget {

    /**
     * Indicates that the <i>User</i> content.
     *
     * @since 3.0.0
     */
    USER,
    /**
     * Indicates that the <i>Asset</i> content.
     *
     * @since 3.0.0
     */
    ASSET,
    /**
     * Indicates that the <i>Belong Organization</i> content.
     *
     * @since 3.0.0
     */
    BELONG_ORG,
    /**
     * Indicates that the primary <i>Organization</i> content.
     *
     * @since 3.0.0
     */
    ORG1,
    /**
     * Indicates that the secondary <i>Organization</i> content.
     *
     * @since 3.0.0
     */
    ORG2,
    /**
     * Indicates that the <i>Belong Group</i> content.
     *
     * @since 3.0.0
     */
    BELONG_GRP,
    /**
     * Indicates that the <i>Group</i> content.
     *
     * @since 3.0.0
     */
    GRP,
    /**
     * Indicates that the combined all content per <i>User</i>.
     *
     * @since 3.0.0
     */
    PER_USER,
    /**
     * Indicates that the combined all content per <i>Asset</i>.
     *
     * @since 3.0.0
     */
    PER_ASSET,
}
