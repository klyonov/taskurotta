package ru.taskurotta.backend.checkpoint.model;

import ru.taskurotta.backend.checkpoint.TimeoutType;

import java.util.UUID;

/**
 * Description for common entity having specified time (checkpoint time).
 * For instance, an expiration time for task having response timeout
 */
public class Checkpoint {

    //Entity entityGuid
    private UUID entityGuid;

    //Type of entity quid field references to
    private String entityType;

    //Type of timeout for which checkpoint is set
    private TimeoutType timeoutType;

    //Checkpoint time
    private long time;

    public Checkpoint(TimeoutType timeoutType, UUID entityGuid, String entityType, long time) {
        this.entityGuid = entityGuid;
        this.time = time;
        this.entityType = entityType;
        this.timeoutType = timeoutType;
    }

    public Checkpoint(TimeoutType timeoutType, UUID entityGuid, long time) {
        this(timeoutType, entityGuid, null, time);
    }

    public Checkpoint() {
    }

    public UUID getEntityGuid() {
        return entityGuid;
    }
    public void setEntityGuid(UUID entityGuid) {
        this.entityGuid = entityGuid;
    }

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public TimeoutType getTimeoutType() {
        return timeoutType;
    }

    public void setTimeoutType(TimeoutType timeoutType) {
        this.timeoutType = timeoutType;
    }

    @Override
    public String toString() {
        return "Checkpoint [entityGuid=" + entityGuid + ", entityType=" + entityType
                + ", timeoutType=" + timeoutType + ", time=" + time + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((entityType == null) ? 0 : entityType.hashCode());
        result = prime * result + ((entityGuid == null) ? 0 : entityGuid.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        result = prime * result
                + ((timeoutType == null) ? 0 : timeoutType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Checkpoint other = (Checkpoint) obj;
        if (entityType == null) {
            if (other.entityType != null)
                return false;
        } else if (!entityType.equals(other.entityType))
            return false;
        if (entityGuid == null) {
            if (other.entityGuid != null)
                return false;
        } else if (!entityGuid.equals(other.entityGuid))
            return false;
        if (time != other.time)
            return false;
        if (timeoutType != other.timeoutType)
            return false;
        return true;
    }

}