package ru.taskurotta.service.dependency.links;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.taskurotta.service.common.ResultSetCursor;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: romario
 * Date: 4/5/13
 * Time: 11:23 AM
 */
public class MemoryGraphDao implements GraphDao {

    private final static Logger logger = LoggerFactory.getLogger(MemoryGraphDao.class);

    // TODO: garbage collection policy for real database
    private Map<UUID, GraphRow> graphs = new ConcurrentHashMap<UUID, GraphRow>();

    private Object newGraphLock = new Object();

    //TODO: find and delete usage
    //TODO: configuration support
    private int retryTimes = 100;

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }


    /**
     * Table of row contains current graph (process) state
     */
    public static class GraphRow {
        private int version;
        private String jsonGraph;

        protected GraphRow(Graph graph) {
            version = graph.getVersion();
            dump(graph);
        }


        /**
         * @param modifiedGraph
         * @return true if modification was successful
         */
        protected synchronized boolean updateGraph(Graph modifiedGraph) {

            int newVersion = modifiedGraph.getVersion();

            if (version != newVersion - 1) {
                return false;
            }
            version = newVersion;
            dump(modifiedGraph);

            return true;
        }


        protected void dump(Graph graph) {
            try {
                jsonGraph = mapper.writeValueAsString(graph);
            } catch (JsonProcessingException e) {
                // TODO: create new RuntimeException type
                throw new RuntimeException("Can not create json String from Object: " + graph, e);
            }
        }


        protected Graph parse() {

            try {
                return mapper.readValue(jsonGraph, Graph.class);
            } catch (IOException e) {
                // TODO: create new RuntimeException type
                throw new RuntimeException("Can not instantiate Object from json. JSON value: " + jsonGraph, e);
            }
        }


    }


    @Override
    public void createGraph(UUID graphId, UUID taskId) {

        if (graphs.get(graphId) != null) {
            return;
        }

        // too ugly
        synchronized (newGraphLock) {

            if (graphs.get(graphId) != null) {
                return;
            }

            Graph graph = new Graph(graphId, taskId);
            GraphRow graphRow = new GraphRow(graph);

            graphs.put(graphId, graphRow);
        }
    }

    @Override
    public void deleteGraph(UUID graphId) {
        graphs.remove(graphId);
    }

    @Override
    public Graph getGraph(UUID graphId) {

        GraphRow graphRow = graphs.get(graphId);

        if (graphRow == null) {
            return null;
        }

        return graphRow.parse();
    }

    private boolean updateGraph(Graph modifiedGraph) {

        logger.debug("updateGraph() modifiedGraph = [{}]", modifiedGraph);

        GraphRow graphRow = graphs.get(modifiedGraph.getGraphId());

        return graphRow.updateGraph(modifiedGraph);
    }


    @Override
    public boolean changeGraph(Updater updater) {

        UUID processId = updater.getProcessId();

        for (int i = 0; i < retryTimes; i++) {

            Graph graph = getGraph(processId);

            if (!updater.apply(graph)) {
                break;
            }

            if (updateGraph(graph)) {
                return true;
            }
        }

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultSetCursor<UUID> findLostGraphs(long lastGraphChangeTime, int batchSize) {
        throw new UnsupportedOperationException("Please, use MongoGraphDao");
    }
}
