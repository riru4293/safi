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
package trial;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import jp.mydns.projectk.safi.dao.CommonBatchDao;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.ImportContext;
import jp.mydns.projectk.safi.value.UserValue;
import trial.ImportationService.UserImportationService;

/**
 * The facade process of transform the content source data into content and the importing it into the <i>SAFI</i>
 * database.
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
    @Dependent
    abstract class AbstractImportationFacade<C extends ContentValue, S extends ImportationService<C>>
            implements ImportationFacade {

        @Inject
        private CommonBatchDao comDao;

        @Inject
        private JobRecordingService recSvc;

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
         */
        @Override
        @Transactional(rollbackOn = {InterruptedException.class, IOException.class})
        public void importContents(ImportContext ctx) throws IOException, InterruptedException {
            Objects.requireNonNull(ctx);

            S svc = getImportationService();

            // Clear all the work-data of importation.
            svc.initializeImportationWork();

            try (var s = ctx.getImporter().fetch(); var t = ctx.getTransformer().transform(s);
                    var x = svc.extractSuccessfulsAndCollectFailures(t); var m = svc.toContentMap(x);
                    var h = svc.toChunkedStream(m.stream());) {

                // Records duplicate content as a failure.
                if (m.duplicates().findAny().isPresent()) {
                    recSvc.rec("Duplicate content detected.");
                    m.duplicates().forEach(svc::collectDuplicateAsFailure);
                }

                // Register, update, and delete the contents.
                h.forEachOrdered(c -> {
                    // Deletes content that is explicitly instructed to be deleted.
                    svc.getToBeExplicitDeleted(c).forEach(svc::logicalDelete);

                    // Preparing the work-data for calculate difference.
                    svc.registerToImportationWork(c.values());

                    // Register the contents to database that are create and update.
                    svc.getToBeRegistered(c).forEach(svc::register);

                    comDao.flushAndClear();
                });

            }

            // Implicit delete the lost contents.
            if (ctx.isAllowedImplicitDeletion()) {
                deleteLosts(ctx);
            }

            svc.rebuildDependent();
        }

        private void deleteLosts(ImportContext ctx) {

            S svc = getImportationService();
            Condition extractCondition = svc.buildConditionForExtractingImplicitDeletion(
                    ctx.getAdditionalConditionForExtractingImplicitDeletion());

            long count = svc.getToBeImplicitDeleteCount(extractCondition);
            long limit = ctx.getLimitNumberOfImplicitDeletion();
            boolean isExceedLimit = count > limit;

            Consumer<ImportationValue<C>> delete
                    = !isExceedLimit ? svc::logicalDelete : svc::collectDeniedDeletionAsFailure;

            if (isExceedLimit) {
                recSvc.rec(String.format("Deletion limit count exceeded. %d/%d", count, limit));
            }

            try (var toBeDeleted = svc.getToBeImplicitDeleted(extractCondition);) {
                toBeDeleted.forEachOrdered(c -> {
                    c.forEach(delete);
                    comDao.flushAndClear();
                });
            }

        }

    }

    /**
     * The {@code ImportationFacade} for the <i>User</i> content.
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
     * Facade of import medium contents.
     */
    @RequestScoped
    class MediumImportationFacade extends AbstractImportationFacade<Medium, MediumImportationService> {

        @Inject
        private MediumImportationService svc;

        @Override
        protected MediumImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * Facade of import belong-organization contents.
     */
    @RequestScoped
    class BelongOrgImportationFacade extends AbstractImportationFacade<BelongOrg, BelongOrgImportationService> {

        @Inject
        private BelongOrgImportationService svc;

        @Override
        protected BelongOrgImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * Facade of import organization#1 contents.
     */
    @RequestScoped
    class Org1ImportationFacade extends AbstractImportationFacade<Org, Org1ImportationService> {

        @Inject
        private Org1ImportationService svc;

        @Override
        protected Org1ImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * Facade of import organization#2 contents.
     */
    @RequestScoped
    class Org2ImportationFacade extends AbstractImportationFacade<Org, Org2ImportationService> {

        @Inject
        private Org2ImportationService svc;

        @Override
        protected Org2ImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * Facade of import belong-group contents.
     */
    @RequestScoped
    class BelongGroupImportationFacade extends AbstractImportationFacade<BelongGroup, BelongGroupImportationService> {

        @Inject
        private BelongGroupImportationService svc;

        @Override
        protected BelongGroupImportationService getImportationService() {
            return svc;
        }
    }

    /**
     * Facade of import group contents.
     */
    @RequestScoped
    class GroupImportationFacade extends AbstractImportationFacade<Group, GroupImportationService> {

        @Inject
        private GroupImportationService svc;

        @Override
        protected GroupImportationService getImportationService() {
            return svc;
        }
    }
}
