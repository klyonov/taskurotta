package ru.taskurotta.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.taskurotta.Environment;
import ru.taskurotta.RuntimeProcessor;
import ru.taskurotta.bootstrap.profiler.Profiler;
import ru.taskurotta.client.TaskSpreader;
import ru.taskurotta.core.Task;
import ru.taskurotta.core.TaskDecision;
import ru.taskurotta.exception.SerializationException;
import ru.taskurotta.exception.server.ServerConnectionException;
import ru.taskurotta.exception.server.ServerException;
import ru.taskurotta.internal.core.TaskDecisionImpl;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * User: stukushin
 * Date: 12.02.13
 * Time: 19:29
 */
public class ActorExecutor implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ActorExecutor.class);

    private static final long SUPPRESS_REPEATED_ERROR_MLS = TimeUnit.SECONDS.toMillis(30);

    protected static class LogErrorEvent {
        String msg;
        Throwable ex;
        long timeMls;

        public LogErrorEvent(String msg, Throwable ex) {
            this.msg = msg;
            this.ex = ex;
            this.timeMls = System.currentTimeMillis();
        }
    }

    private LogErrorEvent lastErrorEvent;

    private Profiler profiler;
    private RuntimeProcessor runtimeProcessor;
    private TaskSpreader taskSpreader;

    private ThreadLocal<Boolean> threadRun = new ThreadLocal<Boolean>();
    private volatile boolean instanceRun = true;

    public ActorExecutor(Profiler profiler, Inspector inspector, RuntimeProcessor runtimeProcessor, TaskSpreader taskSpreader) {
        this.profiler = profiler;
        this.runtimeProcessor = inspector.decorate(profiler.decorate(runtimeProcessor));
        this.taskSpreader = inspector.decorate(profiler.decorate(taskSpreader));
    }

    @Override
    public void run() {

        String threadName = Thread.currentThread().getName();

        logger.trace("Started actor executor thread [{}]", threadName);

        threadRun.set(Boolean.TRUE);

        while (threadRun.get() && instanceRun) {

            profiler.cycleStart();

            try {

                logger.trace("Thread [{}]: try to poll", threadName);
                Task task = null;

                try {
                    task = taskSpreader.poll();
                } catch (SerializationException ex) {

                    UUID taskId = ex.getTaskId();

                    if (taskId == null) {
                        throw ex;
                    }

                    // send error decision to the server
                    Throwable realEx = ex.getCause();
                    if (realEx == null) {
                        realEx = ex;
                    }

                    TaskDecision errorDecision = new TaskDecisionImpl(taskId, ex.getProcessId(), ex.getPass(), realEx,
                            null);

                    logger.error("Can not deserialize task. Try to release error decision [{}]", errorDecision);

                    taskSpreader.release(errorDecision);
                    continue;
                }

                if (task == null) {
                    continue;
                }

                logger.trace("Thread [{}]: try to execute task [{}]", threadName, task);

                TaskDecision taskDecision = null;

                try {
                    taskDecision = runtimeProcessor.execute(task);
                } catch (SerializationException ex) {

                    UUID taskId = ex.getTaskId();

                    if (taskId == null) {
                        throw ex;
                    }

                    // send error decision to the server
                    Throwable realEx = ex.getCause();
                    if (realEx == null) {
                        realEx = ex;
                    }

                    TaskDecision errorDecision = new TaskDecisionImpl(taskId, ex.getProcessId(), ex.getPass(), realEx,
                            null);

                    logger.error("Can not serialize task decision. Try to release error decision [{}]", errorDecision);

                    taskSpreader.release(errorDecision);
                    continue;
                }

                logger.trace("Thread [{}]: try to release decision [{}] of task [{}]", threadName, taskDecision, task);
                taskSpreader.release(taskDecision);

            } catch (ServerConnectionException ex) {
                logError("Connection to task server error", ex);
            } catch (ServerException ex) {
                logError("Error at client-server communication", ex);
            } catch (Throwable t) {
                logError("Unexpected actor execution error", t);
                if (Environment.getInstance().getType() == Environment.Type.TEST) {
                    throw new RuntimeException(t);
                }
            } finally {
                profiler.cycleFinish();
            }

        }

        logger.debug("Finish actor executor thread [{}]", threadName);
    }

    protected void logError(String msg, Throwable ex) {

        LogErrorEvent newLogErrorEvent = new LogErrorEvent(msg, ex);

        // try to find same error
        if (lastErrorEvent != null && isRepeatedError(lastErrorEvent, newLogErrorEvent)) {

            // skip it
            return;
        }

        lastErrorEvent = newLogErrorEvent;

        logger.error(msg, ex);
    }

    private static boolean isRepeatedError(LogErrorEvent oldErrorEvent, LogErrorEvent newErrorEvent) {

        if (oldErrorEvent == null) {
            return false;
        }

        if (newErrorEvent.timeMls - SUPPRESS_REPEATED_ERROR_MLS > oldErrorEvent.timeMls) {
            return false;
        }

        if (!newErrorEvent.msg.equals(oldErrorEvent.msg)) {
            return false;
        }

        // we cannot use ex.equals()

        return recursionEquals(oldErrorEvent.ex, newErrorEvent.ex);
    }

    private static boolean recursionEquals(Throwable oldEx, Throwable newEx) {

        if (oldEx == null && newEx == null) {
            return true;
        }

        if (oldEx == null || newEx == null) {
            return false;
        }

        String oldExMsg = oldEx.getMessage();
        String newExMsg = newEx.getMessage();


        if (!((newExMsg == null && oldExMsg == null) || (newExMsg != null && oldExMsg != null && newExMsg
                .equals(oldExMsg)))) {
            return false;
        }

        StackTraceElement[] oldStElements = oldEx.getStackTrace();
        StackTraceElement[] newStElements = newEx.getStackTrace();

        if (!Arrays.equals(oldStElements, newStElements)) {
            return false;
        }

        return recursionEquals(oldEx.getCause(), newEx.getCause());
    }

    /**
     * stop current thread
     */
    public void stopThread() {
        threadRun.set(Boolean.FALSE);

        if (logger.isDebugEnabled()) {
            logger.debug("Set threadRun = false for thread [{}]", Thread.currentThread().getName());
        }
    }

    /**
     * stop all threads
     */
    public void stopInstance() {
        instanceRun = false;

        if (logger.isDebugEnabled()) {
            logger.debug("Set instanceRun = false for thread from thread [{}]", Thread.currentThread().getName());
        }
    }
}

