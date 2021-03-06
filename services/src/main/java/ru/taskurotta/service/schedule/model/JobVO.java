package ru.taskurotta.service.schedule.model;

import ru.taskurotta.service.schedule.JobConstants;
import ru.taskurotta.transport.model.TaskContainer;

import java.io.Serializable;

/**
 *
 * User: dimadin
 * Date: 23.09.13 10:31
 */
public class JobVO implements Serializable {

    protected long id = -1;
    protected String name;
    protected String cron;
    protected TaskContainer task;
    protected int status = JobConstants.STATUS_UNDEFINED;
    protected int errorCount = 0;
    protected String lastError;
    protected int maxErrors = JobConstants.DEFAULT_MAX_CONSEQUENTIAL_ERRORS;
    protected int limit = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public TaskContainer getTask() {
        return task;
    }

    public void setTask(TaskContainer task) {
        this.task = task;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public void setMaxErrors(int maxErrors) {
        this.maxErrors = maxErrors;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "JobVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cron='" + cron + '\'' +
                ", task=" + task +
                ", status=" + status +
                ", errorCount=" + errorCount +
                ", lastError='" + lastError + '\'' +
                ", maxErrors=" + maxErrors +
                ", limit=" + limit +
                '}';
    }

}
