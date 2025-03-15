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
package jp.mydns.projectk.safi.value;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An information for transform content values.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/../schemas/trnsdef.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@Schema(name = "Trnsdef", description
        = """
The key is the property name after transformation, and the value is the transformation expression. Details of the expression are as follows.

# Trunsform expression syntax

| Element              | Syntax              | Description |
|----------------------|---------------------|-------------|
| Literal element      | \\`literal-value\\` | The literal value is enclosed by \\`. If you want to escape it, prefix it with \\. |
| Input element        | [input-value-name]  | The input value encloses its name in [ and ]. |
| Function element     | func(arg, arg)      | Function consists of a function name followed by arguments enclosed in ( and ). The arguments are separated by , and the number varies depending on the function. The argument is any kind of the element that "Input" or "Literal" or "Function". |
| Elements joiner      | &                   | Joiner concatenates the values before and after the element. Also, there must be white space before and after the Joiner. |

# Trunsform example

## Premise

- Original values: {"id": " 01 ", "name": "taro"}
- Trunsform expression: [name] & \\`'s number is \\` & LPAD( TRIM( [id] ), \\`4\\`, \\`P\\` )
- Functions
  - TRIM
    - syntax: TRIM( arg-value )
    - description: Trim leading and trailing spaces from arguments.
  - LPAD
    - syntax: LPAD( arg-value, arg-length, arg-padding-char )
    - description: Pads a string to the left with the specified characters until the specified number of digits is reached.

## Process of calculating

| Step   | Interpretation of the expression |
|--------|----------------------------------|
| Step.0 | [name] & \\`'s number is \\` & LPAD( TRIM( [id] ), \\`4\\`, \\`P\\` ) |
| Step.1 | [name] & \\`'s number is \\` & LPAD( TRIM( __\\` 01 \\`__ ), \\`4\\`, \\`P\\` ) |
| Step.2 | [name] & \\`'s number is \\` & LPAD( __\\`01\\`__, \\`4\\`, \\`P\\` ) |
| Step.3 | __\\`taro\\`__ & \\`'s number is \\` & __\\`PP01\\`__ |
| Result | __taro's number is PP01__ |""",
        example = "{\"name\":\"toTitleCase([firstName]) & ` ` & toTitleCase([lastName])\", \"id\":\"[userId]\"}",
        type = "object", additionalItems = String.class, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class TrnsdefValue extends LinkedHashMap<String, String> {

    /**
     * Build with {@code Map<String, String>}.
     *
     * @param m transform definition
     * @return received as is
     * @throws NullPointerException if {@code m} is {@code null} or if contains {@code null} key in {@code m}.
     * @since 3.0.0
     */
    public static TrnsdefValue of(Map<String, String> m) {
        var val = new TrnsdefValue();
        val.putAll(m);
        return val;
    }

    @Schema(hidden = true)
    protected boolean empty; // Note: For supress "empty" property in OpenAPI.

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     * @since 3.0.0
     */
    @Override
    public String put(String key, String value) {
        return super.put(Objects.requireNonNull(key), value);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code m} is {@code null} or if contains {@code null} key in {@code m}.
     * @since 3.0.0
     */
    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        m.keySet().forEach(Objects::requireNonNull);
        super.putAll(m);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code key} is {@code null}
     * @since 3.0.0
     */
    @Override
    public String putIfAbsent(String key, String value) {
        return super.putIfAbsent(Objects.requireNonNull(key), value);
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 3.0.0
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Indicates that specified object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if matches otherwise {@code false}.
     * @since 3.0.0
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Map<?, ?> m && super.equals(m);
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
