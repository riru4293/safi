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
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jp.mydns.projectk.safi.SafiLimited;

/**
 Producer of the {@link ValidatorFactory}. Instances are created only once, reducing construction
 costs.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface ValidatorFactoryProducer {

/**
 Produce the {@code ValidatorFactory}.

 @return the {@code ValidatorFactory}
 @since 3.0.0
 */
ValidatorFactory produce();

/**
 Close the produced {@code ValidatorFactory} if disposed.

 @param vf the produced {@code ValidatorFactory}
 @since 3.0.0
 */
void close(ValidatorFactory vf);

/**
 Implements of the {@code ValidatorFactoryProducer}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(ValidatorFactoryProducer.class)
@Dependent
class Impl implements ValidatorFactoryProducer {

@SuppressWarnings("unused")
Impl() {
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Produces
@SafiLimited
@ApplicationScoped
@Override
public ValidatorFactory produce() {
    return Validation.buildDefaultValidatorFactory();
}

/**
 {@inheritDoc}

 @since 3.0.0
 */
@Override
public void close(@Disposes @SafiLimited ValidatorFactory vf) {
    vf.close();
}

}

}
