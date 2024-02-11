package trial;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.validation.Validator;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import jp.mydns.projectk.safi.constant.ContentKind;
import jp.mydns.projectk.safi.constant.JobKind;
import jp.mydns.projectk.safi.constant.JobStatus;
import jp.mydns.projectk.safi.service.BatchService;
import jp.mydns.projectk.safi.value.Condition;
import jp.mydns.projectk.safi.value.Filtdef;
import jp.mydns.projectk.safi.value.FilteringOperation;
import jp.mydns.projectk.safi.value.Job;
import jp.mydns.projectk.safi.value.Jobdef;
import jp.mydns.projectk.safi.value.Plugdef;
import jp.mydns.projectk.safi.value.ValidityPeriod;
import org.slf4j.LoggerFactory;

@Path("trial")
@RequestScoped
public class TrialResource {

    java.lang.System.Logger jul = System.getLogger("JUL");
    org.slf4j.Logger slf4j = LoggerFactory.getLogger("SLF4J");
    org.apache.log4j.Logger log4j = org.apache.log4j.LogManager.getLogger("LOG4J");
    org.apache.commons.logging.Log jcl = org.apache.commons.logging.LogFactory.getLog("JCL");

    @Inject
    private TrialService svc;

    @Inject
    private BatchService batchSvc;

    @Inject
    private Validator validator;

    @GET
    @Path("ping")
    public Response ping() {

        jul.log(System.Logger.Level.DEBUG, "JUL!-1");
        slf4j.debug("SLF4J!-1");
        log4j.debug("LOG4J-1");
        jcl.debug("JCL!-1");

        jul.log(System.Logger.Level.INFO, "JUL!-2");
        slf4j.info("SLF4J!-2");
        log4j.info("LOG4J-2");
        jcl.info("JCL!-2");

        jul.log(System.Logger.Level.WARNING, "JUL!-3");
        slf4j.warn("SLF4J!-3");
        log4j.warn("LOG4J-3");
        jcl.warn("JCL!-3");

        jul.log(System.Logger.Level.ERROR, "JUL!-4");
        slf4j.error("SLF4J!-4");
        log4j.error("LOG4J-4");
        jcl.error("JCL!-4");

        return Response
                .ok("Ping trial")
                .build();
    }

    @GET
    @Path("import1")
    public Response doImport1() {
        svc.doImport1();
        return Response.ok("success").build();
    }

    @GET
    @Path("import2")
    public Response doImport2() {
        svc.doImport2();
        return Response.ok("success").build();
    }

    @GET
    @Path("parallel")
    public Response parallel() {
        IntStream.range(Integer.MIN_VALUE, Integer.MAX_VALUE).parallel().forEach(System.out::println);
        return Response.ok("success").build();
    }

    @GET
    @Path("batch")
    public Response batch() {
        Job job = new Job.Builder().withBeginTime(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withEndTime(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withId("id")
                .withLimitTime(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withMessages(List.of())
                .withOptions(JsonValue.EMPTY_JSON_OBJECT)
                .withSchedefId("schedef")
                .withScheduleTime(OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .withStatus(JobStatus.RUNNING)
                .withVersion(0)
                .withJobdef(new Jobdef.Builder()
                        .withContentKind(ContentKind.USER)
                        .withId("jobdef")
                        .withJobKind(JobKind.IMPORT)
                        .withName("name")
                        .withOptions(JsonValue.EMPTY_JSON_OBJECT)
                        .withTimeout(Duration.ZERO)
                        .withTrnsdef(Map.of())
                        .withValidityPeriod(new ValidityPeriod.Builder().build(validator))
                        .withVersion(0)
                        .withFiltdef(new Filtdef.Builder()
                                .withTrnsdef(Map.of())
                                .withFilter(new Condition.Single() {
                                    @Override
                                    public String getName() {
                                        return "name";
                                    }

                                    @Override
                                    public String getValue() {
                                        return "value";
                                    }

                                    @Override
                                    public boolean isMulti() {
                                        return false;
                                    }

                                    @Override
                                    public FilteringOperation getOperation() {
                                        return FilteringOperation.Single.EQUAL;
                                    }
                                })
                                .build(validator))
                        .withPlugdef(new Plugdef() {
                            @Override
                            public String getName() {
                                return "plugin";
                            }

                            @Override
                            public JsonObject getArgs() {
                                return JsonObject.EMPTY_JSON_OBJECT;
                            }
                        })
                        .build(validator))
                .build(validator);
        batchSvc.startBatchAsync(job);
        return Response.ok("success").build();
    }
}
