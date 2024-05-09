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
package stock;

import trial.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.value.ContentRecord;

/**
 *
 * @author riru
 */
@RequestScoped
public class JobService {

    @Inject
    private JsonService jsonSvc;

//    /**
//     * Represents a job option values. Returns the default value if the option value is not set or malformed value. This
//     * class is for known option values only.
//     *
//     * <p>
//     * Implementation requirements.
//     * <ul>
//     * <li>This class is immutable and thread-safe.</li>
//     * </ul>
//     *
//     * @author riru
//     * @version 1.0.0
//     * @since 1.0.0
//     */
//    private class JobOptionsImpl extends JsonObjectVo implements JobOptions {
//
//        private static final long serialVersionUID = -8252044403065798628L;
//
//        /**
//         * Construct from job option values.
//         *
//         * @param opts job option values
//         * @throws NullPointerException if {@code opts} is {@code null}
//         * @since 1.0.0
//         */
//        public JobOptionsImpl(JsonObject opts) {
//            super(opts);
//        }
//
//        /**
//         * Return {@code true} to allow implicit deletion on import.
//         *
//         * @return {@code true} if allow implicit deletion on import. Default is {@code false}.
//         * @since 1.0.0
//         */
//        @Override
//        public boolean allowImplicitDeletion() {
//            return getBoolean("allowImplicitDeletion", false);
//        }
//
//        /**
//         * Return {@code true} if processing includes add content.
//         *
//         * @return {@code true} if processing includes add content. Default is {@code true}.
//         * @since 1.0.0
//         */
//        @Override
//        public boolean containAddition() {
//            return getBoolean("containAddition", true);
//        }
//
//        /**
//         * Return {@code true} if processing includes update content.
//         *
//         * @return {@code true} if processing includes update content. Default is {@code true}.
//         * @since 1.0.0
//         */
//        @Override
//        public boolean containUpdate() {
//            return getBoolean("containUpdate", true);
//        }
//
//        /**
//         * Return {@code true} if processing includes delete content.
//         *
//         * @return {@code true} if processing includes delete content. Default is {@code false}.
//         * @since 1.0.0
//         */
//        @Override
//        public boolean containDeletion() {
//            return getBoolean("containDeletion", false);
//        }
//
//        /**
//         * Return {@code true} if processing includes unchanged content.
//         *
//         * @return {@code true} if processing includes unchanged content. Default is {@code false}.
//         * @since 1.0.0
//         */
//        @Override
//        public boolean containUnchanging() {
//            return getBoolean("containUnchanging", false);
//        }
//
//        /**
//         * Provides a limit number of deletions.
//         *
//         * @return limit number of deletions. Default is {@value Long#MAX_VALUE}.
//         * @since 1.0.0
//         */
//        @Override
//        public long limitOfDeletion() {
//
//            JsonValue val = get("limitOfDeletion");
//
//            try {
//
//                return JsonNumber.class.cast(val).longValueExact();
//
//            } catch (RuntimeException ignore) {
//
//                return Long.MAX_VALUE;  // Note: Means unlimited.
//            }
//
//        }
//
//        /**
//         * Provides a filtering condition for content to be implicitly deleted on import.
//         *
//         * @return filtering condition for content to be implicitly deleted on import. Default is no condition.
//         * @since 1.0.0
//         */
//        @Override
//        public Condition conditionOfImplicitDeletion() {
//            try {
//                return jsonSvc.convertViaJson(get("conditionOfImplicitDeletion"), Condition.class);
//            } catch (RuntimeException ignore) {
//                return Condition.emptyCondition();
//            }
//        }
//
//        /**
//         * Provides a period until the deletion is executed on export.
//         *
//         * @return period until the deletion is executed on export. Default is no grace.
//         * @since 1.0.0
//         */
//        @Override
//        public Period periodOfUntilDeletion() {
//            try {
//                return Period.parse(getString("periodOfUntilDeletion"));
//            } catch (RuntimeException ignore) {
//                return Period.ZERO;
//            }
//        }
//    }
//
//    public JobOptions buildJobOptions(JsonObject opts) {
//        return new JobOptionsImpl(Objects.requireNonNull(opts));
//    }
    public void appendMessages(String jobId, Stream< String> msgs) {

    }

    public void appendRecords(String jobId, Stream<ContentRecord> recs) {

    }

    public void updateToSuccessState(String jobId) {

    }

    public void updateToAbortState(String jobId) {

    }

    public void updateToFailureState(String jobId) {

    }
}
