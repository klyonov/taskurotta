package ru.taskurotta.backend.ora;

import java.util.UUID;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.taskurotta.backend.dependency.links.Graph;
import ru.taskurotta.backend.ora.dao.DbConnect;
import ru.taskurotta.backend.ora.dependency.OraGraphDao;

/**
 * User: moroz
 * Date: 17.04.13
 */
public class DependencyTest {

    OraGraphDao dao = new OraGraphDao();

    @Before
    public void prepare() {
        dao.setDataSource(new DbConnect().getDataSource());
    }

    @Test
    public void test() {
        UUID graphId = UUID.randomUUID();
        dao.createGraph(graphId, UUID.randomUUID());
        Graph graph = dao.getGraph(graphId);
        Assert.assertEquals(graph.getGraphId(), graphId);
    }


}
