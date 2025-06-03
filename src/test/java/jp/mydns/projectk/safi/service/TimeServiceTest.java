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
package jp.mydns.projectk.safi.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 Test of class {@code TimeService}.

 @author riru
 @version 3.0.0
 @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class TimeServiceTest {

/**
 Test of getOffsetNow method.

 @param confSvc it is mock
 @since 3.0.0
 */
@Test
void testGetOffsetNow(@Mock ConfigService confSvc) {
    var instance = new TimeService.Impl(confSvc);

    var pre = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    var result = instance.getOffsetNow();
    var post = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    assertThat(result).isBetween(pre, post);
}

/**
 Test of getLocalNow method.

 @param confSvc it is mock
 @since 3.0.0
 */
@Test
void testGetLocalNow(@Mock ConfigService confSvc) {
    var instance = new TimeService.Impl(confSvc);

    var pre = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
    var result = instance.getLocalNow();
    var post = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);

    assertThat(result).isBetween(pre, post);
}

/**
 Test of getExactlyOffsetNow method.

 @param confSvc it is mock
 @since 3.0.0
 */
@Test
void testGetExactlyOffsetNow(@Mock ConfigService confSvc) {
    var instance = new TimeService.Impl(confSvc);

    var pre = OffsetDateTime.now();
    var result = instance.getExactlyOffsetNow();
    var post = OffsetDateTime.now();

    assertThat(result).isBetween(pre, post);
}

/**
 Test of getExactlyLocalNow method.

 @param confSvc it is mock
 @since 3.0.0
 */
@Test
void testGetExactlyLocalNow(@Mock ConfigService confSvc) {
    var instance = new TimeService.Impl(confSvc);

    var pre = LocalDateTime.now(ZoneOffset.UTC);
    var result = instance.getExactlyLocalNow();
    var post = LocalDateTime.now(ZoneOffset.UTC);

    assertThat(result).isBetween(pre, post);
}

}
