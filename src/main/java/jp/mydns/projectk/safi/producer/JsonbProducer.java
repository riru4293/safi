// Copyright 2025, Project-K
// SPDX-License-Identifier: BSD-2-Clause
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 * <i>Jakarta CDI</i> producer implementations for application-specific
 * {@link Jsonb}.
 *
 * <p>
 * This class provides CDI producers for the application-specific {@code Jsonb}.
 * Centralizing {@code Jsonb} instantiation ensures consistent usage across the
 * application.
 * 
 * <p>
 * Only a single instance is created, reducing construction overhead.
 * 
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
@ApplicationScoped
public class JsonbProducer {

    /**
     * Create a new instance of {@code JsonbProducer}.
     * 
     * <p>
     * This constructor is intended solely for CDI instantiation.
     * 
     * @since 3.0.0
     */
    public JsonbProducer() {
    }

    /**
     * Produce the {@code Jsonb}.
     * 
     * @return the {@code Jsonb}
     * @since 3.0.0
     */
    @Produces
    @ApplicationScoped
    public Jsonb produce() {
        /* Note:
            In the future, we may be able to create customized instances,
            such as by specifying the JSON serialization format.
        */
        return JsonbBuilder.create();
    }

    /**
     * Close the produced {@code Jsonb} if disposed.
     * 
     * @param jsonb the produced {@code Jsonb}
     * @since 3.0.0
     */
    public void close(@Disposes Jsonb jsonb) {
        try {
            jsonb.close();
        } catch (Exception ignore) {
            /* Note:
                While exceptions are not expected,
                they will be intentionally ignored to
                eliminate the possibility of unnecessary logging.
            */
        }
    }
}
