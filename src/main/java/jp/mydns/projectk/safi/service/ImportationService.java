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

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.function.Predicate.not;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobPhase;
import jp.mydns.projectk.safi.dao.ImportationDao;
import jp.mydns.projectk.safi.dxo.ImportationDxo;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.util.ThrowableUtils;
import jp.mydns.projectk.safi.util.ValidationUtils;
import jp.mydns.projectk.safi.value.ContentMap;
import jp.mydns.projectk.safi.value.ContentRecord;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.ImportationValue;
import jp.mydns.projectk.safi.value.TransResult;
import trial.RecordingDxo;

/**
 * Processes for importation content.
 *
 * @param <V> content type
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportationService<V extends ContentValue<V>> {

    /**
     * Initialize the importation working area. All data in working area will be erased.
     *
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    void initializeWork();

    /**
     * Register import content collection to working area.
     *
     * @param values import content collection
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    void registerWork(Collection<ImportationValue<V>> values);

    /**
     * Convert to the {@code ImportationValue}. Values that fail the conversion are excluded from the return value and
     * the failure reason is logged via {@code failRecorder}.
     *
     * @param trunsResult conversion source data
     * @param failRecorder function to collect failure content
     * @throws NullPointerException if any argument is {@code null}
     * @return converted data
     * @since 1.0.0
     */
    Optional<ImportationValue<V>> toImportationValue(TransResult.Success trunsResult, Consumer<ContentRecord> failRecorder);

    /**
     * Convert to the {@code ContentMap}. The purpose of conversion is to eliminate duplicate content.
     *
     * @param values collection of the importation content
     * @return collection of the importation content as the {@code ContentMap}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IOException if occurs I/O error
     * @since 1.0.0
     */
    ContentMap<ImportationValue<V>> toContentMap(Stream<ImportationValue<V>> values) throws IOException;

    /**
     * Returns the content to be registered from the difference of content between the will be imported and the
     * registered in the database. Registration means creation and updating.
     *
     * @param values collection of the importation content
     * @return content collection of the to be registered
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws PersistenceException if occurs an exception while access to database
     * @since 1.0.0
     */
    Stream<ImportationValue<V>> getToBeRegistered(Map<String, ImportationValue<V>> values);
