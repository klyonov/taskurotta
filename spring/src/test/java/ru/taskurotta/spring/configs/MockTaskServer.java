package ru.taskurotta.spring.configs;

import ru.taskurotta.server.TaskServer;
import ru.taskurotta.transport.model.DecisionContainer;
import ru.taskurotta.transport.model.TaskContainer;
import ru.taskurotta.util.ActorDefinition;

import java.util.UUID;

public class MockTaskServer implements TaskServer {
    @Override
    public void startProcess(TaskContainer task) {

    }

    @Override
    public TaskContainer poll(ActorDefinition actorDefinition) {
        return null;
    }

    @Override
    public void release(DecisionContainer taskResult) {

    }

    @Override
    public void updateTaskTimeout(UUID taskId, UUID processId, long timeout) {

    }
}
