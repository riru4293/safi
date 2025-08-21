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
package jp.mydns.projectk.safi.batch;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ContextServiceDefinition;
import static jakarta.enterprise.concurrent.ContextServiceDefinition.APPLICATION;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Predicate.not;
import jp.mydns.projectk.safi.service.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 Notifies heartbeat events. It starts automatically when the application starts. The notification
 interval is 1 second, and the start delay is 10 seconds. It can also be stopped and started
 manually.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface HeartbeatService {

/**
 Notifies a heartbeat as CDI event. Along with the notification, enable request context.
 <p>
 <h4>Receiver implementation.</h4>
 To act as a handler for this event, write a method in your CDI managed class that looks like this:
 {@code void someMethod(@}{@link Observes} {@link JustOneSecond} {@code ntf)}.

 @since 3.0.0
 */
@ActivateRequestContext
void fire();

/**
 Returns {@code true} if this service is running.

 @return {@code true} if running, otherwise {@code false}.
 @since 3.0.0
 */
boolean isRunning();

/**
 Start this service. Restart this service if already started.
 <p>
 Enable {@link RequestScoped} by calling the {@link #fire} method via our own CDI instance.

 @since 3.0.0
 */
@ActivateRequestContext
void start();

/**
 Stop the heartbeat. Do nothing if not started yet.

 @since 3.0.0
 */
void stop();

/**
 Implements of the {@code HeartbeatService}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
/* Define a scheduled Executor. */
@ManagedScheduledExecutorDefinition(
    /* The JNDI name of the Executor. */
    name = "java:module/concurrent/Heartbeat",
    /* The JNDI name of the context the executor will use. */
    context = "java:module/concurrent/HeartbeatContext"
)
/* Defines the context to be propagated to threads associated with the Executor. */
@ContextServiceDefinition(
    /* The JNDI name of the context. */
    name = "java:module/concurrent/HeartbeatContext",
    /* Set the context type to propagate. */
    /* - APPLICATION: Inherits CDI, Resource, and Transaction. */
    propagated = {APPLICATION}
)
@Typed(HeartbeatService.class)
@ApplicationScoped
class Impl implements HeartbeatService {

private static final Logger log = LoggerFactory.getLogger(Impl.class);

private static final long INTERVAL_SEC = 1;
private static final long INITIAL_DELAY_SEC = 10;

private final Object lock = new Object();

private final Provider<HeartbeatService> selfPvd;
private final Provider<Event<Reset>> resetPvd;
private final Provider<Event<JustOneSecond>> ntfPvd;
private final TimeService timeSvc;

private ManagedScheduledExecutorService scheduler;  // Note: Automatically looked up and set from JNDI.

private ScheduledFuture<?> scheduledTask;           // Note: Set when the start method is executed.

@Inject
@SuppressWarnings("unused")
Impl(
    Provider<HeartbeatService> selfPvd, Provider<Event<Reset>> resetPvd,
    Provider<Event<JustOneSecond>> ntfPvd, TimeService timeSvc) {

    this.selfPvd = selfPvd;
    this.resetPvd = resetPvd;
    this.ntfPvd = ntfPvd;
    this.timeSvc = timeSvc;
}

@Resource(lookup = "java:module/concurrent/Heartbeat",
          name = "java:module/concurrent/env/HeartbeatRef")
@SuppressWarnings("unused")
void setScheduler(ManagedScheduledExecutorService scheduler) {
    this.scheduler = scheduler;
}

/**
 Notifies {@link JustOneSecond} as a CDI event. Along with the notification, enable request context.

 @since 3.0.0
 */
@ActivateRequestContext
@Override
public void fire() {
    JustOneSecond heartbeat = new JustOneSecond(timeSvc.getExactlyLocalNow());

    log.debug("Notify heartbeat. {}", heartbeat.getHappened());

    ntfPvd.get().fire(heartbeat);
}

/**
 Start the heartbeat.
 <p>
 Handles the {@code Startup} event fired by the CDI container during application initialization,
 i.e. it is called once, when the application starts.

 @param startup the {@code Startup}.
 @since 3.0.0
 */
@ActivateRequestContext
@SuppressWarnings("unused")
void start(@Observes Startup startup) {
    start();
}

/**
 Start the heartbeat. Restart if already started.
 <p>
 Enable {@link RequestScoped} by calling the {@link #fire} method via our own CDI instance.
 <p>
 Send the {@link Reset} as CDI event when previous start.

 @since 3.0.0
 */
@ActivateRequestContext
@Override
public void start() {
    synchronized (lock) {
        stop();

        log.info("Start the heartbeat.");

        resetPvd.get().fire(new Reset(timeSvc.getExactlyLocalNow()));

        scheduledTask = scheduler.scheduleAtFixedRate(
            selfPvd.get()::fire, INITIAL_DELAY_SEC, INTERVAL_SEC, SECONDS);
    }
}

/**
 Stop the heartbeat.
 <p>
 Handles the {@code Startup} event fired by the CDI container during application shutdown, i.e. it
 is called once, when the application shutdown.

 @param shutdown the {@code Shutdown}.
 @since 3.0.0
 */
@SuppressWarnings("unused")
void stop(@Observes Shutdown shutdown) {
    stop();
}

/**
 Stop the heartbeat. Do nothing if not started yet.

 @since 3.0.0
 */
@Override
public void stop() {
    synchronized (lock) {
        Optional.ofNullable(scheduledTask).filter(this::cancelScheduledTaskExecution)
            .ifPresent(t -> log.info("Stop the heartbeat."));
    }
}

/**
 Returns {@code true} if the heart is running.

 @return {@code true} if running, otherwise {@code false}.
 @since 3.0.0
 */
@Override
public boolean isRunning() {
    synchronized (lock) {
        return Optional.ofNullable(scheduledTask).filter(not(Future::isCancelled)).isPresent();
    }
}

private boolean cancelScheduledTaskExecution(Future<?> scheduledTask) {
    return scheduledTask.cancel(false);
}

}

/**
 Events notified at fixed intervals.
 <p>
 Implementation requirements.
 <ul>
 <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
static abstract class PeriodicEvent {

protected final LocalDateTime happened;

/**
 Construct with happened time.

 @param happened happened time
 @since 3.0.0
 */
protected PeriodicEvent(LocalDateTime happened) {
    this.happened = Objects.requireNonNull(happened);
}

/**
 Get the time when this happened.

 @return happened time.
 @since 3.0.0
 */
public LocalDateTime getHappened() {
    return happened;
}

}

/**
 One second periodic event.
 <p>
 Implementation requirements.
 <ul>
 <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
static class JustOneSecond extends PeriodicEvent {

private JustOneSecond(LocalDateTime happened) {
    super(happened);
}

/**
 Returns a string representation.

 @return string representation.
 @since 3.0.0
 */
@Override
public String toString() {
    return "JustOneSecond{" + "happened=" + happened + '}';
}

}

/**
 Event that reset the heartbeat.
 <p>
 Implementation requirements.
 <ul>
 <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
static class Reset extends PeriodicEvent {

private Reset(LocalDateTime happened) {
    super(happened);
}

/**
 Returns a string representation.

 @return string representation.
 @since 3.0.0
 */
@Override
public String toString() {
    return "HeartBeat.Reset{" + "happened=" + happened + '}';
}

}

}