//
//    /**
//     * Returns the content to be explicit deleted.
//     *
//     * @param values collection of the importation content
//     * @return content collection of the to be registered
//     * @throws NullPointerException if {@code values} is {@code null}
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    Stream<ImportationValue<C>> getToBeExplicitDeleted(Collection<ImportationValue<C>> values);
//
//    /**
//     * Build an implicit deletion content extraction condition using additional conditions.
//     *
//     * @param additionalCondition additional conditions for extract lost content
//     * @return implicit deletion content extraction condition
//     * @throws NullPointerException if {@code additionalCondition} is {@code null}
//     * @since 1.0.0
//     */
//    Condition buildConditionForImplicitDeletion(Condition additionalCondition);
//
//    /**
//     * Get a count of the implicit deletion content.
//     *
//     * @param condition implicit deletion content extraction condition
//     * @return count of the implicit deletion content
//     * @throws NullPointerException if {@code condition} is {@code null}
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    long getToBeImplicitDeleteCount(Condition condition);
//
//    /**
//     * Gets a chunked collection of implicitly deleted content.
//     *
//     * @param condition implicit deletion content extraction condition
//     * @return implicit deletion content
//     * @throws NullPointerException if {@code condition} is {@code null}
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    Stream<List<ImportationValue<C>>> getToBeImplicitDeleted(Condition condition);
//
//    /**
//     * Gets a chunked collection of to be rebuilt content.
//     *
//     * @param refTime reference time of rebuilding
//     * @return to be rebuilt content
//     * @throws NullPointerException if {@code refTime} is {@code null}
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    Stream<List<C>> getToBeRebuilt(LocalDateTime refTime);
//
//    /**
//     * Register content to database. Create or update is automatically determined.
//     *
//     * @param value content
//     * @return result recording
//     * @throws NullPointerException if {@code value} is {@code null}
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    ContentRecord register(ImportationValue<C> value);
//
//    /**
//     * Register content to database. Create or update is automatically determined.
//     *
//     * @param value content
//     * @return result recording
//     * @throws NullPointerException if {@code value} is {@code null}
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    ContentRecord register(C value);
//
//    /**
//     * Logically delete content registered in the database.
//     *
//     * @param value content
//     * @return result recording
//     * @throws NullPointerException if {@code value} is {@code null}
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    ContentRecord logicalDelete(ImportationValue<C> value);
//
//    /**
//     * Rebuild the content.
//     *
//     * @throws NullPointerException if {@code value} is {@code null}
//     * @throws IllegalArgumentException if {@code value} does not have paired entity
//     * @since 1.0.0
//     */
//    C rebuild(C value);
//
//    /**
//     * Update the content-dependent data in the database. For example, updating derived database tables etc.
//     *
//     * @throws PersistenceException if occurs an exception while access to database
//     * @since 1.0.0
//     */
//    void updateDependents();

    /**
     * Abstract implements of the {@code ImportationService}.
     *
     * @param <E> entity type
     * @param <V> content type
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    abstract class AbstractImportationService<E extends ContentEntity<E>, V extends ContentValue<V>> implements ImportationService<V> {

//        @Inject
//        private CommonBatchDao comDao;
        @Inject
        private JsonService jsonSvc;

        @Inject
        private ConfigService confSvc;
//
//        @Inject
//        private Validator validator;
        @Inject
        private RecordingDxo recDxo;

        /**
         * Get content type.
         *
         * @return content type
         * @since 1.0.0
         */
        protected abstract Class<V> getContentType();

        /**
         * Get data exchange object for content.
         *
         * @return the {@code ImportationDxo}
         * @since 1.0.0
         */
        protected abstract ImportationDxo<E, V> getDxo();

        /**
         * Get data access object for content.
         *
         * @return the {@code ImportationDao}
         * @since 1.0.0
         */
        protected abstract ImportationDao<E> getDao();

        /**
         * {@inheritDoc}
         *
         * @throws PersistenceException if occurs an exception while access to database
         * @since 1.0.0
         */
        @Override
        public void initializeWork() {
            getDao().clearWrk();
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code values} is {@code null}
         * @throws PersistenceException if occurs an exception while access to database
         * @since 1.0.0
         */
        @Override
        public void registerWork(Collection<ImportationValue<V>> values) {
            getDao().appendsWrk(Objects.requireNonNull(values).stream().map(getDxo()::toImportationWorkEntity).toList());
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if any argument is {@code null}
         * @since 1.0.0
         */
        @Override
        public Optional<ImportationValue<V>> toImportationValue(TransResult.Success transResult, Consumer<ContentRecord> failRecorder) {
            Objects.requireNonNull(transResult);
            Objects.requireNonNull(failRecorder);

            final String failureReason;

            try {
                return Optional.of(getDxo().toImportationValue(transResult));
            } catch (ConstraintViolationException ex) {
                failureReason = ex.getConstraintViolations().stream().map(ValidationUtils::toMessageEntry).toList().toString();
            } catch (RuntimeException ex) {
                failureReason = Optional.ofNullable(ThrowableUtils.toMessageEntries(ex))
                        .filter(not(Collection::isEmpty)).map(Collection::toString)
                        .orElse("Occurs unexpected error while build an importation content.");
            }

            failRecorder.accept(recDxo.toFailure(transResult, JobPhase.VALIDATION, failureReason));

            return Optional.empty();
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code values} is {@code null}
         * @since 1.0.0
         */
        @Override
        public ContentMap<ImportationValue<V>> toContentMap(Stream<ImportationValue<V>> values) throws IOException {
            return new ContentMap<>(Objects.requireNonNull(values).map(ImportationValue::asEntry).iterator(),
                    confSvc.getTmpDir(), new ImportationValueSerializer());
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if {@code values} is {@code null}
         * @throws PersistenceException if occurs an exception while access to database
         * @since 1.0.0
         */
        @Override
        public Stream<ImportationValue<V>> getToBeRegistered(Map<String, ImportationValue<V>> values) {
            Set<String> targetIds = Set.copyOf(values.keySet());

            return Stream.concat(
                    getDao().getAdditionalDiferrence(targetIds).map(values::get),
                    getDao().getUpdateDifference(targetIds).map(e -> getDxo().toImportationValue(e, values.get(e.getId())))
            );
        }

//        /**
//         * {@inheritDoc}
//         *
//         * @throws NullPointerException if {@code values} is {@code null}
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public Stream<ImportationValue<C>> getToBeExplicitDeleted(Collection<ImportationValue<C>> values) {
//            return getDao().getContents(Objects.requireNonNull(values).stream()
//                    .filter(ImportationValue::doDelete).map(ImportationValue::getId).toList()
//            ).map(e -> new ImportationValue<>(getDxo().toLogicalDeletion(e), values.get(e.getId()).getSource(), e));
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @throws NullPointerException if {@code condition} is {@code null}
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public long getToBeImplicitDeleteCount(Condition condition) {
//            return getDao().getCountOfLosts(condition);
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @throws NullPointerException if {@code condition} is {@code null}
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public Stream<List<ImportationValue<C>>> getToBeImplicitDeleted(Condition condition) {
//            return getDao().getLosts(additional).map(convertElements(
//                    e -> new ImportationValue<>(getDxo().toLogicalDeletion(e), Map.of(), e)));
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @param refTime reference time of the rebuild
//         * @param resultCollector recording result kind collector
//         * @throws NullPointerException if {@code refTime} is {@code null}
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public void rebuildPersistedContents(LocalDateTime refTime, Consumer<ContentValue> recorder) {
//            Dxo dxo = getDxo();
//            return getDao().getToBeRebuilts(refTime).map(convertElements(dxo::rebuild))
//                    .map(dxo.toValue()).forEach(recorder::accept);
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @throws NullPointerException if {@code value} is {@code null}
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public void register(ImportationValue<C> value) {
//            comDao.persistOrMerge(getDxo().toEntity(Objects.requireNonNull(value)));
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @throws NullPointerException if {@code value} is {@code null}
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public void register(C value) {
//            comDao.persistOrMerge(getDxo().toEntity(Objects.requireNonNull(value)));
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @throws NullPointerException if {@code value} is {@code null}
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public void logicalDelete(ImportationValue<C> value) {
//            comDao.persistOrMerge(getDxo().toEntity(value));
//        }
//
//        /**
//         * {@inheritDoc}
//         *
//         * @throws PersistenceException if occurs an exception while access to database
//         * @since 1.0.0
//         */
//        @Override
//        public void rebuildPersistedContents() {
//            Consumer<RecordableValue> record
//                    = v -> recSvc.rec(recDxo.toSuccess(v, RecordKind.REGISTER));
//
//            try (var rebuilds = getDao().getRebuilds(appTimeSvc.getNow())) {
//                rebuilds.forEach(chunk -> {
//                    chunk.stream().map(getDxo()::rebuild)
//                            .forEach(c(record).andThen(comDao::merge));
//                    comDao.flushAndClear();
//                });
//            }
//
//            rebuildDependent();
//        }
//
//        /**
//         * Rebuilds content-dependent database tables.
//         *
//         * @throws PersistenceException if occurs problem in persistence provider
//         */
//        @Transactional(Transactional.TxType.MANDATORY)
//        public void rebuildDependent() {
//            getDao().doUniqueRebuilding();
//        }
//
//        @Transactional(Transactional.TxType.MANDATORY)
//        public void initializeImportationWork() {
//
//        }
//
//        @Transactional(Transactional.TxType.MANDATORY)
//        public Stream<TransResult.Success> extractSuccessfulsAndCollectFailures(Stream<TransResult> transformed) {
//
//        }
//
//        public ContentMap<ImportationValue<C>> toContentMap(Stream<ImportationValue<C>> values) throws IOException {
//            return new ContentMap<>(values.map(ImportationValue::asEntry).iterator(), confSvc.getTmpDir(), new ContentConvertor());
//        }
//
//        private class ContentConvertor implements ContentMap.Convertor<ImportationValue<C>> {
//
//            /**
//             * {@inheritDoc}
//             *
//             * @since 1.0.0
//             */
//            @Override
//            public String serialize(ImportationValue<C> c) {
//                return jsonb.toJson(c);
//            }
//
//            /**
//             * {@inheritDoc}
//             *
//             * @since 1.0.0
//             */
//            @Override
//            public ImportationValue<C> deserialize(String s) {
//                JsonObject j = jsonSvc.toJsonObject(Objects.requireNonNull(s));
//                return new ImportationValue<>(
//                        jsonSvc.convertViaJson(j.get("content"), getContentClass()),
//                        jsonSvc.toStringMap(j.getJsonObject("source")));
//            }
//        }
//
//        public void registerToImportationWork(Collection<ImportationValue<C>> contents) {
//
//        }
//
//        public Condition buildConditionForExtractingImplicitDeletion(Condition additionalCondition) {
//
//        }
//
//        public void collectDuplicateAsFailure(ImportationValue<C> duplicate) {
//            d -> recSvc.rec(recDxo.toFailure(d, JobPhase.VALIDATION, List.of("Duplicate id.")));
//        }
//
//        public void collectDeniedDeletionAsFailure(ImportationValue<C> value) {
//            v -> recSvc.rec(recDxo.toFailure(v, JobPhase.PROVISIONING,
//                    List.of("Ignored because the limit was exceeded.")));
//        }
        private class ImportationValueSerializer implements ContentMap.Convertor<ImportationValue<V>> {

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public String serialize(ImportationValue<V> value) {
                return jsonSvc.toJson(value);
            }

            /**
             * {@inheritDoc}
             *
             * @since 1.0.0
             */
            @Override
            public ImportationValue<V> deserialize(String value) {
                JsonObject jo = jsonSvc.toJsonObject(Objects.requireNonNull(value));
                return new ImportationValue<>(jsonSvc.convertViaJson(jo.get("content"), getContentType()),
                        jsonSvc.toStringMap(jo.getJsonObject("source")));
            }
        }
    }

//    /**
//     * Batch processing for user content.
//     *
//     * @author riru
//     * @version 1.0.0
//     * @since 1.0.0
//     */
//    @RequestScoped
//    class UserImportationService extends AbstractImportationService<UserEntity, UserValue>
//            implements ImportationService<UserValue> {
//
//        @Inject
//        private UserImportationDxo dxo;
//
//        @Inject
//        private UserImportationDao dao;
//
//        @Override
//        protected UserImportationDxo getDxo() {
//            return dxo;
//        }
//
//        @Override
//        protected UserImportationDao getDao() {
//            return dao;
//        }
//
//        @Override
//        protected Class<UserValue> getContentType() {
//            return UserValue.class;
//        }
//    }
}
