package trial;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import jp.mydns.projectk.safi.constant.AttName;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.producer.EntityManagerProducer.ForBatch;
import jp.mydns.projectk.safi.value.ValidityPeriod;

@Path("persistence")
public class PersistenceResource {

    @Inject
    @ForBatch
    private EntityManager em;
    
    @Inject
    private Validator validator;

    @GET
    @Path("c")
    @Transactional
    public Response c() {

        var u = new UserEntity();

        u.setId("id");
        u.setEnabled(true);
        u.setName("taro");
        u.setAtts(Map.of(AttName.ATT01, "a1",AttName.ATT02, "a2",
                AttName.ATT03, "a3",AttName.ATT04, "a4",AttName.ATT05, "a5",
                AttName.ATT06, "a6",AttName.ATT07, "a7",AttName.ATT08, "a8",
                AttName.ATT09, "a9",AttName.ATT10, "aa"));
        u.setValidityPeriod(new ValidityPeriod.Builder().build(validator));
        u.setDigest("x");
        u.setNote("Trial.");
        u.setVersion(0);

        em.persist(u);

        return Response.ok().build();
    }

    @GET
    @Path("r")
    @Transactional
    public Response r() {

        return Response.ok(em.find(UserEntity.class, "id").toString()).build();
    }
}
