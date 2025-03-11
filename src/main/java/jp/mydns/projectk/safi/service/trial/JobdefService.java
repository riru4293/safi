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
package jp.mydns.projectk.safi.service.trial;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;
import jp.mydns.projectk.safi.dao.JobdefDao;
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
@RequestScoped
public class JobdefService {

    private final JobdefDao jobdefDao;

    /**
     * Constructor.
     *
     * @param jobdefDao the {@code JobdefDao}
     * @since 3.0.0
     */
    @Inject
    public JobdefService(JobdefDao jobdefDao) {
        this.jobdefDao = jobdefDao;
    }

    /**
     * Build the {@code JobdefValue} from the {@code JobCreationContext}.
     *
     * @param ctx the {@code JobCreationContext}
     * @return the {@code JobdefValue}
     * @throws ConstraintViolationException if not exists a job definition that specified in {@code ctx} or if malformed
     * return value.
     * @throws NullPointerException if any argument is {@code null}
     * @since 3.0.0
     */
    public JobdefValue buildJobdef(@NotNull @Valid JobCreationContext ctx) {
        Objects.requireNonNull(ctx);

        return null;
    }

    private Optional<JobdefEntity> getValidJobdefEntity(String id) {

        return jobdefDao.getJobdef(id);
//        return Optional.ofNullable(id)
//                .flatMap(jobdefDao::getJobdef)
//                .filter(validationSvc::withinValidityPeriod);
    }
}
