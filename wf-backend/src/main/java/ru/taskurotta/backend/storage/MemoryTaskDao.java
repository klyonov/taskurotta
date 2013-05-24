package ru.taskurotta.backend.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.taskurotta.transport.model.DecisionContainer;
import ru.taskurotta.transport.model.TaskContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: moroz
 * Date: 09.04.13
 */
public class MemoryTaskDao implements TaskDao {

    private final static Logger logger = LoggerFactory.getLogger(MemoryTaskDao.class);

    private Map<UUID, TaskContainer> id2TaskMap = new ConcurrentHashMap<UUID, TaskContainer>();
    private Map<UUID, DecisionContainer> id2TaskDecisionMap = new ConcurrentHashMap<UUID, DecisionContainer>();


    @Override
    public void addDecision(DecisionContainer taskDecision) {
        id2TaskDecisionMap.put(taskDecision.getTaskId(), taskDecision);
    }

    @Override
    public TaskContainer getTask(UUID taskId) {
        return id2TaskMap.get(taskId);
    }

    @Override
    public void addTask(TaskContainer taskContainer) {
        id2TaskMap.put(taskContainer.getTaskId(), taskContainer);
    }

    @Override
    public DecisionContainer getDecision(UUID taskId) {
        return id2TaskDecisionMap.get(taskId);
    }

    @Override
    public boolean isTaskReleased(UUID taskId) {
        return id2TaskDecisionMap.containsKey(taskId);
    }

    @Override
    public List<TaskContainer> getProcessTasks(UUID processUuid) {
        if(processUuid == null) {
            return null;
        }
        List<TaskContainer> result = new ArrayList<>();
        for(TaskContainer tc: id2TaskMap.values()) {
            if(processUuid.equals(tc.getProcessId())) {
                result.add(tc);
            }
        }
        return result;
    }

    @Override
    public void updateTask(TaskContainer taskContainer) {
        //No need to implement it for in-memory storage case
    }
}
