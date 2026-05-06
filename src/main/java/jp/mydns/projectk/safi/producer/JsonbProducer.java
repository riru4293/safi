// Copyright 2025, Project-K
// SPDX-License-Identifier: BSD-2-Clause
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 <i>Jakarta CDI</i> producer of the {@link Jsonb}.

 Instance is created only once, reducing construction costs.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface JsonbProducer
{
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
     Internal Implementation.

     @hidden
     */
    @Typed(JsonbProducer.class)
    @Dependent
    class Impl implements JsonbProducer
    {
        @SuppressWarnings("unused") // Note: To be called by CDI.
        Impl() {}

        @Produces
        @ApplicationScoped
        @Override
        public Jsonb produce()
        {
            return JsonbBuilder.create();
        }

        @Override
        public void close(@Disposes Jsonb jsonb)
        {
            try
            {
                jsonb.close();
            }
            catch (Exception ignore)
            {
                // Do nothing.
                // Note: No expected to occur the exception.
            }
        }
    }
}
