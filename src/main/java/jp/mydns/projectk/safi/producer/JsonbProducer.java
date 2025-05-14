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
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 Producer of the {@link Jsonb}. Instances are created only once, reducing construction costs.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JsonbProducer {

/**
 Produce the {@code Jsonb}.

 @return the {@code Jsonb}
 @since 3.0.0
 */
Jsonb produce();

/**
 Close the produced {@code Jsonb} if disposed.

 @param jsonb the produced {@code Jsonb}
 @since 3.0.0
 */
void close(Jsonb jsonb);

/**
 Implements of the {@code JsonbProducer}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(JsonbProducer.class)
@ApplicationScoped
class Impl implements JsonbProducer {

@SuppressWarnings("unused")
Impl() {
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Produces
@ApplicationScoped
@Override
public Jsonb produce() {
    return JsonbBuilder.create();
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public void close(@Disposes Jsonb jsonb) {
    try {
        jsonb.close();
    } catch (Exception ex) {
        // Do nothing. Because not expected to occur the exception.
    }
}

}

}
