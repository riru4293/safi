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
package trial;

import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.plugin.ImportResult;
import jp.mydns.projectk.safi.plugin.ImportResultContainer;
import jp.mydns.projectk.safi.value.ContentMap;
import jp.mydns.projectk.safi.value.ContentRecord;

/**
 * Collection of the {@code ImportResult}.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImportationRecordMap extends ContentMap<ImportResult> implements ImportResultContainer {

    /**
     * Constructor.
     *
     * @param records stream of the {@code ContentRecord}
     * @param tmpDir temporary directory
     * @param jsonb the {@code Jsonb}
     * @throws IOException if occurs I/O error
     * @since 1.0.0
     */
    public ImportationRecordMap(Stream<ContentRecord> records, Path tmpDir, Jsonb jsonb) throws IOException {
        super(Objects.requireNonNull(records).map(ImportResultImpl::new).map(ImportResult.class::cast)
                .map(r -> Map.entry(UUID.randomUUID().toString(), r)).iterator(),
                Objects.requireNonNull(tmpDir), new ConvertorImpl(Objects.requireNonNull(jsonb)));
    }

    private static class ConvertorImpl implements Convertor<ImportResult> {

        private final Jsonb jsonb;

        /**
         * Constructor.
         *
         * @param jsonb the {@code Jsonb}
         * @since 1.0.0
         */
        public ConvertorImpl(Jsonb jsonb) {
            this.jsonb = jsonb;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String serialize(ImportResult c) {
            return jsonb.toJson(c);
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public ImportResult deserialize(String s) {
            return jsonb.fromJson(s, ImportResultImpl.class);
        }

    }

    /**
     * Implements of the {@code ImportResult}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    protected static class ImportResultImpl implements ImportResult {

        private boolean success;
        private String kindName;
        private String formatName;
        private JsonObject value;
        private String message;

        /**
         * Constructor. Used only for deserialization from JSON.
         *
         * @since 1.0.0
         */
        protected ImportResultImpl() {
        }

        /**
         * Constructor.
         *
         * @param rec the {@code ContentRecord}
         * @since 1.0.0
         */
        public ImportResultImpl(ContentRecord rec) {
            this.success = rec.getKind().isSuccessful();
            this.kindName = rec.getKind().name();
            this.formatName = rec.getFormat().name();
            this.value = rec.getValue();
            this.message = rec.getMessage();
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public boolean isSuccess() {
            return success;
        }

        /**
         * Set the successful or failure of the importation processing.
         *
         * @param success {@code true} if import was successful, otherwise {@code false}.
         * @since 1.0.0
         */
        public void setSuccess(boolean success) {
            this.success = success;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String getKindName() {
            return kindName;
        }

        /**
         * Set name of the result kind.
         *
         * @param kindName result kind name
         * @since 1.0.0
         */
        public void setKindName(String kindName) {
            this.kindName = kindName;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String getFormatName() {
            return formatName;
        }

        /**
         * Set name of the record value format.
         *
         * @param formatName format name
         * @since 1.0.0
         */
        public void setFormatName(String formatName) {
            this.formatName = formatName;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public JsonObject getValue() {
            return value;
        }

        /**
         * Set record value.
         *
         * @param value record value
         * @since 1.0.0
         */
        public void setValue(JsonObject value) {
            this.value = value;
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public String getMessage() {
            return message;
        }

        /**
         * Set result message.
         *
         * @param message result message
         * @since 1.0.0
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Returns a string representation.
         *
         * @return a string representation
         * @since 1.0.0
         */
        @Override
        public String toString() {
            return "ImportResult{" + "success=" + success + ", kindName=" + kindName + ", formatName=" + formatName
                    + ", value=" + value + ", message=" + message + '}';
        }
    }
}
