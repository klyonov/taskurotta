package ru.taskurotta.server.recovery;

import ru.taskurotta.backend.checkpoint.CheckpointService;
import ru.taskurotta.backend.checkpoint.TimeoutType;
import ru.taskurotta.backend.checkpoint.model.Checkpoint;
import ru.taskurotta.backend.queue.QueueBackend;
import ru.taskurotta.backend.storage.ProcessBackend;
import ru.taskurotta.backend.storage.TaskBackend;
import ru.taskurotta.backend.storage.model.TaskContainer;
import ru.taskurotta.server.recovery.base.AbstractIterableRecovery;

public class ProcessBackendEnqueueTaskRecovery extends AbstractIterableRecovery {

    private TaskBackend taskBackend;
    private QueueBackend queueBackend;
    private ProcessBackend processBackend;

    @Override
    protected CheckpointService getCheckpointService() {
        return processBackend.getCheckpointService();
    }

    @Override
    protected TaskContainer getTaskByCheckpoint(Checkpoint checkpoint) {
        return taskBackend.getTask(checkpoint.getGuid());
    }

    @Override
    protected void recoverTask(TaskContainer target, Checkpoint checkpoint,
            TimeoutType timeoutType) {
        queueBackend.enqueueItem(target.getActorId(), target.getTaskId(), target.getStartTime(), null);
    }

    public void setTaskBackend(TaskBackend taskBackend) {
        this.taskBackend = taskBackend;
    }

    public void setQueueBackend(QueueBackend queueBackend) {
        this.queueBackend = queueBackend;
    }

    public void setProcessBackend(ProcessBackend processBackend) {
        this.processBackend = processBackend;
    }

}
