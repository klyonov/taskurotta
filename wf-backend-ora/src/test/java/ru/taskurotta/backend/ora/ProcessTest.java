package ru.taskurotta.backend.ora;

import org.junit.Test;
import ru.taskurotta.backend.ora.checkpoint.OracleCheckpointService;
import ru.taskurotta.backend.ora.dao.DbConnect;
import ru.taskurotta.backend.ora.storage.OraProcessBackend;
import ru.taskurotta.backend.storage.model.TaskContainer;

/**
 * User: moroz
 * Date: 29.04.13
 */
public class ProcessTest {

    private DbConnect connection = new DbConnect();
    private OraProcessBackend dao = new OraProcessBackend(connection.getDataSource(), new OracleCheckpointService(connection.getDataSource()));

    @Test
    public void test() {
        TaskContainer task = SerializationTest.createTaskContainer();
        dao.startProcess(task);

        TaskContainer task1 = SerializationTest.createTaskContainer();
        dao.startProcess(task1);

        dao.finishProcess(task.getProcessId(), "Process result");
    }

}
