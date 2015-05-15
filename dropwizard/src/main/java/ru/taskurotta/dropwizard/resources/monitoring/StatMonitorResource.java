package ru.taskurotta.dropwizard.resources.monitoring;

import org.springframework.beans.factory.annotation.Required;
import ru.taskurotta.service.console.retriever.StatInfoRetriever;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 15.05.2015.
 */
@Path("/monitoring/stats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public class StatMonitorResource {

    private StatInfoRetriever statInfoRetriever;

    @GET
    public String showStats() {
        return statInfoRetriever.getNodeStats();
    }

    @Required
    public void setStatInfoRetriever(StatInfoRetriever statInfoRetriever) {
        this.statInfoRetriever = statInfoRetriever;
    }
}