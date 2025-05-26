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
package jp.mydns.projectk.safi.util;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.UnresolvedInstanceException;

/**
 Utilities for Jakarta Contexts and Dependency Injection.

 <p>
 Implementation requirements.
 <ul>
 <li>This class has not variable field member and it has all method is static.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public class CdiUtils {

private CdiUtils() {
}

/**
 Validate the {@code Provider} to see if there is exactly one bean that matches the required type
 and qualifiers.

 @param <T> the required bean type
 @param pvd the {@code Provider}
 @return instance of {@code T}
 @throws NullPointerException if {@code pvd} is {@code null}
 @throws UnresolvedInstanceException if instance of {@code pvd} is not resolvable
 @since 3.0.0
 */
public static <T> T requireResolvable(Provider<T> pvd) {
    Objects.requireNonNull(pvd);

    try {
        return pvd.get();
    } catch (RuntimeException ex) {
        throw new UnresolvedInstanceException(ex);
    }
}

/**
 Obtains a child Instance for the given additional required qualifiers.

 @param <T> the required type.
 @param clazz a {@link java.lang.Class} representing the required type.
 @param qualifiers the additional required qualifiers.
 @return the child <code>Instance</code>
 @throws NullPointerException if any argument is {@code null} or if contains {@code null} in
 {@code qualifiers}.
 @throws UnresolvedInstanceException if passed two instances of the same non repeating qualifier
 type, or an instance of an annotation that is not a qualifier type.
 @throws IllegalStateException if the CDI container is already shutdown.
 @since 3.0.0
 */
public static <T> Instance<T> getInstance(Class<T> clazz, Annotation... qualifiers) {
    Objects.requireNonNull(clazz);
    Stream.of(Objects.requireNonNull(qualifiers)).forEach(Objects::requireNonNull);

    try {
        return CDI.current().select(clazz, qualifiers);
    } catch (IllegalArgumentException | IllegalStateException ex) {
        throw new UnresolvedInstanceException(ex);
    }
}

/**
 Provides a fully-constructed and injected instance of {@code T}.

 @param <T> the required type.
 @param clazz a {@link java.lang.Class} representing the required type.
 @param qualifiers the additional required qualifiers.
 @return instance of {@code T}.
 @throws NullPointerException if any argument is {@code null} or if contains {@code null} in
 {@code qualifiers}.
 @throws UnresolvedInstanceException if can't get a unique instance
 @since 3.0.0
 */
public static <T> T get(Class<T> clazz, Annotation... qualifiers) {
    try {
        return getInstance(clazz, qualifiers).get();
    } catch (NullPointerException | UnresolvedInstanceException ex) {
        throw ex;
    } catch (RuntimeException ex) {
        throw new UnresolvedInstanceException(ex);
    }
}

}
