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

import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jp.mydns.projectk.safi.constant.SchedefKing;
import jp.mydns.projectk.safi.entity.SchedefEntity;
import jp.mydns.projectk.safi.value.ValidityPeriodValue;

/**
 * Schedule definition.
 *
 * @implSpec This class is immutable and thread-safe.
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public class SchedefValue {

    private final String id;
    private final String jobdefId;
    private final ValidityPeriodValue validityPeriod;
    private final String priority;
    private final SchedefKing type;
    private final String name;
    private final JsonObject value;

    /**
     * Constructor.
     *
     * @param entity the {@code MSchedef}
     * @throws NullPointerException if {@code entity} is {@code null}
     * @throws IllegalArgumentException if schedule definition type does not exist
     * @since 3.0.0
     */
    public SchedefValue(SchedefEntity entity) {
        JsonValue rawType = entity.getValue().unwrap().asJsonObject().get("type");
        if (rawType == null || rawType.getValueType() != JsonValue.ValueType.STRING) {
            throw new IllegalArgumentException("Schedule definition type does not exist.");
        }
        this.id = entity.getId();
        this.priority = entity.getPriority();
        this.value = entity.getValue();
        this.type = JsonString.class.cast(rawType).getString();
        this.jobdefId = entity.getJobdefId();
    }

    /**
     * Get schedule definition id.
     *
     * @return schedule definition id
     * @since 3.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * Get job definition id.
     *
     * @return job definition id
     * @since 3.0.0
     */
    public String getJobdefId() {
        return jobdefId;
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public ValidityPeriodValue getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    /**
     * Get schedule definition priority.
     *
     * @return schedule definition priority
     * @since 3.0.0
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Get schedule definition type.
     *
     * @return schedule definition type
     * @since 3.0.0
     */
    public String getType() {
        return type;
    }

    /**
     * Get schedule definition value.
     *
     * @return schedule definition value
     * @since 3.0.0
     */
    public JsonObject getValue() {
        return value;
    }

    /**
     * Returns a string representation of this.
     *
     * @return a string representation
     * @since 3.0.0
     */
    @Override
    public String toString() {
        return "SchedefValue{" + "id=" + id + ", priority=" + priority
            + ", type=" + type + ", assignables=" + assignables
            + ", jobdefId=" + jobdefId + ", value=" + value + '}';
    }
}
