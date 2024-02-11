package trial;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import java.lang.System.Logger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import jp.mydns.projectk.safi.dao.ImportWorkDao;
import jp.mydns.projectk.safi.dao.UserImportationDao;
import jp.mydns.projectk.safi.entity.ImportWorkEntity;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.producer.EntityManagerProducer.ForBatch;
import jp.mydns.projectk.safi.service.ContentDigestGenerator;
import static jp.mydns.projectk.safi.util.LambdaUtils.p;
import jp.mydns.projectk.safi.value.UserValue;
import jp.mydns.projectk.safi.value.ValidityPeriod;

@RequestScoped
public class TrialService {

    private static final Logger logger = System.getLogger(TrialService.class.getName());

    @Inject
    @ForBatch
    private EntityManager em;

    @Inject
    private UserImportationDao userBatchDao;

    @Inject
    private ImportWorkDao importWorkDao;

    @Inject
    private Validator validator;

    @Inject
    private ContentDigestGenerator digestGenerator;

    @Transactional
    public void doImport1() {

    }

    @Transactional
    public void doImport2() {

        final ValidityPeriod valid = new ValidityPeriod.Builder().build(validator);
        final ValidityPeriod invalid = new ValidityPeriod.Builder().withFrom(ValidityPeriod.defaultTo())
                .withTo(ValidityPeriod.defaultFrom()).withBan(true).build(validator);

        List<UserValue> users = List.of(
                new UserValue.Builder(digestGenerator).withId("u1").withEnabled(valid.isEnabled(LocalDateTime.now()))
                        .withName("user-1").withValidityPeriod(valid).withAtts(Map.of()).build(validator),
                new UserValue.Builder(digestGenerator).withId("u2").withEnabled(invalid.isEnabled(LocalDateTime.now()))
                        .withName("user-2").withValidityPeriod(invalid).withAtts(Map.of()).build(validator)
        );

    }

    @Transactional
    public void doImport(List<UserValue> users) {

        final ValidityPeriod valid = new ValidityPeriod.Builder().build(validator);
        final ValidityPeriod invalid = new ValidityPeriod.Builder().withFrom(ValidityPeriod.defaultTo())
                .withTo(ValidityPeriod.defaultFrom()).withBan(true).build(validator);

        Optional<UserEntity> u1 = Optional.ofNullable(em.find(UserEntity.class, "u1"));

        final boolean isFirst = u1.isEmpty();
        final boolean isSecond = !isFirst || u1.map(UserEntity::getDigest).filter("x"::equals).isPresent();
        final boolean isThird = !isSecond;

        importWorkDao.clear();
        importWorkDao.appends(users.stream().map(u -> {
            var e = new ImportWorkEntity();
            e.setId(u.getId());
            e.setDigest(u.getDigest());
            return e;
        }));
        Set<String> toBeAdded = userBatchDao.getAdditions(users.stream().map(UserValue::getId).collect(toSet())).collect(toSet());
        users.stream().filter(p(toBeAdded::contains, UserValue::getId))
                .map(v -> {
                    var e = new UserEntity();
                    e.setId(v.getId());
                    e.setEnabled(v.isEnabled());
                    e.setName(v.getName());
                    e.setAtts(v.getAtts());
                    e.setValidityPeriod(v.getValidityPeriod());
                    e.setDigest(v.getDigest());
                    e.setNote(v.getNote());
                    e.setVersion(v.getVersion());
                    return e;
                }).forEach(em::persist);
    }
}
