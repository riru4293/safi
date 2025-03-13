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
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import jp.mydns.projectk.safi.dao.JobdefDao;
import jp.mydns.projectk.safi.dxo.JobdefDxo;
import jp.mydns.projectk.safi.entity.JobdefEntity;
import jp.mydns.projectk.safi.value.JobCreationContext;
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

        private static final long serialVersionUID = 7853095085270398570L;

        /**
         * Construct with error message.
         *
         * @param message error message. Keep in mind that this may be exposed to users. It must be an abstract message
         * that can briefly describe the issue.
         * @since 3.0.0
         */
        public JobdefIOException(String message) {
            super(message);
        }
    }

    /**
     * Build the {@code JobdefValue} from the {@code JobCreationContext}.
     *
     * @param ctx the {@code JobCreationContext}
     * @return the {@code JobdefValue}
     * @throws ConstraintViolationException if not exists a job definition that specified in {@code ctx} or if malformed
     * return value.
     * @throws NullPointerException if any argument is {@code null}
     * @throws JobdefIOException if not found valid job definition
     * @since 3.0.0
     */
    JobdefValue buildJobdef(JobCreationContext ctx) throws JobdefIOException;

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

        /**
         * Constructor.
         *
         * @param jobdefDxo the {@code JobdefDxo}
         * @param jobdefDao the {@code JobdefDao}
         * @param validationSvc the {@code ValidationService}
         * @param jsonSvc the {@code JsonService}
         * @since 3.0.0
         */
        @Inject
        public Impl(JobdefDxo jobdefDxo, JobdefDao jobdefDao, ValidationService validationSvc,
            JsonService jsonSvc) {
            this.jobdefDxo = jobdefDxo;
            this.jobdefDao = jobdefDao;
            this.validationSvc = validationSvc;
            this.jsonSvc = jsonSvc;
        }

        /**
         * {@inheritDoc}
         *
         * @throws ConstraintViolationException if not exists a job definition that specified in {@code ctx} or if
         * malformed return value.
         * @throws NullPointerException if any argument is {@code null}
         * @throws JobdefIOException if not found valid job definition
         * @since 3.0.0
         */
        @Override
        public JobdefValue buildJobdef(JobCreationContext ctx) throws JobdefIOException {
            Objects.requireNonNull(ctx);

            UnaryOperator<JsonObject> overwriteCtx = b -> jsonSvc.merge(b, jsonSvc.toJsonValue(ctx).
                asJsonObject());

            return getValidJobdefEntity(ctx.getJobdefId()).map(jsonSvc::toJsonValue).map(JsonValue::asJsonObject)
                .map(overwriteCtx).map(jobdefDxo::toValue).orElseThrow(noFoundJobdef);
        }

        private Optional<JobdefEntity> getValidJobdefEntity(String id) {
            return jobdefDao.getJobdef(id).filter(validationSvc::isEnabled);
        }

    }
}
