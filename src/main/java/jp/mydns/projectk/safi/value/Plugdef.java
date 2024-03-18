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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.JsonObject;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Type;
import jp.mydns.projectk.plugin.Plugin;

/**
 * Definition for {@link Plugin} to loading and executing.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <p>
 * JSON format
 * <pre><code>
 * {
 *     "$schema": "https://json-schema.org/draft/2020-12/schema",
 *     "$id": "https://project-k.mydns.jp/safi/plugdef.schema.json",
 *     "title": "Plugdef",
 *     "description": "Definition for Plug-in to loading and executing.",
 *     "type": "object",
 *     "properties": {
 *         "name": {
 *             "description": "Name of the target Plug-in.",
 *             "type": "string",
 *             "minLength": 1
 *         },
 *         "args": {
 *             "description": "Plugin execution arguments.",
 *             "type": "object"
 *         },
 *         "required": [
 *             "name",
 *             "args"
 *         ]
 *     }
 * }
 * </code></pre>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonbTypeDeserializer(Plugdef.Deserializer.class)
@Schema(name = "Plugdef", description = "Definition for Plug-in to loading and executing.")
public interface Plugdef {

    /**
     * Get the name of the target {@link Plugin}.
     *
     * @return name of the target {@code Plugin}
     * @since 1.0.0
     */
    @NotBlank
    @Schema(description = "Name of the target Plug-in.")
    String getName();

    /**
     * Get the {@link Plugin} execution arguments.
     *
     * @return the {@code Plugin} execution arguments
     * @since 1.0.0
     */
    @NotNull
    @Schema(description = "Plug-in execution arguments.")
    JsonObject getArgs();

    /**
     * JSON deserializer for {@code Plugdef}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    class Deserializer implements JsonbDeserializer<Plugdef> {

        /**
         * Construct a new JSON deserializer.
         *
         * @since 1.0.0
         */
        public Deserializer() {
        }

        /**
         * {@inheritDoc}
         *
         * @since 1.0.0
         */
        @Override
        public Plugdef deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            return ctx.deserialize(Bean.class, parser);
        }

        /**
         * Implements of the {@code Plugdef} as Java Beans.
         *
         * @author riru
         * @version 1.0.0
         * @since 1.0.0
         */
        protected static class Bean implements Plugdef {

            private String name;
            private JsonObject args;

            /**
             * Constructor just for JSON deserialization.
             *
             * @since 1.0.0
             */
            protected Bean() {
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String getName() {
                return name;
            }

            /**
             * Set the name of the target {@link Plugin}.
             *
             * @param name name of the target {@code Plugin}
             * @since 1.0.0
             */
            public void setName(String name) {
                this.name = name;
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public JsonObject getArgs() {
                return args;
            }

            /**
             * Set the {@link Plugin} execution arguments.
             *
             * @param args the {@code Plugin} execution arguments
             * @since 1.0.0
             */
            public void setArgs(JsonObject args) {
                this.args = args;
            }

            /**
             * Returns a string representation.
             *
             * @return a string representation
             * @since 1.0.0
             */
            @Override
            public String toString() {
                return "Plugdef{" + "name=" + name + ", args=" + args + '}';
            }
        }
    }
}
