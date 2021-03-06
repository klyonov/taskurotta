package ru.taskurotta.dropwizard.resources.console.actors;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import ru.taskurotta.service.config.ConfigService;
import ru.taskurotta.service.console.manager.ActorConfigManager;
import ru.taskurotta.service.console.model.ActorExtVO;
import ru.taskurotta.service.console.model.ActorVO;
import ru.taskurotta.service.console.model.GenericPage;
import ru.taskurotta.service.console.model.MetricsStatDataVO;
import ru.taskurotta.service.console.retriever.metrics.MetricsMethodDataRetriever;
import ru.taskurotta.service.metrics.RateUtils;
import ru.taskurotta.service.metrics.model.QueueBalanceVO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: stukushin, dimadin
 * Date: 04.09.13 17:06
 */
@Path("/console/actor")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ActorListResource {

    private static final Logger logger = LoggerFactory.getLogger(ActorListResource.class);

    private ActorConfigManager actorConfigManager;

    private ConfigService configService;

    private MetricsMethodDataRetriever metricsDataRetriever;

    @POST
    @Path("/block")
    public Response blockActor(String actorId) {
        try {
            logger.debug("Blocking actor [{}]", actorId);
            configService.blockActor(actorId);

            return Response.ok().build();
        } catch (Throwable e) {
            logger.error("Catch exception while blocking actor [" + actorId + "]", e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/unblock")
    public Response unblockActor(String actorId) {
        try {
            logger.debug("Unblocking actor [{}]", actorId);
            configService.unblockActor(actorId);

            return Response.ok().build();
        } catch (Throwable e) {
            logger.error("Catch exception while unblocking actor [" + actorId + "]", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/list")
    public Response listActors(@QueryParam("pageNum") Optional<Integer> pageNum, @QueryParam("pageSize") Optional<Integer> pageSize, @QueryParam("filter") Optional<String> oFilter) {

        try {
            GenericPage<ActorExtVO> extActors = extendActorFeatures(actorConfigManager.getActorList(
                    pageNum.or(1), pageSize.or(10), oFilter.orNull()));
            logger.debug("Actor list (ext) got is [{}]", extActors);

            return Response.ok(extActors, MediaType.APPLICATION_JSON).build();

        } catch (Throwable e) {
            logger.error("Error listing actors", e);
            return Response.serverError().build();
        }

    }

    private GenericPage<ActorExtVO> extendActorFeatures(GenericPage<ActorVO> actors) {
        GenericPage<ActorExtVO> result = null;
        if (actors != null) {
            List<ActorExtVO> items = new ArrayList<>();
            if (actors.getItems() != null && !actors.getItems().isEmpty()) {
                for (ActorVO actor : actors.getItems()) {
                    items.add(createActorExtVO(actor));
                }
            }
            result = new GenericPage<ActorExtVO>(items, actors.getPageNumber(), actors.getPageSize(), actors.getTotalCount());
        }

        return result;
    }

    @POST
    @Path("/metrics/compare")
    public Map<String, Collection<MetricsStatDataVO>> getMetricsDataOnActors(CompareCommand command) {

        Map<String, Collection<MetricsStatDataVO>> result = null;

        if (isContainsData(command)) {
            result = actorConfigManager.getMetricsData(command.getMetrics(), command.getActorIds());
        }

        logger.debug("Metrics data got by command [{}] are [{}]", command, result);

        return result;
    }

    private boolean isContainsData(CompareCommand command) {
        return command != null
                && command.getActorIds() != null
                && command.getMetrics() != null
                && !command.getActorIds().isEmpty() && !command.getMetrics().isEmpty();
    }

    @GET
    @Path("/metrics/compare")
    public Response getAvailableMetricsList() {
        Collection<String> metrics = metricsDataRetriever.getMetricNames();
        logger.debug("Metrics getted are [{}]", metrics);
        return Response.ok(metrics, MediaType.APPLICATION_JSON).build();
    }

    public static class CompareCommand implements Serializable {
        private List<String> actorIds;
        private List<String> metrics;

        public List<String> getActorIds() {
            return actorIds;
        }

        public void setActorIds(List<String> actorIds) {
            this.actorIds = actorIds;
        }

        public List<String> getMetrics() {
            return metrics;
        }

        public void setMetrics(List<String> metrics) {
            this.metrics = metrics;
        }

        @Override
        public String toString() {
            return "CompareCommand{" +
                    "actorIds=" + actorIds +
                    ", metrics=" + metrics +
                    "} ";
        }
    }

    @GET
    @Path("/info")
    public Response getInfo(@QueryParam("actorId") String actorId) {
        return Response.ok(actorConfigManager.getActorFullVo(actorId), MediaType.APPLICATION_JSON).build();
    }

    private ActorExtVO createActorExtVO(ActorVO actorVO) {
        ActorExtVO extActor = new ActorExtVO(actorVO);
        QueueBalanceVO queueBalanceVO = actorConfigManager.getQueueState(actorVO.getId());
        extActor.setQueueState(queueBalanceVO);
        if (queueBalanceVO != null) {
            extActor.setDayRate(RateUtils.getOverallRate(queueBalanceVO.getTotalInDay(),
                    queueBalanceVO.getInDayPeriod(), queueBalanceVO.getTotalOutDay(),
                    queueBalanceVO.getOutDayPeriod()));
            extActor.setHourRate(RateUtils.getOverallRate(queueBalanceVO.getTotalInHour(),
                    queueBalanceVO.getInHourPeriod(), queueBalanceVO.getTotalOutHour(),
                    queueBalanceVO.getOutHourPeriod()));
        }
        return extActor;
    }

    @Required
    public void setActorConfigManager(ActorConfigManager actorConfigManager) {
        this.actorConfigManager = actorConfigManager;
    }

    @Required
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Required
    public void setMetricsDataRetriever(MetricsMethodDataRetriever metricsDataRetriever) {
        this.metricsDataRetriever = metricsDataRetriever;
    }

}
