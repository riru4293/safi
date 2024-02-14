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
package jp.mydns.projectk.safi.facade;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobPhase;
import jp.mydns.projectk.safi.constant.RecordKind;
import jp.mydns.projectk.safi.dao.CommonBatchDao;
import jp.mydns.projectk.safi.service.AppTimeService;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import static jp.mydns.projectk.safi.util.LambdaUtils.toLinkedHashMap;
import jp.mydns.projectk.safi.util.StreamUtils;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.ContentMap;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.ImportContext;
import jp.mydns.projectk.safi.value.TransResult;
import jp.mydns.projectk.safi.value.UserValue;
import trial.BelongGroupImportationService;
import trial.BelongGroupValue;
import trial.BelongOrgImportationService;
import trial.BelongOrgValue;
import trial.GroupImportationService;
import trial.GroupValue;
import trial.ImportationService;
import trial.ImportationService.UserImportationService;
import trial.ImportationValue;
import trial.JobRecordingService;
import trial.MediumImportationService;
import trial.MediumValue;
import trial.Org1ImportationService;
import trial.Org2ImportationService;
import trial.OrgValue;
import trial.RecordingDxo;

/**
 * The facade process for the content import process.
 * <p>
 * Transform the content source data into content and the importing it into the <i>SAFI</i>
 * database. Priority is given to continuing processing, and data that fails to be processed is recorded and excluded
 * from processing. Specifically, perform the following processing.
 * <ul>
 * <li>Obtains the key-value format data from an external data source.</li>
 * <li>Transform data using transformation definitions.</li>
 * <li>Convert to SAFI proprietary content format.</li>
 * <li>Exclude content with duplicate id,</li>
 * <li>Compare with registered content to determine addition/update/deletion.</li>
 * <li>Reflect additions/updates/explicit deletions to the database.</li>
 * <li>If allowed, propagates implicit deletions to the database. However, if the number exceeds the limit, it will not
 * be processed.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportationFacade {

    /**
     * Import the contents to <i>SAFI</i> database.
     *
     * @param ctx the {@code ImportContext}
     * @throws NullPointerException if {@code ctx} is {@code null}
     * @throws InterruptedException if interrupted
     * @throws IOException if occurs I/O error
     * @since 1.0.0
     */
    void importContents(ImportContext ctx) throws IOException, InterruptedException;

    /**
     * Abstract implements of the {@code ImportationFacade}.
     *
     * @param <C> content value type
     * @param <S> importation service type
     */
    abstract class AbstractImportationFacade<C extends ContentValue, S extends ImportationService<C>>
            implements ImportationFacade {

        @Inject
        private CommonBatchDao comDao;

        @Inject
        private JobRecordingService recSvc;

        @Inject
        private RecordingDxo recDxo;

        @Inject
        private AppTimeService appTimeSvc;

        /**
         * Get service of importation.
         *
         * @return importation service
         */
        protected abstract S getImportationService();

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        @Override
        @Transactional(rollbackOn = {InterruptedException.class, IOException.class})
        public void importContents(ImportContext ctx) throws IOException, InterruptedException {
            Objects.requireNonNull(ctx);

            S svc = getImportationService();
            svc.initializeWork();

            try (var s = ctx.getImporter().fetch(); var t = ctx.getTransformer().transform(s); var v = toImportationValues(t);
                    var m = svc.toContentMap(v);) {
                // Records duplicate content as a failure.
                Optional.of(m).filter(ContentMap::hasDuplicates).map(ContentMap::duplicates)
                        .ifPresent(this::recordDuplicates);

                // Process register, update and explicit delete the contents.
                registerContents(m.stream());
            }

            // Process implicit delete the lost contents.
            Optional.of(ctx).filter(ImportContext::isAllowedImplicitDeletion)
                    .ifPresent(this::deleteLostContents);

            // Rebuild the persisted contents.
            rebuildContents(appTimeSvc.getLocalNow());
        }

        private void recordDuplicates(Stream<ImportationValue<C>> duplicates) {
            recSvc.rec("Duplicate content detected.");
            StreamUtils.toChunkedStream(duplicates).forEachOrdered(c -> {
                c.forEach(d -> recSvc.rec(recDxo.toFailure(d, JobPhase.VALIDATION, "Duplicate id.")));
                comDao.flushAndClear();
            });
        }

        private void registerContents(Stream<ImportationValue<C>> contents) {
            S svc = getImportationService();

            StreamUtils.toChunkedStream(contents).map(c -> c.stream().collect(toLinkedHashMap())).forEachOrdered(c -> {
                // Deletes content that is explicitly instructed to be deleted.
                svc.getToBeExplicitDeleted(c.values()).forEach(c(svc::logicalDelete)
                        .andThen(v -> recSvc.rec(recDxo.toSuccess(v, RecordKind.DELETION))));

                // Preparing the working data for calculate difference.
                svc.registerWork(c.values());

                // Register the contents to database that are create and update.
                svc.getToBeRegistered(c).forEach(c(svc::register)
                        .andThen(v -> recSvc.rec(recDxo.toSuccess(v, RecordKind.REGISTER))));

                comDao.flushAndClear();
            });
        }

        private void deleteLostContents(ImportContext ctx) {
            S svc = getImportationService();
            Condition cond = svc.buildConditionForImplicitDeletion(ctx.getAdditionalConditionForImplicitDeletion());

            long count = svc.getToBeImplicitDeleteCount(cond);
            long limit = ctx.getLimitNumberOfImplicitDeletion();
            boolean isExceedLimit = count > limit;

            if (isExceedLimit) {
                recSvc.rec(String.format("Deletion limit count exceeded. %d/%d", count, limit));
            }

            Consumer<ImportationValue<C>> delete = !isExceedLimit
                    ? c(svc::logicalDelete).andThen(v -> recSvc.rec(recDxo.toSuccess(v, RecordKind.DELETION)))
                    : v -> recSvc.rec(recDxo.toFailure(v, JobPhase.PROVISIONING, "Ignored because the limit was exceeded."));

            try (var toBeDeleted = svc.getToBeImplicitDeleted(cond);) {
                toBeDeleted.forEachOrdered(c -> {
                    c.forEach(delete);
                    comDao.flushAndClear();
                });
            }
        }

        private void rebuildContents(LocalDateTime refTime) {
            S svc = getImportationService();
            Consumer<C> register = svc::register;
            Consumer<C> record = r -> recSvc.rec(recDxo.toSuccess(r,
                    r.isEnabled() ? RecordKind.REGISTER : RecordKind.DELETION));

            try (var toBeRebuilt = svc.getToBeRebuilt(appTimeSvc.getLocalNow());) {
                toBeRebuilt.forEachOrdered(c -> {
                    c.stream().map(svc::rebuild).forEach(register.andThen(record));
                    comDao.flushAndClear();
                });
            }

            svc.rebuildPersistedContents();
        }

        private Stream<ImportationValue<C>> toImportationValues(Stream<TransResult> trunsResults) {
            return trunsResults.map(this::toImportationValue).flatMap(Optional::stream);
        }

        private Optional<ImportationValue<C>> toImportationValue(TransResult transResult) {
            if (!transResult.isSuccessful()) {
                recSvc.rec(recDxo.toFailure(transResult, JobPhase.TRANSFORMATION, transResult.getReason()));
                return Optional.empty();
            }

            Consumer<String> failureReasonCollector = r -> recSvc.rec(recDxo.toFailure(transResult, JobPhase.VALIDATION, r));

            return getImportationService().toImportationValue(TransResult.Success.class.cast(transResult), failureReasonCollector);
        }
    }

    /**
     * The {@link ImportationFacade} for <i>User</i> content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class UserImportationFacade extends AbstractImportationFacade<UserValue, UserImportationService> {

        @Inject
        private UserImportationService svc;

        @Override
        protected UserImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * The {@link ImportationFacade} for <i>Medium</i> content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class MediumImportationFacade extends AbstractImportationFacade<MediumValue, MediumImportationService> {

        @Inject
        private MediumImportationService svc;

        @Override
        protected MediumImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * The {@link ImportationFacade} for <i>Belong-Organization</i> content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class BelongOrgImportationFacade extends AbstractImportationFacade<BelongOrgValue, BelongOrgImportationService> {

        @Inject
        private BelongOrgImportationService svc;

        @Override
        protected BelongOrgImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * The {@link ImportationFacade} for <i>Organization</i> #1 content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class Org1ImportationFacade extends AbstractImportationFacade<OrgValue, Org1ImportationService> {

        @Inject
        private Org1ImportationService svc;

        @Override
        protected Org1ImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * The {@link ImportationFacade} for <i>Organization</i> #2 content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class Org2ImportationFacade extends AbstractImportationFacade<OrgValue, Org2ImportationService> {

        @Inject
        private Org2ImportationService svc;

        @Override
        protected Org2ImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * The {@link ImportationFacade} for <i>Belong-Group</i> content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class BelongGroupImportationFacade extends AbstractImportationFacade<BelongGroupValue, BelongGroupImportationService> {

        @Inject
        private BelongGroupImportationService svc;

        @Override
        protected BelongGroupImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * The {@link ImportationFacade} for <i>Group</i> content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class GroupImportationFacade extends AbstractImportationFacade<GroupValue, GroupImportationService> {

        @Inject
        private GroupImportationService svc;

        @Override
        protected GroupImportationService getImportationService() {
            return svc;
        }
    }
}
