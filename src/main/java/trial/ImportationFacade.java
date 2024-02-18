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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobPhase;
import jp.mydns.projectk.safi.dao.CommonBatchDao;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.service.ImportationService;
import jp.mydns.projectk.safi.service.ImportationService.UserImportationService;
import static jp.mydns.projectk.safi.util.LambdaUtils.toLinkedHashMap;
import jp.mydns.projectk.safi.util.StreamUtils;
import jp.mydns.projectk.safi.value.ContentMap;
import jp.mydns.projectk.safi.value.ContentRecord;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.ImportContext;
import jp.mydns.projectk.safi.value.ImportationValue;
import jp.mydns.projectk.safi.value.TransResult;
import jp.mydns.projectk.safi.value.UserValue;

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
 * <li>Restructure the content and its derived data on the database.</li>
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
     * @throws UncheckedInterruptedException if interrupted
     * @throws UncheckedIOException if occurs I/O error
     * @since 1.0.0
     */
    void importContents(ImportContext ctx);

    /**
     * Abstract implements of the {@code ImportationFacade}.
     *
     * @param <V> content value type
     * @param <S> importation service type
     */
    abstract class AbstractImportationFacade<V extends ContentValue<V>, S extends ImportationService<V>>
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
         * @throws UncheckedInterruptedException if interrupted
         * @throws UncheckedIOException if occurs I/O error
         * @since 1.0.0
         */
        @Override
        @Transactional(rollbackOn = {IOException.class})
        public void importContents(ImportContext ctx) {
            Objects.requireNonNull(ctx);

            S svc = getImportationService();
            svc.initializeWork();
            throwIfInterrupted();

            try (var s = ctx.getImporter().fetch(); var t = ctx.getTransformer().transform(s); var v = toImportationValues(t);
                    var m = svc.toContentMap(v);) {

                Optional.of(m).filter(ContentMap::hasDuplicates).map(ContentMap::duplicates).ifPresent(p -> {
                    recSvc.rec("Duplicate content detected.");
                    StreamUtils.toChunkedStream(p).forEachOrdered(c -> {
                        c.forEach(d -> recSvc.rec(recDxo.toFailure(d, JobPhase.VALIDATION, "Duplicate id.")));
                        comDao.flushAndClear();
                        throwIfInterrupted();
                    });
                });

                // Process register, update and explicit delete the contents.
                StreamUtils.toChunkedStream(m.stream()).map(c -> c.stream().collect(toLinkedHashMap())).forEachOrdered(c -> {
                    c.values().stream().forEach(svc::registerWork);
                    comDao.flushAndClear();
                    throwIfInterrupted();

                    svc.getToBeRegistered(c).map(svc::register).forEach(recSvc::rec);
                    comDao.flushAndClear();
                    throwIfInterrupted();
                });
            } catch (InterruptedException ex) {
                throw new UncheckedInterruptedException(ex);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }

            // Process implicit delete the lost contents.
            if (ctx.isAllowedImplicitDeletion()) {
                long delCount = svc.getCountOfToBeImplicitDelete(ctx.getAdditionalConditionForImplicitDeletion());
                long delLimit = ctx.getLimitNumberOfImplicitDeletion();
                boolean isExceedDelLimit = delCount > delLimit;
                throwIfInterrupted();

                if (isExceedDelLimit) {
                    recSvc.rec(String.format("Deletion limit count exceeded. %d/%d", delCount, delLimit));
                }

                Function<ImportationValue<V>, ContentRecord> delete = !isExceedDelLimit ? svc::register
                        : v -> recDxo.toFailure(v, JobPhase.PROVISIONING, "Ignored because the limit was exceeded.");

                try (var toBeDeleted = svc.getToBeImplicitDeleted(ctx.getAdditionalConditionForImplicitDeletion());) {
                    toBeDeleted.forEachOrdered(c -> {
                        c.stream().map(delete).forEach(recSvc::rec);
                        comDao.flushAndClear();
                        throwIfInterrupted();
                    });
                }
            }

            // Rebuild the persisted contents.
            try (var toBeRebuilt = svc.getToBeRebuilt(appTimeSvc.getLocalNow());) {
                toBeRebuilt.forEachOrdered(c -> {
                    c.stream().map(svc::register).forEach(recSvc::rec);
                    comDao.flushAndClear();
                    throwIfInterrupted();
                });
            }

            svc.updateContentDerivedData();
        }

        private Stream<ImportationValue<V>> toImportationValues(Stream<TransResult> trunsResults) {
            return trunsResults.map(this::toImportationValue).flatMap(Optional::stream);
        }

        private Optional<ImportationValue<V>> toImportationValue(TransResult transResult) {
            if (!transResult.isSuccessful()) {
                recSvc.rec(recDxo.toFailure(transResult, JobPhase.TRANSFORMATION, transResult.getReason()));
                return Optional.empty();
            }

            return getImportationService().toImportationValue(TransResult.Success.class.cast(transResult), recSvc::rec);
        }

        private void throwIfInterrupted() {
            if (Thread.currentThread().isInterrupted()) {
                throw new UncheckedInterruptedException(new InterruptedException());
            }
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

}
