package ru.taskurotta.dropwizard.resources.console;

import ru.taskurotta.backend.console.model.ProcessVO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: dimadin
 * Date: 24.05.13 11:54
 */
@Path("/console/process/{processId}")
public class ProcessResource extends BaseResource {

    @GET
    public Response getProcess(@PathParam("processId") String processId) {

        try {
            ProcessVO process = consoleManager.getProcess(UUID.fromString(processId));
            logger.debug("Process getted by id[{}] is [{}]", processId, process);
            return Response.ok(process, MediaType.APPLICATION_JSON).build();
        } catch(Throwable e) {
            logger.error("Error at getting process by id["+processId+"]", e);
            return Response.serverError().build();
        }

    }

}
