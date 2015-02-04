package ru.taskurotta.hz.test.mongo.serialization;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.taskurotta.hz.test.mongo.serialization.custom.impl.TaskContainerDbObject;
import ru.taskurotta.internal.core.TaskType;
import ru.taskurotta.mongodb.driver.DBObjectСheat;
import ru.taskurotta.mongodb.driver.impl.BDecoderFactory;
import ru.taskurotta.mongodb.driver.impl.BEncoderFactory;
import ru.taskurotta.service.hz.serialization.bson.TaskContainerSerializer;
import ru.taskurotta.transport.model.ArgContainer;
import ru.taskurotta.transport.model.TaskContainer;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by greg on 23/01/15.
 */
@Ignore
public class MongoSerializationTest {

    private MongoTemplate getMongoTemplate() throws UnknownHostException {
        ServerAddress serverAddress = new ServerAddress("127.0.0.1", 27017);
        MongoClient mongoClient = new MongoClient(serverAddress);

        WriteConcern writeConcern = new WriteConcern(1, 0, false, true);

        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test-mongo");
        mongoTemplate.setWriteConcern(writeConcern);
        return mongoTemplate;
    }

    @Test
    @Ignore
    public void testWithCustom() throws Exception {

        MongoTemplate mongoTemplate = getMongoTemplate();

        DBCollection withCol = mongoTemplate.getCollection("clcustom");

        TaskContainerSerializer taskContainerSerializer = new TaskContainerSerializer();

        withCol.setObjectClass(TaskContainerDbObject.class);
        withCol.setDBEncoderFactory(new BEncoderFactory(taskContainerSerializer));
        withCol.setDBDecoderFactory(new BDecoderFactory(taskContainerSerializer));

        List<ObjectId> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TaskContainer taskContainer = createTaskContainer();
            DBObjectСheat dbObject = new DBObjectСheat(taskContainer);
            WriteResult writeResult = withCol.insert(dbObject);
            list.add((ObjectId) writeResult.getUpsertedId());
        }

        try (DBCursor cursor = withCol.find()) {
            while (cursor.hasNext()) {
                DBObjectСheat obj = (DBObjectСheat) cursor.next();
                System.out.println("actorId = " + ((TaskContainer) obj.getObject()).getActorId());
            }
        }
    }

    private TaskContainer createTaskContainer() {
        UUID taskId = UUID.randomUUID();
        String method = "method";
        String actorId = "actorId#" + taskId.toString();
        TaskType type = TaskType.DECIDER_START;
        long startTime = 15121234;
        int errorAttempts = 2;

        List<ArgContainer> containerList = new ArrayList<>();
        ArgContainer argContainer1 = new ArgContainer();
        argContainer1.setTaskId(UUID.randomUUID());
        argContainer1.setDataType("simple1");
        argContainer1.setJSONValue("jsonData1");
        argContainer1.setPromise(false);
        argContainer1.setReady(true);
        argContainer1.setValueType(ArgContainer.ValueType.COLLECTION);

        ArgContainer argContainer2 = new ArgContainer();
        argContainer2.setTaskId(UUID.randomUUID());
        argContainer2.setDataType("simple1");
        argContainer2.setJSONValue("jsonData1");
        argContainer2.setPromise(false);
        argContainer2.setReady(true);
        argContainer2.setValueType(ArgContainer.ValueType.COLLECTION);

        argContainer2.setCompositeValue(new ArgContainer[]{argContainer1});

        containerList.add(argContainer1);
        containerList.add(argContainer2);

        ArgContainer[] args = new ArgContainer[containerList.size()];
        containerList.toArray(args);
        UUID processId = UUID.randomUUID();
        String[] failTypes = {"java.lang.RuntimeException"};
        return new TaskContainer(taskId, processId, method, actorId, type, startTime, errorAttempts, args, null, true, failTypes);
    }

}
