package ru.taskurotta.test.fullfeature.worker;

import ru.taskurotta.annotation.LinearRetry;
import ru.taskurotta.annotation.Worker;

/**
 * Created by void 20.12.13 18:00
 */
@Worker
public interface FullFeatureWorker {

    double sqr(double a);

    @LinearRetry(initialRetryIntervalSeconds = 1, maximumRetryIntervalSeconds = 1, maximumAttempts = 3)
    double sqrt(double a);

}
