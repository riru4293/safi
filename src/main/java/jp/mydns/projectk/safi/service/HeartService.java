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
package jp.mydns.projectk.safi.service;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ContextServiceDefinition;
import static jakarta.enterprise.concurrent.ContextServiceDefinition.APPLICATION;
import static jakarta.enterprise.concurrent.ContextServiceDefinition.SECURITY;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Predicate.not;

/**
 * Notify heartbeat event. It starts automatically when the application starts. Notification interval can be customized
 * by configuration. Can also stop and start manually.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ManagedScheduledExecutorDefinition(name = "java:module/concurrent/HeartBeat", context = "java:module/concurrent/HeartBeatContext")
@ContextServiceDefinition(name = "java:module/concurrent/HeartBeatContext", propagated = {SECURITY, APPLICATION})
@ApplicationScoped
public class HeartService {

    private static final long INTERVAL = Duration.ofSeconds(1).toSeconds();

    private static final Logger LOGGER = System.getLogger(HeartService.class.getName());

    private final Object lock = new Object();

    @Resource(lookup = "java:module/concurrent/HeartBeat", name = "java:module/concurrent/env/HeartBeatRef")
    private ManagedScheduledExecutorService scheduler;

    private ScheduledFuture<?> scheduled;

    @Inject
    private HeartService self;

    @Inject
    private Event<Heartbeat> notification;

    @Inject
    private RealTimeService realTimeSvc;

    /**
     * Notifies {@link Heartbeat} as a CDI event. Along with the notification, enable request context.
     *
     * @since 1.0.0
     */
    @ActivateRequestContext
    public void fire() {
        Heartbeat hb = new Heartbeat(realTimeSvc.getLocalNow());
        LOGGER.log(DEBUG, "Notify heartbeat. %s".formatted(hb));
        notification.fire(hb);
    }

    /**
     * Start the heart. For startup events only.
     *
     * @param startup the {@code Startup}
     * @since 1.0.0
     */
    @ActivateRequestContext
    public void start(@Observes Startup startup) {
        start();
    }

    /**
     * Start the heart. Restart if already started.
     *
     * @since 1.0.0
     */
    @ActivateRequestContext
    public void start() {
        synchronized (lock) {
            stop();
            LOGGER.log(INFO, "Start the heart with a beat interval of %d seconds.".formatted(INTERVAL));
            scheduled = scheduler.scheduleAtFixedRate(self::fire, INTERVAL, INTERVAL, SECONDS);
        }
    }

    /**
     * Stop the heart. For shutdown events only.
     *
     * @param shutdown the {@code Shutdown}
     * @since 1.0.0
     */
    public void stop(@Observes Shutdown shutdown) {
        stop();
    }

    /**
     * Stop the heart. Do nothing if not started yet.
     *
     * @since 1.0.0
     */
    public void stop() {
        synchronized (lock) {
            Optional.ofNullable(scheduled).filter(this::cancelScheduledTaskExecution)
                    .ifPresent(t -> LOGGER.log(INFO, "Stop the heart."));
        }
    }

    private boolean cancelScheduledTaskExecution(Future<?> scheduledTask) {
        return scheduledTask.cancel(false);
    }

    /**
     * Returns {@code true} if the heart is running.
     *
     * @return {@code true} if running, otherwise {@code false}.
     * @since 1.0.0
     */
    public boolean isRunning() {
        synchronized (lock) {
            return Optional.ofNullable(scheduled).filter(not(Future::isCancelled)).isPresent();
        }
    }

    /**
     * Events notified at fixed intervals.
     *
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * </ul>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class Heartbeat {

        private final LocalDateTime happened;

        Heartbeat(LocalDateTime time) {
            this.happened = time;
        }

        /**
         * Get the time when this happened.
         *
         * @return happened time
         * @since 1.0.0
         */
        public LocalDateTime getHappened() {
            return happened;
        }

        /**
         * Returns a string representation.
         *
         * @return string representation
         * @since 1.0.0
         */
        @Override
        public String toString() {
            return "Heartbeat{" + "happened=" + happened + '}';
        }
    }
}
