package ru.taskurotta.core;

import java.util.UUID;

/**
 * User: romario
 * Date: 12/26/12
 * Time: 12:07 PM
 */
public interface TaskDecision {

    public static final int NO_RESTART = -1;

    /**
     * @return Task unique Id
     */
    public UUID getId();


    /**
     * @return Task unique Id
     */
    public UUID getProcessId();

    /**
     * @return retirned value
     */
    Object getValue();


    /**
     * TODO: to Array
     * @return produced tasks
     */
    Task[] getTasks();


    boolean isError();


    Throwable getException();

    long getRestartTime();

    long getExecutionTime();

    UUID getPass();
}
