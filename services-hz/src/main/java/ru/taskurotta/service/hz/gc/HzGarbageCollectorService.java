package ru.taskurotta.service.hz.gc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.taskurotta.service.dependency.links.GraphDao;
import ru.taskurotta.service.gc.AbstractGCTask;
import ru.taskurotta.service.gc.GarbageCollectorService;
import ru.taskurotta.service.storage.ProcessService;
import ru.taskurotta.service.storage.TaskDao;
import ru.taskurotta.hazelcast.queue.delay.DelayIQueue;
import ru.taskurotta.hazelcast.queue.delay.QueueFactory;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class HzGarbageCollectorService implements GarbageCollectorService {

    private static final Logger logger = LoggerFactory.getLogger(HzGarbageCollectorService.class);

    private long timeBeforeDelete;
    private boolean enabled;

    private DelayIQueue<UUID> garbageCollectorQueue;

    private volatile boolean notInShutdown = true;

    public HzGarbageCollectorService(final ProcessService processService, final GraphDao graphDao,
                                     final TaskDao taskDao, QueueFactory queueFactory, String garbageCollectorQueueName,
                                     int poolSize, long timeBeforeDelete, boolean enabled) {

        logger.debug("Garbage Collector initialization. Enabled: {}", enabled);

        this.enabled = enabled;

        if (!enabled) {
            return;
        }

        this.timeBeforeDelete = timeBeforeDelete;

        this.garbageCollectorQueue = queueFactory.create(garbageCollectorQueueName);

        final ExecutorService executorService = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
            private int counter = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("GC-" + counter++);
                thread.setDaemon(true);
                return thread;
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                notInShutdown = false;
            }
        });

        for (int i = 0; i < poolSize; i++) {
            executorService.submit(new AbstractGCTask(processService, graphDao, taskDao) {
                @Override
                public void run() {
                    while (notInShutdown) {
                        try {
                            logger.trace("Try to get process for garbage collector");

                            UUID processId = garbageCollectorQueue.poll(50, TimeUnit.SECONDS);
                            if (processId != null) {
                                gc(processId);
                            }
                        } catch (Exception e) {
                            logger.error(e.getLocalizedMessage(), e);
                        }

                    }
                }
            });
        }
    }

    @Override
    public void collect(UUID processId) {
        if (!enabled) {
            return;
        }
        garbageCollectorQueue.add(processId, timeBeforeDelete, TimeUnit.MILLISECONDS);
    }

    @Override
    public int getCurrentSize() {
        if (!enabled) {
            return 0;
        }
        return garbageCollectorQueue.size();
    }
}
