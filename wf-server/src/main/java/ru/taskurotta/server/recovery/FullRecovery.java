package ru.taskurotta.server.recovery;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ru.taskurotta.backend.dependency.DependencyBackend;
import ru.taskurotta.backend.dependency.model.DependencyDecision;
import ru.taskurotta.backend.queue.QueueBackend;
import ru.taskurotta.backend.storage.TaskBackend;
import ru.taskurotta.backend.storage.model.DecisionContainer;
import ru.taskurotta.backend.storage.model.TaskContainer;

/**
 * First pre-mega-supa-alfa of full recovery process;
 * <p/>
 * User: romario
 * Date: 4/1/13
 * Time: 8:39 PM
 */
public class FullRecovery {

    private TaskBackend taskBackend;
    private DependencyBackend dependencyBackend;
    private QueueBackend queueBackend;

    public void run() {

        // This is stupid but good for pre-mega-supa-alfa :)
        List<TaskContainer> allRunProcesses = taskBackend.getAllRunProcesses();

        for (TaskContainer taskContainer : allRunProcesses) {

            List<UUID> tasksToQueueList = new LinkedList<UUID>();

            // emulate start of process
            dependencyBackend.startProcess(taskContainer);
            tasksToQueueList.add(taskContainer.getTaskId());

            // Also stupid :)
            List<DecisionContainer> decisionContainers = taskBackend.getAllTaskDecisions(taskContainer.getTaskId());

            // ? Should we put start task to dependencyBackend firstly ?

            for (DecisionContainer decisionContainer : decisionContainers) {

                // remove resolved task
                tasksToQueueList.remove(decisionContainer.getTaskId());
                // add all new ready tasks to queue
                DependencyDecision dependencyDecision = dependencyBackend.applyDecision(decisionContainer);

//                UUID[] readyTasks = dependencyDecision.getReadyTasks();
//                if (readyTasks != null) {
//                    tasksToQueueList.addAll(Arrays.asList(readyTasks));
//                }
            }

            for (UUID taskToQueueId : tasksToQueueList) {

                TaskContainer task2Queue = taskBackend.getTask(taskToQueueId);

                queueBackend.enqueueItem(task2Queue.getActorId(), taskToQueueId,
                        task2Queue.getStartTime(), null); // This time may be shifted by RetryPolicy
            }
        }

    }


}