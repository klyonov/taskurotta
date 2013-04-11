package ru.taskurotta.backend.queue;

import java.util.UUID;

import ru.taskurotta.util.ActorDefinition;

/**
 * User: romario
 * Date: 4/1/13
 * Time: 12:12 PM
 */
public interface QueueBackend {

    /**
     * Create TASK_POLL_TIMEOUT checkpoint.
     *
     * @param actorDefinition
     * @return
     */
    public UUID poll(ActorDefinition actorDefinition);


    /**
     * Create TASK_TIMEOUT checkpoint
     * Delete TASK_POLL_TIMEOUT checkpoint
     *
     * @param taskId
     */
    public void pollCommit(ActorDefinition actorDefinition, UUID taskId);


    public void enqueueItem(String actorId, UUID taskId, long startTime);

}
