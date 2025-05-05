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
import java.util.Objects;
import jp.mydns.projectk.safi.exception.trial.PublishableIllegalStateException;

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
 Validate the {@code Instance} to see if there is exactly one bean that matches the required type
 and qualifiers.

 @param <T> the required bean type
 @param inst the {@code Instance}
 @return instance of {@code T}
 @throws NullPointerException if {@code inst} is {@code null}
 @throws PublishableIllegalStateException if {@code inst} is not resolvable
 @since 3.0.0
 */
public static <T> T requireResolvable(Instance<T> inst) {
    Objects.requireNonNull(inst);

    try {
        return inst.get();
    } catch (RuntimeException ex) {
        throw new PublishableIllegalStateException(ex);
    }
}

}
