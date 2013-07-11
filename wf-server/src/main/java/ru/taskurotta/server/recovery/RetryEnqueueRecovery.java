package ru.taskurotta.server.recovery;

import ru.taskurotta.backend.checkpoint.CheckpointService;
import ru.taskurotta.backend.checkpoint.TimeoutType;
import ru.taskurotta.backend.checkpoint.model.Checkpoint;
import ru.taskurotta.backend.queue.QueueBackend;
import ru.taskurotta.backend.storage.TaskBackend;
import ru.taskurotta.server.recovery.base.AbstractIterableRecovery;
import ru.taskurotta.transport.model.TaskContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Recovery process that tries to enqueue expired task again.
 * Requires CheckpointService as Checkpoint source.
 * Tries to recover all types of checkpoints if other is not explicitly set
 *
 */
public class RetryEnqueueRecovery extends AbstractIterableRecovery {

    private TimeoutType[] supportedTimeouts = new TimeoutType[]{TimeoutType.PROCESS_START_TO_CLOSE, TimeoutType.TASK_START_TO_CLOSE, TimeoutType.TASK_POLL_TO_COMMIT};//tries to recover all types of timeouts by default
    private TaskBackend taskBackend;
    private QueueBackend queueBackend;

    private List<CheckpointService> defaultCheckpointServices;

    @Override
    protected boolean recover(Checkpoint checkpoint) {
        boolean result = false;
        TimeoutType timeoutType = checkpoint.getTimeoutType();

        //TODO: make it in some better way
        if (timeoutType.toString().toUpperCase().startsWith("TASK")) {//try to enqueue task again
            logger.debug("Recover checkpoint {}", checkpoint);
            result = retryTaskEnqueue(checkpoint.getTaskId(), checkpoint.getProcessId());
        } else if (timeoutType.toString().toUpperCase().startsWith("PROCESS")) {//Try to recover process by enqueue first task
            retryProcessStartTaskEnqueue(checkpoint.getProcessId());
        } else {
            logger.error("Unknown timeout type [{}] - cannot recover!", timeoutType);
        }
        return result;
    }


    private boolean retryTaskEnqueue(UUID taskGuid, UUID processId) {
        boolean result = false;
        TaskContainer task = taskBackend.getTask(taskGuid, processId);
        if (task!=null) {
            logger.debug("Recover task {}", task);
            queueBackend.enqueueItem(task.getActorId(), task.getTaskId(), task.getProcessId(), task.getStartTime(), extractTaskList(task));
            result = true;
        } else {
            logger.error("Task not found by GUID["+taskGuid+"]");
        }
        return result;
    }

    private String extractTaskList(TaskContainer task) {
        String result = null;
        if (task.getOptions()!= null && task.getOptions().getActorSchedulingOptions() != null) {
            result = task.getOptions().getActorSchedulingOptions().getTaskList();
        }
        return result;
    }

    private boolean retryProcessStartTaskEnqueue(UUID processGuid) {
        boolean result = false;

        logger.debug("Recover process {}. Not implemented yet, sorry.", processGuid);
        //TODO: requires some logic here :)

        return result;
    }

    public void setTaskBackend(TaskBackend taskBackend) {
        this.taskBackend = taskBackend;
    }

    public void setQueueBackend(QueueBackend queueBackend) {
        this.queueBackend = queueBackend;
    }

    @Override
    protected TimeoutType[] getSupportedTimeouts() {
        //TODO: ask for this the Checkpoint service itself?
        return supportedTimeouts;
    }

    //Optional to override defaults
    public void setSupportedTimeouts(TimeoutType[] supportedTimeouts) {
        this.supportedTimeouts = supportedTimeouts;
    }

    @Override
    public List<CheckpointService> getCheckpointServices() {
        if (checkpointServices!=null) {
            return checkpointServices;
        } else {
            return defaultCheckpointServices();
        }
    }

    private List<CheckpointService> defaultCheckpointServices() {
       if (defaultCheckpointServices == null) {
           defaultCheckpointServices = new ArrayList<CheckpointService>();
           defaultCheckpointServices.add(taskBackend.getCheckpointService());
           defaultCheckpointServices.add(queueBackend.getCheckpointService());
       }
       return defaultCheckpointServices;
    }
}
