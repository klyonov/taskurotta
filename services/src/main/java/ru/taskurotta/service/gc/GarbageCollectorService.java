package ru.taskurotta.service.gc;

import java.util.UUID;

public interface GarbageCollectorService {

    public void delete(UUID processId);

    public int getCurrentSize();

}
