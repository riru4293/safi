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

import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.TemporalAdjusters.lastInMonth;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;

/**
 * Schedule resolver.
 *
 * @implSpec This class is immutable and thread-safe.
 * @author riru
 * @since 9.0
 */
class ScheduleResolver {

    private static final String KEY_DATETIME = "datetime";

    private final WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
    private final LocalDateTime beginTime;
    private final LocalDateTime endTime;

    /**
     * Constructor.
     *
     * @param beginTime scheduling begin time
     * @param endTime scheduling end time
     * @throws NullPointerException if any argument is {@code null}
     */
    ScheduleResolver(LocalDateTime beginTime, LocalDateTime endTime) {
        this.beginTime = Objects.requireNonNull(beginTime);
        this.endTime = Objects.requireNonNull(endTime);
    }

    /**
     * Resolve schedules from definitions.
     *
     * @param defs schedule definitions
     * @return schedules
     * @throws IllegalArgumentException if {@code defs} is incorrect
     * @throws NullPointerException if {@code defs} is {@code null}
     */
    public List<Entry<LocalDateTime, SchedefValue>> resolve(List<SchedefValue> defs) {
        Objects.requireNonNull(defs);

        try {
            Map<String, List<SchedefValue>> defsPerJobdef
                    = defs.stream().collect(groupingBy(SchedefValue::getJobdefId));
            return defsPerJobdef.values().stream().flatMap(this::toSchedules).toList();
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Incorrect schedule definition!!", ex);
        }
    }

    private Stream<Entry<LocalDateTime, SchedefValue>> toSchedules(List<SchedefValue> defs) {

        Map<LocalDateTime, SchedefValue> sches = defs.stream()
                .filter(not(this::isBan)).flatMap(this::toScheduleStream)
                .collect(toMap(Entry::getKey, Entry::getValue, winsHighPriority()));

        defs.stream().filter(this::isBan).map(this::toBanPred).sequential()
                .forEach(sches.entrySet()::removeIf);

        return sches.entrySet().stream();
    }

    private Stream<Entry<LocalDateTime, SchedefValue>> toScheduleStream(SchedefValue def) {
        Function<LocalDateTime, Entry<LocalDateTime, SchedefValue>> toEntry
                = d -> Map.entry(d, def);
        JsonObject defVal = def.getValue();

        return switch (def.getType()) {
            case "DAILY" ->
                toDailyScheduleStream(defVal).map(toEntry);

            case "WEEKLY" ->
                toWeeklyScheduleStream(defVal).map(toEntry);

            case "MONTHLY_DAYS" ->
                toMonthlyDaysScheduleStream(defVal).map(toEntry);

            case "MONTHLY_WEEKDAYS" ->
                toMonthlyWeekdaysScheduleStream(defVal).map(toEntry);

            case "ONCE" ->
                toOnceScheduleStream(defVal).map(toEntry);

            default ->
                throw new IllegalArgumentException("Unexpected schedule type!!");
        };
    }

    private Stream<LocalDateTime> toMonthlyWeekdaysScheduleStream(JsonObject defVal) {
        LocalDateTime refTime = LocalDateTime.parse(defVal.getString(KEY_DATETIME));

        Set<DayOfWeek> weekdays = defVal.getJsonArray("weekdays").stream()
                .map(JsonValueUtils::toString).map(DayOfWeek::valueOf)
                .collect(toUnmodifiableSet());

        Set<Integer> weeks = defVal.getJsonArray("weeks").stream()
                .map(JsonNumber.class::cast).map(JsonNumber::intValueExact)
                .collect(toUnmodifiableSet());

        int maxWeek = weeks.stream().mapToInt(Integer::intValue).max().orElseGet(() -> 0);

        Set<Month> months = defVal.getJsonArray("months").stream()
                .map(JsonValueUtils::toString).map(Month::valueOf)
                .collect(toUnmodifiableSet());

        Predicate<LocalDate> matchOfWeek = d -> {
            LocalDate lastDayOfMonth = d.with(lastInMonth(d.getDayOfWeek()));
            int week = d.get(weekFields.weekOfMonth());
            return weeks.contains(week)
                    || d.equals(lastDayOfMonth) && maxWeek >= week;
        };

        return dayStream(refTime, endTime)
                .filter(p(months::contains, LocalDateTime::getMonth))
                .filter(p(weekdays::contains, LocalDateTime::getDayOfWeek))
                .filter(p(matchOfWeek, LocalDateTime::toLocalDate))
                .filter(this::withinRange);
    }

