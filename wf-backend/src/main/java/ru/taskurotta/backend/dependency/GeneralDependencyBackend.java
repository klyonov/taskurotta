package ru.taskurotta.backend.dependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.taskurotta.annotation.Profiled;
import ru.taskurotta.backend.console.retriever.GraphInfoRetriever;
import ru.taskurotta.backend.dependency.links.Graph;
import ru.taskurotta.backend.dependency.links.GraphDao;
import ru.taskurotta.backend.dependency.links.Modification;
import ru.taskurotta.backend.dependency.model.DependencyDecision;
import ru.taskurotta.transport.model.ArgContainer;
import ru.taskurotta.transport.model.ArgType;
import ru.taskurotta.transport.model.DecisionContainer;
import ru.taskurotta.transport.model.TaskContainer;
import ru.taskurotta.transport.model.TaskOptionsContainer;

import java.util.Collection;
import java.util.UUID;

/**
 * User: romario
 * Date: 4/5/13
 * Time: 11:17 AM
 */
public class GeneralDependencyBackend implements DependencyBackend, GraphInfoRetriever {

    private final static Logger logger = LoggerFactory.getLogger(GeneralDependencyBackend.class);

    private GraphDao graphDao;

    public GeneralDependencyBackend(GraphDao graphDao) {

        this.graphDao = graphDao;
    }

    @Override
    public DependencyDecision applyDecision(DecisionContainer taskDecision) {

        logger.debug("applyDecision() taskDecision = [{}]", taskDecision);

        final UUID finishedTaskId = taskDecision.getTaskId();
        final UUID processId = taskDecision.getProcessId();
        final DependencyDecision resultDecision = new DependencyDecision(processId);

        final Modification modification = createLinksModification(taskDecision);

//        for (int i = 0; i < retryTimes; i++) {
//
//            Graph graph = graphDao.getGraph(processId);
//
//            if (!graph.hasNotFinishedItem(finishedTaskId)) {
//                logger.warn("Won't apply graph modification. Current task [{}] is already finished.", finishedTaskId);
//                return resultDecision; // ignore task decision and its tasks
//            }
//
//            graph.apply(modification);
//
//            if (logger.isDebugEnabled()) {
//                logger.debug("Graph  copy: " + graph.copy());
//            }
//
//            if (graphDao.updateGraph(graph)) {
//                resultDecision.setProcessFinished(graph.isFinished());
//                return resultDecision.withReadyTasks(graph.getReadyItems());
//            }
//        }

        boolean isSuccess = graphDao.changeGraph(new GraphDao.Updater() {

            public UUID getProcessId() {
                return processId;
            }

            public boolean apply(Graph graph) {

                if (!graph.hasNotFinishedItem(finishedTaskId)) {
                    logger.warn("Won't apply graph modification. Current task [{}] is already finished.", finishedTaskId);
                    return false; // ignore task decision and its tasks
                }

                graph.apply(modification);
                resultDecision.setProcessFinished(graph.isFinished());
                resultDecision.withReadyTasks(graph.getReadyItems());

                if (logger.isDebugEnabled()) {
                    logger.debug("Graph  copy: " + graph.copy());
                }

                return true;
            }
        });

        if (isSuccess) {
            return resultDecision;
        }

        logger.warn("Can't apply graph modification");
        // TODO: should be analyzed at TaskServer
        return resultDecision.withFail();
    }

    @Override
    @Profiled
    public void startProcess(TaskContainer task) {
        logger.debug("startProcess() task = [{}]", task);

        graphDao.createGraph(task.getProcessId(), task.getTaskId());
    }

    @Override
    public Graph getGraph(UUID processId) {
        return graphDao.getGraph(processId);
    }


    /**
     * Convert taskDecision to modification view
     *
     * @param taskDecision
     * @return
     */
    private Modification createLinksModification(DecisionContainer taskDecision) {

        Modification modification = new Modification();
        modification.setCompletedItem(taskDecision.getTaskId());

        ArgContainer value = taskDecision.getValue();
        if (value != null && value.isPromise() && !value.isReady()) {
            modification.setWaitForAfterRelease(value.getTaskId());
        }

        TaskContainer[] newTasks = taskDecision.getTasks();
        if (newTasks != null) {
            for (TaskContainer newTask : newTasks) {
                registerNewTask(modification, newTask);
            }
        }

        return modification;
    }

    private void registerNewTask(Modification modification, TaskContainer newTask) {
        UUID childTaskId = newTask.getTaskId();

        modification.addNewItem(childTaskId);

        ArgContainer args[] = newTask.getArgs();
        if (args == null) {
            return;
        }

        TaskOptionsContainer taskOptionsContainer = newTask.getOptions();
        ArgType[] argTypes = taskOptionsContainer != null ? taskOptionsContainer.getArgTypes() : null;

        for (int j = 0; j < args.length; j++) {
            ArgContainer arg = args[j];

            if (arg.isPromise()) {
                // skip resolved promises
                if (arg.isReady()) {
                    continue;
                }

                // skip @NoWait promises
                if (argTypes != null && ArgType.NO_WAIT.equals(argTypes[j])) {
                    continue;
                }

                modification.linkItem(childTaskId, arg.getTaskId());

            } else if (arg.isCollection() && argTypes != null && ArgType.WAIT.equals
                    (argTypes[j])) { //should wait for all promises in collection to be ready

                processWaitCollection(modification, childTaskId, arg);

            }

        }

        if (taskOptionsContainer == null) {
            return;
        }

        ArgContainer[] promisesWaitForArgContainers = taskOptionsContainer.getPromisesWaitFor();

        if (promisesWaitForArgContainers == null) {
            return;
        }

        for (ArgContainer argContainer : promisesWaitForArgContainers) {
            if (argContainer.isReady()) {
                modification.linkItem(childTaskId, argContainer.getTaskId());
            }
        }
    }


    private void processWaitCollection(Modification modification, UUID childTaskId, ArgContainer collectionArg) {
        ArgContainer[] items = collectionArg.getCompositeValue();
        for (ArgContainer item : items) {
            if (item.isPromise() && !item.isReady()) {
                modification.linkItem(childTaskId, item.getTaskId());
            }
        }
    }

    @Override
    public Collection<UUID> getProcessTasks(UUID processId) {
        Graph graph = graphDao.getGraph(processId);

        if (graph == null) {
            return null;
        }

        return graph.getProcessTasks();
    }
}