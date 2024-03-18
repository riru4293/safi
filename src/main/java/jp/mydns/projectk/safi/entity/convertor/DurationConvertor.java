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
package jp.mydns.projectk.safi.entity.convertor;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 * JPA attribute convertor for the {@code Duration}. This convertor is applied automatically.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@Converter(autoApply = true)
public final class DurationConvertor implements AttributeConverter<Duration, String> {

    /**
     * Construct a new entity attribute convertor.
     *
     * @since 1.0.0
     */
    public DurationConvertor() {
    }

    /**
     * Convert to database column type.
     *
     * @param entityValue entity attribute value. It can be set {@code null}.
     * @return {@code entityValue} that converted to string representation of {@code Duration}. {@code null} if
     * {@code entityValue} is {@code null}.
     * @see Duration#toString() conversion process
     * @since 1.0.0
     */
    @Override
    public String convertToDatabaseColumn(Duration entityValue) {
        return entityValue != null ? entityValue.toString() : null;
    }

    /**
     * Convert to entity attribute type.
     *
     * @param databaseValue database column value. It must be a string representation of {@code Duration}. It can be set
     * {@code null}.
     * @return {@code databaseValue} as {@code Duration}. {@code null} if {@code databaseValue} is {@code null}.
     * @throws DateTimeParseException if {@code databaseValue} is malformed as {@code Duration}
     * @see Duration#parse(java.lang.CharSequence) conversion process
     * @since 1.0.0
     */
    @Override
    public Duration convertToEntityAttribute(String databaseValue) {
        return databaseValue != null ? Duration.parse(databaseValue) : null;
    }
}
