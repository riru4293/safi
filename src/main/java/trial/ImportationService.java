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
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.unmodifiableSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import jp.mydns.projectk.safi.constant.JobPhase;
import jp.mydns.projectk.safi.constant.RecordKind;
import jp.mydns.projectk.safi.dao.CommonBatchDao;
import jp.mydns.projectk.safi.dao.ContentImportationDao;
import jp.mydns.projectk.safi.dao.UserImportationDao;
import jp.mydns.projectk.safi.entity.ContentEntity;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.service.AppTimeService;
import jp.mydns.projectk.safi.service.ConfigService;
import jp.mydns.projectk.safi.service.JsonService;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.ContentMap;
import jp.mydns.projectk.safi.value.ContentValue;
import jp.mydns.projectk.safi.value.RecordableValue;
import jp.mydns.projectk.safi.value.TransResult;
import jp.mydns.projectk.safi.value.UserValue;

/**
 * Facade for the content import process.
 *
 * @param <C> content type
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImportationService<C extends ContentValue> {

    void initializeWork();

    void registerWork(Collection<ImportationValue<C>> values);

    Optional<ImportationValue<C>> toImportationValue(TransResult.Success trunsResults, Consumer<String> failureReasonCollector);

    ContentMap<ImportationValue<C>> toContentMap(Stream<ImportationValue<C>> values) throws IOException;

    Stream<ImportationValue<C>> getToBeRegistered(Map<String, ImportationValue<C>> values);

    Stream<ImportationValue<C>> getToBeExplicitDeleted(Map<String, ImportationValue<C>> values);

    Condition buildConditionForImplicitDeletion(Condition additionalCondition);

    long getToBeImplicitDeleteCount(Condition condition);

    Stream<List<ImportationValue<C>>> getToBeImplicitDeleted(Condition condition);

    void register(ImportationValue<C> value);

    void logicalDelete(ImportationValue<C> value);

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
    @Dependent
    abstract class AbstractImportationService<E extends ContentEntity, C extends ContentValue> {

        @Inject
        private RecordDxo recDxo;

        @Inject
        private CommonBatchDao comDao;

        @Inject
        private JobRecordingService recSvc;

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

        protected abstract Class<C> getContentType();

        protected abstract ContentBatchDxo<E, C> getDxo();

        protected abstract ContentImportationDao<E> getDao();

        void initializeWork() {

        }

        void registerWork(Collection<ImportationValue<C>> values) {

        }

        Optional<ImportationValue<C>> toImportationValue(TransResult.Success transformed) {

            List<String> failureReasons = new ArrayList<>();

            try {

                return Optional.of(getDxo().toValue(transformed));

            } catch (ConstraintViolationException ex) {

                ex.getConstraintViolations().stream()
                        .map(ConstraintViolationUtils::toMessage)
                        .forEach(failureReasons::add);

            } catch (RuntimeException ex) {

                failureReasons.addAll(ThrowableUtils.toMessages(ex));

            }

            recSvc.rec(recDxo.toFailure(transformed, JobPhase.VALIDATION, failureReasons));

            return Optional.empty();
        }

        /**
         * Get values that to be registered.
         * <p>
         * Returns a fully validated value. Constraint violations are excluded and recorded.
         *
         * @param values collection of the {@code ImportationValue}
         * @param opts job options
         * @return values that to be registered.
         * @throws NullPointerException if {@code values} is {@code null}
         * @throws PersistenceException if occurs problem in persistence provider
         */
        public Stream<ImportationValue<C>> getToBeRegistered(Map<String, ImportationValue<C>> values) {

            Set<String> ids = unmodifiableSet(values.keySet());
            boolean allowAlways = ctx.allowUpdateAnotherLabel();

            Function<E, Stream<ImportationValue<C>>> buildRegisterValue = e -> {
                ImportationValue<C> v = values.get(e.getId());

                return allowAlways || sameLabel(v, e)
                        ? Stream.of(getDxo().toValue(v, e))
                        : Stream.empty();
            };

            return Stream.concat(
                    getDao().getAdditions(ids).map(values::get),
                    getDao().getUpdates(ids).flatMap(buildRegisterValue)
            ).map(this::validate).flatMap(Optional::stream);
        }

        private boolean sameLabel(ImportationValue<C> v, E e) {
            return Objects.equals(v.getContent().getLabel(), e.getLabel());
        }

        private Optional<ImportationValue<C>> validate(ImportationValue<C> value) {

            Set<ConstraintViolation<C>> cv = validator.validate(value.getContent());

            if (cv.isEmpty()) {
                return Optional.of(value);
            }

            List<String> failureReasons = cv.stream()
                    .map(ConstraintViolationUtils::toMessage).toList();

            recSvc.rec(recDxo.toFailure(value, JobPhase.VALIDATION, failureReasons));

            return Optional.empty();
        }

        /**
         * Get values that to be explicit deleted.
         *
         * @param values collection of the {@code ImportationValue}
         * @return values that to be explicit deleted
         * @throws NullPointerException if {@code values} is {@code null}
         * @throws PersistenceException if occurs problem in persistence provider
         */
        public Stream<ImportationValue<C>> getToBeExplicitDeleted(Map<String, ImportationValue<C>> values) {

            List<String> ids = values.values().stream().filter(ImportationValue::doDelete)
                    .map(ImportationValue::getId).toList();

            return getDao().getContents(ids).map(e -> new ImportationValue<>(
                    getDxo().toLogicalDeletion(e), values.get(e.getId()).getSource(), e));
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
