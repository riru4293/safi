package trial;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jp.mydns.projectk.safi.entity.UserEntity;
import jp.mydns.projectk.safi.producer.EntityManagerProducer.ForBatch;

@Path("persistence")
public class PersistenceResource {

    @Inject
    @ForBatch
    private EntityManager em;

    @GET
    @Path("c")
    @Transactional
    public Response c() {

        var u = new UserEntity();

        u.setId("id");
        u.setName("taro");
        u.setEnabled(true);
        u.setDigest("x");
        u.setNote("Trial.");

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
