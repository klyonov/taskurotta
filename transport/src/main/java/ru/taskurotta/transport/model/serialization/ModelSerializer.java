package ru.taskurotta.transport.model.serialization;

/**
 * User: moroz
 * Date: 11.04.13
 */
public interface ModelSerializer<T> {
    Object serialize(T obj);

    T deserialize(Object obj);
}
