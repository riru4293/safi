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

import jp.mydns.projectk.safi.value.Job;

/**
 * Kind definitions of the {@link Job}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public enum JobKind {
    /**
     * Represents the contents import processing. Import process is the processing of registering contents from external
     * sources into the database.
     *
     * @since 1.0.0
     */
    IMPORT,
    /**
     * Represents the contents export processing. Export process is the processing of exporting content stored in the
     * database to externally.
     *
     * @since 1.0.0
     */
    EXPORT,
    /**
     * Represents the contents rebuild processing. Rebuild process is the processing of rebuilding the content stored in
     * the database with current information. Current information includes the current date and time, etc.
     *
     * @since 1.0.0
     */
    REBUILD,
    /**
     * Represents the contents archive processing. Archive process is the processing of physically deleting old content.
     * It will be exported externally before being deleted.
     *
     * @since 1.0.0
     */
    ARCHIVE,
}
