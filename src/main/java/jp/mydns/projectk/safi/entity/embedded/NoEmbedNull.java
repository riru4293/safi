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
package jp.mydns.projectk.safi.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.AggregateObjectMapping;

/**
 * Prohibit the mapping of {@code null} to {@link Embedded}. Implementation depends on the <i>EclipseLink</i>.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public final class NoEmbedNull implements DescriptorCustomizer {

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public NoEmbedNull() {
    }

    /**
     * Prevent certain {@link Embeddable} field from always becoming {@code null} when entity mapping.
     *
     * @param descriptor the {@code ClassDescriptor}
     * @since 1.0.0
     */
    @Override
    public void customize(ClassDescriptor descriptor) {
        withInheritors(descriptor.getJavaClass()).map(Class::getDeclaredFields).flatMap(Stream::of)
                .filter(f -> Objects.nonNull(toEmbedded(f))).map(Field::getName)
                .map(descriptor::getMappingForAttributeName).map(AggregateObjectMapping.class::cast)
                .forEach(this::setNoAllowedNull);
    }

    private Stream<Class<?>> withInheritors(Class<?> clazz) {
        return Optional.ofNullable(clazz.getSuperclass()).map(p -> Stream.concat(Stream.of(clazz), withInheritors(p)))
                .orElseGet(() -> Stream.of(clazz));
    }

    private Embedded toEmbedded(Field f) {
        return f.getAnnotation(Embedded.class);
    }

    private void setNoAllowedNull(AggregateObjectMapping aom) {
        aom.setIsNullAllowed(false);
    }
}