    private Stream<LocalDateTime> toMonthlyDaysScheduleStream(JsonObject defVal) {
        LocalDateTime refTime = LocalDateTime.parse(defVal.getString(KEY_DATETIME));

        Set<Integer> days = defVal.getJsonArray("days").stream()
                .map(JsonNumber.class::cast).map(JsonNumber::intValueExact)
                .collect(toUnmodifiableSet());

        int maxDay = days.stream().mapToInt(Integer::intValue).max().orElseGet(() -> 0);

        Set<Month> months = defVal.getJsonArray("months").stream()
                .map(JsonValueUtils::toString).map(Month::valueOf)
                .collect(toUnmodifiableSet());

        Predicate<LocalDate> matchOfDay = d -> {
            int day = d.get(ChronoField.DAY_OF_MONTH);
            return days.contains(day) || d.lengthOfMonth() == day && maxDay >= day;
        };

        return dayStream(refTime, endTime)
                .filter(p(months::contains, LocalDateTime::getMonth))
                .filter(this::withinRange)
                .filter(p(matchOfDay, LocalDateTime::toLocalDate));
    }

    private Stream<LocalDateTime> toWeeklyScheduleStream(JsonObject defVal) {
        LocalDateTime refTime = LocalDateTime.parse(defVal.getString(KEY_DATETIME));

        int interval = defVal.getInt("interval");

        Set<DayOfWeek> weekdays = defVal.getJsonArray("weekdays").stream()
                .map(JsonValueUtils::toString).map(DayOfWeek::valueOf)
                .collect(toUnmodifiableSet());

        Predicate<LocalDateTime> matchOfInterval
                = d -> WEEKS.between(refTime, d) % interval == 0;

        return dayStream(refTime, endTime)
                .filter(p(weekdays::contains, LocalDateTime::getDayOfWeek))
                .filter(this::withinRange).filter(matchOfInterval);
    }

    private Stream<LocalDateTime> toOnceScheduleStream(JsonObject defVal) {
        return Stream.of(defVal).map(j -> j.getString(KEY_DATETIME))
                .map(LocalDateTime::parse).filter(this::withinRange);
    }

    private Stream<LocalDateTime> toDailyScheduleStream(JsonObject defVal) {
        LocalDateTime refTime = LocalDateTime.parse(defVal.getString(KEY_DATETIME));
        int interval = defVal.getInt("interval");
        Predicate<LocalDateTime> matchOfInterval
                = d -> DAYS.between(refTime, d) % interval == 0;

        return dayStream(refTime, endTime)
                .filter(this::withinRange).filter(matchOfInterval);
    }

    private Predicate<Entry<LocalDateTime, SchedefValue>> toBanPred(SchedefValue ban) {
        JsonObject defVal = ban.getValue();
        LocalDateTime banFrom = LocalDateTime.parse(defVal.getString("from"));
        LocalDateTime banTo = LocalDateTime.parse(defVal.getString("to"));

        return s -> moreHighPriotiry(s.getValue()).test(ban)
                && !s.getKey().isBefore(banFrom)
                && !s.getKey().isAfter(banTo);
    }

    private Stream<LocalDateTime> dayStream(LocalDateTime from, LocalDateTime to) {
        return LongStream.rangeClosed(0, DAYS.between(from, to)).mapToObj(from::plusDays);
    }

    private boolean withinRange(LocalDateTime dt) {
        return !beginTime.isAfter(dt) && !endTime.isBefore(dt);
    }

    private Predicate<SchedefValue> moreHighPriotiry(SchedefValue ref) {
        return target -> comparing(SchedefValue::getPriority)
                .thenComparing(SchedefValue::getId).compare(ref, target) < 0;
    }

    private BinaryOperator<SchedefValue> winsHighPriority() {
        return (ref, target) -> moreHighPriotiry(ref).test(target) ? target : ref;
    }

    private boolean isBan(SchedefValue s) {
        return s.getType().equals("BAN");
    }
}
