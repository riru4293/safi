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

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import jp.mydns.projectk.safi.dao.JobdefDao;
import jp.mydns.projectk.safi.dxo.JobdefDxo;
import jp.mydns.projectk.safi.value.JobCreationContext;
import jp.mydns.projectk.safi.value.JobCreationRequest;
import jp.mydns.projectk.safi.value.JobdefValue;

/**
 * Service for <i>Job definition</i>.
 *
 * @author riru
 * @version 3.0.0
 * @since 3.0.0
 */
public interface JobdefService {

    /**
     * Indicates that a jobdef I/O exception has occurred. For example, it doesn't exist, you don't have permission to
     * modify it, and so on.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    class JobdefIOException extends IOException {

        @java.io.Serial
        private static final long serialVersionUID = 7853095085270398570L;

        private JobdefIOException(String message) {
            super(message);
        }
    }

    /**
     * Build the {@code JobCreationContext} from the {@code JobCreationRequest}.
     *
     * @param req the {@code JobCreationRequest}
     * @return the {@code JobCreationContext}
     * @throws NullPointerException if {@code req} is {@code null}
     * @throws JobdefIOException if not found valid job definition
     * @throws PersistenceException if failed database operation
     * @since 3.0.0
     */
    JobCreationContext buildJobCreationContext(JobCreationRequest req) throws JobdefIOException;

    /**
     * Implements of the {@code JobdefService}.
     *
     * @author riru
     * @version 3.0.0
     * @since 3.0.0
     */
    @Typed(JobdefService.class)
    @RequestScoped
    class Impl implements JobdefService {

        private final Supplier<JobdefIOException> noFoundJobdef
            = () -> new JobdefIOException("No found job definition.");

        private final JobdefDao jobdefDao;
        private final JobdefDxo jobdefDxo;
        private final ValidationService validationSvc;
        private final JsonService jsonSvc;
        private final RealTimeService realTimeSvc;

        /**
         * Constructor.
         *
         * @param jobdefDxo the {@code JobdefDxo}
         * @param jobdefDao the {@code JobdefDao}
         * @param validationSvc the {@code ValidationService}
         * @param jsonSvc the {@code JsonService}
         * @param realTimeSvc the {@code RealTimeService}
         * @throws NullPointerException if any argument is {@code null}
         * @since 3.0.0
         */
        @Inject
        public Impl(JobdefDxo jobdefDxo, JobdefDao jobdefDao, ValidationService validationSvc,
            JsonService jsonSvc, RealTimeService realTimeSvc) {

            this.jobdefDxo = Objects.requireNonNull(jobdefDxo);
            this.jobdefDao = Objects.requireNonNull(jobdefDao);
            this.validationSvc = Objects.requireNonNull(validationSvc);
            this.jsonSvc = Objects.requireNonNull(jsonSvc);
            this.realTimeSvc = Objects.requireNonNull(realTimeSvc);
        }

        /**
         * {@inheritDoc}
         *
         * @throws ConstraintViolationException if not exists a job definition that specified in {@code ctx} or if
         * malformed return value.
         * @throws NullPointerException if {@code req} is {@code null}
         * @throws JobdefIOException if not found valid job definition
         * @throws PersistenceException if failed database operation
         * @since 3.0.0
         */
        @Override
        public JobCreationContext buildJobCreationContext(JobCreationRequest req) throws JobdefIOException {
            Objects.requireNonNull(req);

            UnaryOperator<JsonObject> overWriteRequest = b -> jsonSvc.merge(b, jsonSvc.toJsonValue(req).asJsonObject());

            JobdefValue jobdef = jobdefDao.getJobdef(req.getJobdefId()).filter(validationSvc::isEnabled)
                .map(jobdefDxo::toValue).map(jsonSvc::toJsonValue).map(JsonValue::asJsonObject).map(overWriteRequest)
                .map(jobdefDxo::toValue).orElseThrow(noFoundJobdef);

            return new JobCreationContext(req.getScheduleTime().orElseGet(realTimeSvc::getOffsetNow), jobdef);
        }
    }
}
