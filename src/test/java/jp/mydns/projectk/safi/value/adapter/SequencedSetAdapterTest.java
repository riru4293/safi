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
package jp.mydns.projectk.safi.value.adapter;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.LinkedHashSet;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

/**
 * Test of class SequencedSetAdapter.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
class SequencedSetAdapterTest {

    /**
     * Test of adaptToJson method, of class {@code SequencedMonthSetAdapter}.
     *
     * @since 3.0.0
     */
    @Test
    void testAdaptToJsonMonth() {
        List<Month> expect = List.of(Month.JUNE, Month.AUGUST, Month.DECEMBER, Month.APRIL, Month.NOVEMBER);

        var instance = new SequencedSetAdapter.SequencedMonthSetAdapter();

        @SuppressWarnings("SetReplaceableByEnumSet")
        var sequenced = new LinkedHashSet<>(expect);

        var result = instance.adaptToJson(sequenced);

        assertThat(result).containsExactlyElementsOf(expect);
    }

    /**
     * Test of adaptToJson method, of class {@code SequencedDayOfWeekSetAdapter}.
     *
     * @since 3.0.0
     */
    @Test
    void testAdaptToJsonDayOfWeek() {
        List<DayOfWeek> expect = List.of(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY, DayOfWeek.THURSDAY);

        var instance = new SequencedSetAdapter.SequencedDayOfWeekSetAdapter();

        @SuppressWarnings("SetReplaceableByEnumSet")
        var sequenced = new LinkedHashSet<>(expect);

        var result = instance.adaptToJson(sequenced);

        assertThat(result).containsExactlyElementsOf(expect);
    }

    /**
     * Test of adaptToJson method, of class {@code SequencedIntegerSetAdapter}.
     *
     * @since 3.0.0
     */
    @Test
    void testAdaptToJsonInteger() {
        List<Integer> expect = List.of(99, 1, 22, 2, 5, 100);

        var instance = new SequencedSetAdapter.SequencedIntegerSetAdapter();

        var sequenced = new LinkedHashSet<>(expect);

        var result = instance.adaptToJson(sequenced);

        assertThat(result).containsExactlyElementsOf(expect);
    }

    /**
     * Test of adaptFromJson method, of class {@code SequencedMonthSetAdapter}.
     *
     * @since 3.0.0
     */
    @Test
    void testAdaptFromJsonMonth() {
        List<Month> expect = List.of(Month.JUNE, Month.AUGUST, Month.DECEMBER, Month.APRIL, Month.NOVEMBER);

        var instance = new SequencedSetAdapter.SequencedMonthSetAdapter();

        var result = instance.adaptFromJson(expect);

        assertThat(result).containsExactlyElementsOf(expect);
    }

    /**
     * Test of adaptFromJson method, of class {@code SequencedDayOfWeekSetAdapter}.
     *
     * @since 3.0.0
     */
    @Test
    void testAdaptFromJsonDayOfWeek() {
        List<DayOfWeek> expect = List.of(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY, DayOfWeek.THURSDAY);

        var instance = new SequencedSetAdapter.SequencedDayOfWeekSetAdapter();

        var result = instance.adaptFromJson(expect);

        assertThat(result).containsExactlyElementsOf(expect);
    }

    /**
     * Test of adaptFromJson method, of class {@code SequencedIntegerSetAdapter}.
     *
     * @since 3.0.0
     */
    @Test
    void testAdaptFromJsonInteger() {
        List<Integer> expect = List.of(99, 1, 22, 2, 5, 100);

        var instance = new SequencedSetAdapter.SequencedIntegerSetAdapter();

        var result = instance.adaptFromJson(expect);

        assertThat(result).containsExactlyElementsOf(expect);
    }
}
