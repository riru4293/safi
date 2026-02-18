/*
 * Copyright 2025, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
import jakarta.enterprise.concurrent.ManagedScheduledExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Predicate.not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Provides a 1‑second heartbeat notification as CDI events.

 <p>
 This service publishes a {@link JustOneSecond} event every second using a
 {@link ManagedScheduledExecutorService}. An initial delay of 10 seconds is applied only to the
 first automatic startup triggered by the CDI Startup event. The service starts automatically at
 application startup and stops at shutdown. It can also be started and stopped manually.

 <p>
 Heartbeat events are fired from a managed executor thread, and a request context is activated for
 each publication. Lifecycle operations ({@link start()}, {@link stop()}, {@link isRunning()}) are
 synchronized and thread‑safe. A {@link Reset} event is published whenever the service is
 (re)started. All events are immutable and thread‑safe.

 <p>
 Typical usage includes second‑level polling. For example, a 3‑second periodic task can be
 implemented by counting three {@code JustOneSecond} events.

 <p>
 Scheduling uses {@link ManagedScheduledExecutorService#scheduleAtFixedRate scheduleAtFixedRate} to
 minimize drift. Slow handlers may delay the next tick, but no backlog is accumulated.

 <p>
 Implementation requirements.
 <ul>
     <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface HeartbeatService {

    /**
     Fires a {@link JustOneSecond} event.
     <p>
     This method is invoked periodically by the internal scheduler. The request context is activated
     for the duration of this method.

     <h4>Receiver Example</h4>
     <pre>{@code
     void onHeartbeat(@Observes JustOneSecond e) {
         // handle tick
     }
     }</pre>

     @since 3.0.0
     */
    @ActivateRequestContext
    void fire();

    /**
     Returns whether the heartbeat scheduler is currently active.

     @return {@code true} if running, otherwise {@code false}.
     @since 3.0.0
     */
    boolean isRunning();

    /**
     Starts the heartbeat scheduler. If already running, the scheduler is stopped and restarted.
     <p>
     When restarted, a {@link Reset} event is fired before scheduling the periodic heartbeat. This
     method is also invoked automatically when the CDI container fires the {@link Startup} event.

     @since 3.0.0
     */
    @ActivateRequestContext
    void start();

    /**
     Stops the heartbeat scheduler. If the scheduler is not running, this method does nothing.
     <p>
     This method is also invoked automatically when the CDI container fires the {@link Shutdown}
     event.

     @since 3.0.0
     */
    void stop();

    /**
     @hidden
     */
    /* Define a scheduled Executor. */
    @ManagedScheduledExecutorDefinition(
        name = "java:module/concurrent/Heartbeat", // The JNDI name of the Executor.
        context = "java:module/concurrent/HeartbeatContext" // The JNDI name of the context the executor will use.
    )
    /* Defines the context to be propagated to threads associated with the Executor. */
    @ContextServiceDefinition(
        name = "java:module/concurrent/HeartbeatContext", // The JNDI name of the context.
        propagated = {APPLICATION} // Context type = Inherits CDI, Resource, and Transaction.
    )
    @Typed(HeartbeatService.class)
    @ApplicationScoped
    class Impl implements HeartbeatService
    {
        private static final Logger log = LoggerFactory.getLogger(Impl.class);

        private static final long INTERVAL_SEC = 1;
        private static final long INITIAL_DELAY_SEC = 10;

        private final Object lock = new Object();

        private final Provider<HeartbeatService> selfPvd;   // Note: Self-injection for execution via CDI proxy.
        private final Provider<Event<Reset>> resetPvd;      // The event that notify start heartbeat.
        private final Provider<Event<JustOneSecond>> ntfPvd;// The event that notify per 1 second.

        private ManagedScheduledExecutorService scheduler;  // Note: Automatically looked up and set from JNDI.
        private ScheduledFuture<?> scheduledTask;           // Note: Set when the start method is executed.

        @Inject
        @SuppressWarnings("unused") // Note: To be called by CDI.
        Impl(Provider<HeartbeatService> selfPvd,
             Provider<Event<Reset>> resetPvd,
             Provider<Event<JustOneSecond>> ntfPvd)
        {
            this.selfPvd = selfPvd;
            this.resetPvd = resetPvd;
            this.ntfPvd = ntfPvd;
        }

        @Resource(lookup = "java:module/concurrent/Heartbeat",
                  name = "java:module/concurrent/env/HeartbeatRef")
        @SuppressWarnings("unused") // Note: To be called by CDI.
        void setScheduler(ManagedScheduledExecutorService scheduler)
        {
            this.scheduler = scheduler;
        }

        @ActivateRequestContext
        @Override
        public void fire()
        {
            ntfPvd.get().fire(new JustOneSecond());
        }

        @ActivateRequestContext
        @SuppressWarnings("unused") // Note: To be called by CDI.
        void start(@Observes Startup startup)
        {
            start();
        }

        @ActivateRequestContext
        @Override
        public void start()
        {
            synchronized (lock)
            {
                stop();

                log.info("Start the heartbeat.");

                resetPvd.get().fire(new Reset());

                scheduledTask = scheduler.scheduleAtFixedRate(
                    selfPvd.get()::fire, INITIAL_DELAY_SEC, INTERVAL_SEC, SECONDS);
            }
        }

        @SuppressWarnings("unused") // Note: To be called by CDI.
        void stop(@Observes Shutdown shutdown)
        {
            stop();
        }

        @Override
        public void stop()
        {
            synchronized (lock)
            {
                Optional.ofNullable(scheduledTask)
                    .filter(this::cancelScheduledTaskExecution)
                    .ifPresent(t -> log.info("Stop the heartbeat."));
            }
        }

        @Override
        public boolean isRunning()
        {
            synchronized (lock)
            {
                return Optional.ofNullable(scheduledTask)
                    .filter(not(Future::isCancelled))
                    .isPresent();
            }
        }

        private boolean cancelScheduledTaskExecution(Future<?> scheduledTask)
        {
            return scheduledTask.cancel(false);
        }

    }

    /**
     One‑second heartbeat event.

     @author riru
     @version 3.0.0
     @since 3.0.0
     */
    static class JustOneSecond {
        @SuppressWarnings("unused") // Note: To be called by CDI.
        private JustOneSecond() {}
    }

    /**
     Event indicating that the heartbeat service has been started or restarted.

     @author riru
     @version 3.0.0
     @since 3.0.0
     */
    static class Reset {
        @SuppressWarnings("unused")  // Note: To be called by CDI.
        private Reset() {}
    }

}
