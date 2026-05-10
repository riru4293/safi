// Copyright 2025, Project-K
// SPDX-License-Identifier: BSD-2-Clause
package jp.mydns.projectk.safi.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 */
@ApplicationScoped
public class JsonbProducer {

    private static final Logger log = LoggerFactory.getLogger(JsonbProducer.class);

    /**
     * Create a new instance of {@code JsonbProducer}.
     * 
     * <p>
     * This constructor is intended solely for CDI instantiation.
     */
    public JsonbProducer() {
    }

    /**
     * Produce the {@code Jsonb}.
     * 
     * <p>
     * Currently, the generated {@code Jsonb} uses default settings,
     * but in the future, it may use custom settings such as the JSON serialization
     * format.
     * 
     * @return the {@code Jsonb}
     */
    @Produces
    @ApplicationScoped
    public Jsonb produce() {
        return JsonbBuilder.create();
    }

    /**
     * Close the produced {@code Jsonb} if disposed.
     *
     * <p>
     * Exceptions raised by this method are intentionally ignored for the following
     * reasons:
     * <ul>
     * <li>This method is called when the application terminates, and even if
     * resource
     * release fails, the resources will inevitably be released.</li>
     * <li>By not throwing the exception, it does not affect other processes.</li>
     * </ul>
     *
     * @param jsonb the produced {@code Jsonb}
     */
    public void close(@Disposes Jsonb jsonb) {
        try {
            jsonb.close();
        } catch (Exception ignore) {
            log.trace("Failed to close Jsonb", ignore);
        }
    }
}
