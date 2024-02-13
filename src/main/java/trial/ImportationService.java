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
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.function.Predicate.not;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobPhase;
import jp.mydns.projectk.safi.constant.RecordKind;
import jp.mydns.projectk.safi.dao.CommonBatchDao;
import jp.mydns.projectk.safi.dao.CommonImportationDao;
import jp.mydns.projectk.safi.dao.ImportationDao;
import jp.mydns.projectk.safi.dao.UserImportationDao;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.service.JsonService;
import static jp.mydns.projectk.safi.util.LambdaUtils.c;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.ContentMap;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.RecordableValue;
import jp.mydns.projectk.safi.value.TransResult;
import jp.mydns.projectk.safi.value.UserValue;

/**
 * Processes for importation content.
 *
 * @param <C> content type
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportationService<C extends ContentValue> {

    /**
     * Initialize the importation working area. All data in working area will be erased.
     *
     * @since 1.0.0
     */
    void initializeWork();

    /**
     * Register import content collection to working area.
     *
     * @param values import content collection
     * @since 1.0.0
     */
    void registerWork(Collection<ImportationValue<C>> values);

    /**
     * Convert to the {@code ImportationValue}. Values that fail the conversion are excluded from the return value and
     * the failure reason is logged via {@code failureReasonCollector}.
     *
     * @param trunsResult conversion source data
     * @param failureReasonCollector function to collect reason for conversion failure
     * @return converted data
     * @since 1.0.0
     */
    Optional<ImportationValue<C>> toImportationValue(TransResult.Success trunsResult, Consumer<String> failureReasonCollector);

    /**
     * Convert to the {@code ContentMap}. The purpose of conversion is to eliminate duplicate content.
     *
     * @param values collection of the importation content
     * @return collection of the importation content as the {@code ContentMap}
     * @throws IOException if occurs I/O error
     * @since 1.0.0
     */
    ContentMap<ImportationValue<C>> toContentMap(Stream<ImportationValue<C>> values) throws IOException;

    /**
     * Returns the content to be registered from the difference of content between the will be imported and the
     * registered in the database. Registration means creation and updating.
     *
     * @param values collection of the importation content
     * @return content collection of the to be registered
     * @since 1.0.0
     */
    Stream<ImportationValue<C>> getToBeRegistered(Map<String, ImportationValue<C>> values);

    /**
     * Returns the content to be explicit deleted.
     *
     * @param values collection of the importation content
     * @return content collection of the to be registered
     * @since 1.0.0
     */
    Stream<ImportationValue<C>> getToBeExplicitDeleted(Map<String, ImportationValue<C>> values);

    /**
     * Build an implicit deletion content extraction condition using additional conditions.
     *
     * @param additionalCondition additional conditions for extract lost content
     * @return implicit deletion content extraction condition
     * @since 1.0.0
     */
    Condition buildConditionForImplicitDeletion(Condition additionalCondition);

    /**
     * Get a count of the implicit deletion content.
     *
     * @param condition implicit deletion content extraction condition
     * @return count of the implicit deletion content
     * @since 1.0.0
     */
    long getToBeImplicitDeleteCount(Condition condition);

    /**
     * Gets a chunked collection of implicitly deleted content.
     *
     * @param condition implicit deletion content extraction condition
     * @return implicit deletion content
     * @since 1.0.0
     */
    Stream<List<ImportationValue<C>>> getToBeImplicitDeleted(Condition condition);

    /**
     * Register content to database. Create or update is automatically determined.
     *
     * @param value content
     * @since 1.0.0
     */
    void register(ImportationValue<C> value);

    /**
     * Logically delete content registered in the database.
     *
     * @param value content
     * @since 1.0.0
     */
    void logicalDelete(ImportationValue<C> value);

    /**
     * Rebuild the content registered in the database. For example, updating derived database tables etc.
     *
     * @since 1.0.0
     */
    void rebuildPersistedContents();

    /**
     * Abstract implements of the {@code ImportationService}.
     *
     * @param <E> entity type
     * @param <C> content type
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractImportationService<E extends ContentEntity, C extends ContentValue>
            implements ImportationService<C> {

        @Inject
        private CommonBatchDao comDao;

        @Inject
        private Jsonb jsonb;

        @Inject
        private JsonService jsonSvc;

        @Inject
        private ConfigService confSvc;

        @Inject
        private AppTimeService appTimeSvc;

        @Inject
        private Validator validator;

        @Inject
        private CommonImportationDao comImportDao;

        @Inject
        private CommonImportationDxo comImportDxo;

        protected abstract Class<C> getContentType();

        protected abstract ImportationDxo<E, C> getDxo();

        protected abstract ImportationDao<E> getDao();

        /**
         * {@inheritDoc}
         *
         * @throws PersistenceException if occurs an exception while access to database
         * @since 1.0.0
         */
        @Override
        public void initializeWork() {
            comImportDao.clearWrk();
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code values} is {@code null}
         * @throws PersistenceException if occurs an exception while access to database
         * @since 1.0.0
         */
        @Override
        public void registerWork(Collection<ImportationValue<C>> values) {
            comImportDao.appendWrk(Objects.requireNonNull(values).stream().filter(not(ImportationValue::doDelete))
                    .map(ImportationValue::getContent).map(comImportDxo::toWrkEntity));
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        @Override
        public Optional<ImportationValue<C>> toImportationValue(
                TransResult.Success transformed, Consumer<String> failureReasonCollector) {
            Objects.requireNonNull(transformed);
            Objects.requireNonNull(failureReasonCollector);

            try {
                return Optional.of(getDxo().toValue(transformed));
            } catch (ConstraintViolationException ex) {
                failureReasonCollector.accept(ex.getConstraintViolations().stream()
                        .map(ValidationUtils::toMessageEntry).toList().toString());
            } catch (RuntimeException ex) {
                failureReasonCollector.accept(Optional.ofNullable(ex.getMessage()).filter(not(String::isBlank))
                        .orElse("Occurs unexpected error while build an importation content."));
            }

            return Optional.empty();
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code values} is {@code null}
         * @since 1.0.0
         */
        @Override
        public ContentMap<ImportationValue<C>> toContentMap(Stream<ImportationValue<C>> values) throws IOException {
            return new ContentMap<>(Objects.requireNonNull(values).map(ImportationValue::asEntry).iterator(),
                    confSvc.getTmpDir(), new ContentConvertor());
        }

        private class ContentConvertor implements ContentMap.Convertor<ImportationValue<C>> {

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String serialize(ImportationValue<C> c) {
                return jsonb.toJson(c);
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public ImportationValue<C> deserialize(String s) {
                JsonObject jo = jsonSvc.toJsonObject(Objects.requireNonNull(s));
                return new ImportationValue<>(jsonSvc.convertViaJson(jo.get("content"), getContentClass()),
                        jsonSvc.toStringMap(jo.getJsonObject("source")));
            }
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code values} is {@code null}
         * @throws PersistenceException if occurs an exception while access to database
         * @since 1.0.0
         */
        @Override
        public Stream<ImportationValue<C>> getToBeRegistered(Map<String, ImportationValue<C>> values) {
            Set<String> targetIds = Set.of(values.keySet());
            ImportationDxo<E, C> dxo = getDxo();

            return Stream.concat(getDao().getAdditions(targetIds).map(values::get),
                    getDao().getUpdates(targetIds).flatMap(e -> Stream.of(dxo.toValue(values.get(e.getId()), e))));
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code values} is {@code null}
         * @throws PersistenceException if occurs an exception while access to database
         * @since 1.0.0
         */
        @Override
        public Stream<ImportationValue<C>> getToBeExplicitDeleted(Collection<ImportationValue<C>> values) {
            return getDao().getContents(Objects.requireNonNull(values).stream()
                    .filter(ImportationValue::doDelete).map(ImportationValue::getId).toList()
            ).map(e -> new ImportationValue<>(getDxo().toLogicalDeletion(e), values.get(e.getId()).getSource(), e));
        }

        /**
         * Get count of values that to be implicit deleted.
         *
         * @param additional additional extract condition
         * @return count of values that to be implicit deleted
         * @throws NullPointerException if {@code additional} is {@code null}
         * @throws PersistenceException if occurs problem in persistence provider
         */
        public long getToBeImplicitDeleteCount(Condition additional) {
            return getDao().getCountOfLost(additional);
        }

        /**
         * Get values that to be implicit deleted.
         *
         * @param additional additional extract condition
         * @return values that to be explicit deleted
         * @throws NullPointerException if {@code additional} is {@code null}
         * @throws PersistenceException if occurs problem in persistence provider
         */
        public Stream<List<ImportationValue<C>>> getToBeImplicitDeleted(Condition additional) {
            return getDao().getLosts(additional).map(applyToList(
                    e -> new ImportationValue<>(getDxo().toLogicalDeletion(e), Map.of(), e)));
        }
//
//    /**
//     * Validate of minimal for subsequent processing.
//     *
//     * @param transformed transform results
//     * @return valid values
//     * @throws IOException if occurs I/O error
//     * @throws NullPointerException if {@code transformed} is {@code null}
//     */
//    public ContentMap<ImportationValue<C>> minimalValidate(Stream<TransResult.Success> transformed) throws IOException {
//
//        try (var trs = transformed) {
//
//            Iterator<Map.Entry<String, ImportationValue<C>>> it
//                    = trs.flatMap(tr -> toImportationValue(tr).stream())
//                            .map(v -> Map.entry(v.getId(), v)).iterator();
//
//            return new ContentMap<>(it, confSvc.getWorkDirectoryPath(), newConverter());
//        }
//    }

        /**
         * Register value.
         *
         * @param value the {@code ImportationValue}
         * @throws NullPointerException if {@code value} is {@code null}
         * @throws UncheckedIOException if occurs I/O error
         * @throws PersistenceException if occurs problem in persistence provider
         */
        public void register(ImportationValue<C> value) {
            comDao.persistOrMerge(getDxo().toEntity(value));
            recSvc.rec(recDxo.toSuccess(value, RecordKind.REGISTER));
        }

        /**
         * Delete(logically) value.
         *
         * @param value the {@code ImportationValue}
         * @throws NullPointerException if {@code value} is {@code null}
         * @throws UncheckedIOException if occurs I/O error
         * @throws PersistenceException if occurs problem in persistence provider
         */
        public void logicalDelete(ImportationValue<C> value) {
            comDao.persistOrMerge(getDxo().toEntity(value));
            recSvc.rec(recDxo.toSuccess(value, RecordKind.DELETION));
        }

        /**
         * Rebuilds content with the current time of application, and also rebuilds content-dependent database tables.
         *
         * @param opts job options
         *
         * @throws PersistenceException if occurs problem in persistence provider
         * @throws UncheckedIOException if occurs I/O error
         */
        @Transactional

        public void rebuild(JobOptionsImpl opts) {
            Consumer<RecordableValue> record
                    = v -> recSvc.rec(recDxo.toSuccess(v, RecordKind.REGISTER));

            try (var rebuilds = getDao().getRebuilds(appTimeSvc.getNow())) {
                rebuilds.forEach(chunk -> {
                    chunk.stream().map(getDxo()::rebuild)
                            .forEach(c(record).andThen(comDao::merge));
                    comDao.flushAndClear();
                });
            }

            rebuildDependent();
        }

        /**
         * Rebuilds content-dependent database tables.
         *
         * @throws PersistenceException if occurs problem in persistence provider
         */
        @Transactional(Transactional.TxType.MANDATORY)
        public void rebuildDependent() {
            getDao().doUniqueRebuilding();
        }

        @Transactional(Transactional.TxType.MANDATORY)
        public void initializeImportationWork() {

        }

        @Transactional(Transactional.TxType.MANDATORY)
        public Stream<TransResult.Success> extractSuccessfulsAndCollectFailures(Stream<TransResult> transformed) {

        }

        public ContentMap<ImportationValue<C>> toContentMap(Stream<ImportationValue<C>> values) throws IOException {
            return new ContentMap<>(values.map(ImportationValue::asEntry).iterator(), confSvc.getTmpDir(), new ContentConvertor());
        }

        private class ContentConvertor implements ContentMap.Convertor<ImportationValue<C>> {

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String serialize(ImportationValue<C> c) {
                return jsonb.toJson(c);
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public ImportationValue<C> deserialize(String s) {
                JsonObject j = jsonSvc.toJsonObject(Objects.requireNonNull(s));
                return new ImportationValue<>(
                        jsonSvc.convertViaJson(j.get("content"), getContentClass()),
                        jsonSvc.toStringMap(j.getJsonObject("source")));
            }
        }

        public void registerToImportationWork(Collection<ImportationValue<C>> contents) {

        }

        public Condition buildConditionForExtractingImplicitDeletion(Condition additionalCondition) {

        }

        public void collectDuplicateAsFailure(ImportationValue<C> duplicate) {
            d -> recSvc.rec(recDxo.toFailure(d, JobPhase.VALIDATION, List.of("Duplicate id.")));
        }

        public void collectDeniedDeletionAsFailure(ImportationValue<C> value) {
            v -> recSvc.rec(recDxo.toFailure(v, JobPhase.PROVISIONING,
                    List.of("Ignored because the limit was exceeded.")));
        }

    }

    /**
     * Batch processing for user content.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @RequestScoped
    class UserImportationService extends AbstractImportationService<UserEntity, UserValue> implements
            ImportationService<UserValue> {

        @Inject
        private UserBatchDxo dxo;

        @Inject
        private UserImportationDao dao;

        @Override
        protected UserBatchDxo getDxo() {
            return dxo;
        }

        @Override
        protected UserImportationDao getDao() {
            return dao;
        }

        @Override
        protected Class<UserValue> getContentType() {
            return UserValue.class;
        }
    }
}
