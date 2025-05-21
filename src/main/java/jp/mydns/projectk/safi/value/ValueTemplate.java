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

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Objects;
import jp.mydns.projectk.safi.util.CdiUtils;
import jp.mydns.projectk.safi.PublishableRuntimeException;
import jp.mydns.projectk.safi.util.ValidationUtils;

/**
 Template of value object.

 <p>
 Implementation requirements.
 <ul>
 <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface ValueTemplate {

/**
 Abstract builder of the {@code Template}.

 @param <B> builder type
 @param <V> value type
 @author riru
 @version 3.0.0
 @since 3.0.0
 */
abstract class AbstractBuilder<B extends AbstractBuilder<B, V>, V extends ValueTemplate> {

protected final Class<B> builderType;

protected AbstractBuilder(Class<B> builderType) {
    this.builderType = Objects.requireNonNull(builderType);
}

/**
 Set all properties from {@code src}.

 @param src source value
 @return updated this
 @throws NullPointerException if {@code src} is {@code null}
 @since 3.0.0
 */
public B with(V src) {
    Objects.requireNonNull(src);
    return builderType.cast(this);
}

/**
 Build a new inspected instance.
 <p>
 The {@link Validator} to use for validation will attempt to obtain it from the CDI container.
 <p>
 Note that while this is convenient, it can be slow, so if you are doing large builds, consider
 using {@link #build(jakarta.validation.Validator, java.lang.Class...)}.

 @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
 @return new inspected instance
 @throws NullPointerException if any argument is {@code null}
 @throws ConstraintViolationException if occurred constraint violations when building
 @throws PublishableRuntimeException if can't get the {@code Validator}
 @since 3.0.0
 */
public V build(Class<?>... groups) {
    return build(CdiUtils.get(Validator.class), groups);
}

/**
 Build a new inspected instance.

 @param validator the {@code Validator}
 @param groups validation groups. Use the {@link jakarta.validation.groups.Default} if empty.
 @return new inspected instance
 @throws NullPointerException if any argument is {@code null}
 @throws ConstraintViolationException if occurred constraint violations when building
 @since 3.0.0
 */
public V build(Validator validator, Class<?>... groups) {
    return ValidationUtils.requireValid(unsafeBuild(), validator, groups);
}

/**
 Build a new instance. It instance may not meet that constraint. Use only if the original value is
 completely reliable.

 @return new unsafe instance
 @since 3.0.0
 */
public abstract V unsafeBuild();

}

}
