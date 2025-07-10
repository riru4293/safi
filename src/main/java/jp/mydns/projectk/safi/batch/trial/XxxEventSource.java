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
package jp.mydns.projectk.safi.batch.trial;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.concurrent.atomic.AtomicReference;
import jp.mydns.projectk.safi.batch.HeartbeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 An {@link Xxx} event is issued every 60 seconds. It is synchronized with the
 heartbeat service with a 15 second delay, starting when the heartbeat starts and ending when the
 heartbeat ends.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
public interface XxxEventSource {

/**
 Handler of the {@code HeartbeatService.Reset}. Reset counter with period and initial delay.

 @param nouse no use
 @since 3.0.0
 */
void handleReset(@Observes HeartbeatService.Reset nouse);

/**
 Handler of the {@code HeartbeatService.JustOneSecond}. It decrements a counter and ignite a
 {@link Xxx} event when the count reaches 0. The counter is then reset and wait counting down again.

 @param nouse no use
 @since 3.0.0
 */
void handleHeartbeat(@Observes HeartbeatService.JustOneSecond nouse);

/**
 Implements of the {@code XxxEventSource}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@Typed(XxxEventSource.class)
@ApplicationScoped
class Impl implements XxxEventSource {

private static final Integer INITIAL_DELAY_SEC = 15;
private static final Integer INTERVAL_SEC = 60;
private static final Integer INITIAL_REMAINING_SEC = INITIAL_DELAY_SEC + INTERVAL_SEC;
private final AtomicReference<Integer> remaining = new AtomicReference<>(INITIAL_REMAINING_SEC);

private final Provider<Event<Xxx>> ntfPvd;

@Inject
@SuppressWarnings("unused")
Impl(Provider<Event<Xxx>> ntfPvd) {
    this.ntfPvd = ntfPvd;
}

private static final Logger log = LoggerFactory.getLogger(Impl.class);

/**
 {@inheritDoc}

 @param nouse no use
 @since 3.0.0
 */
@Override
public void handleReset(@Observes HeartbeatService.Reset nouse) {
    //Note: Reset counter with period + initial delay.
    remaining.set(INITIAL_REMAINING_SEC);

    log.debug("Reset countdown for ignite XXX.");
}

/**
 {@inheritDoc}

 @param nouse no use
 @since 3.0.0
 */
@Override
public void handleHeartbeat(@Observes HeartbeatService.JustOneSecond nouse) {

    // Note: Count down and then check for ignition.
    if (1 > remaining.updateAndGet(c -> c - 1)) {

        // Reset counter.
        remaining.set(INTERVAL_SEC);

        // Ignite an event.
        ntfPvd.get().fire(new Xxx());

        log.debug("Ignite a XXX event.");
    }
}

}

/**
 A XXX Event.
 <p>
 Implementation requirements.
 <ul>
 <li>This class is immutable and thread-safe.</li>
 </ul>

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
static class Xxx {

@SuppressWarnings("unused")
private Xxx() {
}

/**
 Returns a string representation.

 @return string representation.
 @since 3.0.0
 */
@Override
public String toString() {
    return "XXX event";
}

}

}
