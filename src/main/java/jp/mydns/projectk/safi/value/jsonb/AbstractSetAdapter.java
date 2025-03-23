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
package jp.mydns.projectk.safi.value.jsonb;

import jakarta.json.bind.adapter.JsonbAdapter;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.SequencedSet;
import static java.util.stream.Collectors.toCollection;

/**
 * A custom <i>Jakarta JSON-B</i> adapter used to maintain ordering in a {@code Set}.
 *
 * @param <T> value type
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public abstract class AbstractSetAdapter<T> implements JsonbAdapter<SequencedSet<T>, List<T>> {

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     * @throws NullPointerException if {@code s} is {@code null}
     */
    @Override
    public List<T> adaptToJson(SequencedSet<T> s) {
        return Objects.requireNonNull(s).stream().sequential().toList();
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     * @throws NullPointerException if {@code l} is {@code null}
     */
    @Override
    public SequencedSet<T> adaptFromJson(List<T> l) {
        LinkedHashSet<T> s = Objects.requireNonNull(l).stream().sequential()
            .collect(toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSequencedSet(s);
    }

    /**
     * Implements of the {@code AbstractSetAdapter<Month>}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    public static class SetMonthAdapter extends AbstractSetAdapter<Month> {
    }

    /**
     * Implements of the {@code AbstractSetAdapter<DayOfWeek>}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    public static class SetDayOfWeekAdapter extends AbstractSetAdapter<DayOfWeek> {
    }

    /**
     * Implements of the {@code AbstractSetAdapter<Integer>}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    public static class SetIntegerAdapter extends AbstractSetAdapter<Integer> {
    }
}
