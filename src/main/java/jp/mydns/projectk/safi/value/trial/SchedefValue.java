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
package jp.mydns.projectk.safi.value.trial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import jp.mydns.projectk.safi.constant.SchedefKing;
import jp.mydns.projectk.safi.value.NamedValue;

/**
 * Definition for <i>Job</i> scheduling.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This and JSON can be converted bidirectionally.</li>
 * </ul>
 *
 * <a href="{@docRoot}/../schemas/schedef.schema.json">Json schema is here</a>
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
//@JsonbTypeDeserializer(SchedefValue.Deserializer.class)
@Schema(name = "Schedef", description = "Definition for Job scheduling.")
public interface SchedefValue extends NamedValue {

    interface Config {

        SchedefKing getKind();

        interface Daily extends Config {

            OffsetDateTime getDateTime();

            Integer getInterval();
        }

        interface Weekly extends Config {

            List<DayOfWeek> getWeekDays();

            OffsetDateTime getDateTime();

            Integer getInterval();
        }

        interface MonthlyDays extends Config {

            List<Month> getMonths();

            List<Integer> getDays();

            OffsetDateTime getDateTime();
        }

        interface MonthlyWeekDays extends Config {

            List<Month> getMonths();

            Integer getWeeks();

            List<DayOfWeek> getWeekDays();

            OffsetDateTime getDateTime();
        }

        interface Once extends Config {

            OffsetDateTime getDateTime();
        }

        interface Cancel extends Config {

            OffsetDateTime getFrom();

            OffsetDateTime getTo();
        }
    }

    /**
     * Get schedule definition id.
     *
     * @return schedule definition id
     * @since 3.0.0
     */
    @NotNull
    @Size(min = 1, max = 36)
    @Schema(description = "Schedule definition id.", example = "test")
    String getId();

    /**
     * Get schedule configuration.
     *
     * @return schedule configuration
     * @since 3.0.0
     */
    @Schema(description = "Schedule configuration.", example
            = "{\"type\": \"DAILY\", \"interval\": 1, \"datetime\": \"2700-10-10T07:09:42Z\"}")
    @NotNull(groups = {Default.class})
    JsonObject getConfig();

//    private final String id;
//    private final String jobdefId;
//    private final ValidityPeriodValue validityPeriod;
//    private final String priority;
//    private final SchedefKing type;
//    private final String name;
//    private final JsonObject value;
//
//    /**
//     * Constructor.
//     *
//     * @param entity the {@code MSchedef}
//     * @throws NullPointerException if {@code entity} is {@code null}
//     * @throws IllegalArgumentException if schedule definition type does not exist
//     * @since 3.0.0
//     */
//    public SchedefValue(SchedefEntity entity) {
//        JsonValue rawType = entity.getValue().unwrap().asJsonObject().get("type");
//        if (rawType == null || rawType.getValueType() != JsonValue.ValueType.STRING) {
//            throw new IllegalArgumentException("Schedule definition type does not exist.");
//        }
//        this.id = entity.getId();
//        this.priority = entity.getPriority();
//        this.value = entity.getValue();
//        this.type = JsonString.class.cast(rawType).getString();
//        this.jobdefId = entity.getJobdefId();
//    }
//
//    /**
//     * Get schedule definition id.
//     *
//     * @return schedule definition id
//     * @since 3.0.0
//     */
//    public String getId() {
//        return id;
//    }
//
//    /**
//     * Get job definition id.
//     *
//     * @return job definition id
//     * @since 3.0.0
//     */
//    public String getJobdefId() {
//        return jobdefId;
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     * @since 3.0.0
//     */
//    @Override
//    public ValidityPeriodValue getValidityPeriod() {
//        return validityPeriod;
//    }
//
//    /**
//     * {@inheritDoc}
//     *
//     * @since 3.0.0
//     */
//    @Override
//    public Optional<String> getName() {
//        return Optional.ofNullable(name);
//    }
//
//    /**
//     * Get schedule definition priority.
//     *
//     * @return schedule definition priority
//     * @since 3.0.0
//     */
//    public String getPriority() {
//        return priority;
//    }
//
//    /**
//     * Get schedule definition type.
//     *
//     * @return schedule definition type
//     * @since 3.0.0
//     */
//    public String getType() {
//        return type;
//    }
//
//    /**
//     * Get schedule definition value.
//     *
//     * @return schedule definition value
//     * @since 3.0.0
//     */
//    public JsonObject getValue() {
//        return value;
//    }
//
//    /**
//     * Returns a string representation of this.
//     *
//     * @return a string representation
//     * @since 3.0.0
//     */
//    @Override
//    public String toString() {
//        return "SchedefValue{" + "id=" + id + ", priority=" + priority
//            + ", type=" + type + ", assignables=" + assignables
//            + ", jobdefId=" + jobdefId + ", value=" + value + '}';
//    }
}
