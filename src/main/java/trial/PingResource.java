package trial;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.slf4j.LoggerFactory;

@Path("trial")
public class PingResource {

    java.lang.System.Logger jul = System.getLogger("JUL");
    org.slf4j.Logger slf4j = LoggerFactory.getLogger("SLF4J");
    org.apache.log4j.Logger log4j = org.apache.log4j.LogManager.getLogger("LOG4J");
    org.apache.commons.logging.Log jcl = org.apache.commons.logging.LogFactory.getLog("JCL");

    @GET
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
}
