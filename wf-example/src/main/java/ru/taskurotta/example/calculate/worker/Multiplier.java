package ru.taskurotta.example.calculate.worker;

import ru.taskurotta.annotation.LinearRetry;
import ru.taskurotta.annotation.Worker;

@Worker
public interface Multiplier {

    @LinearRetry(initialRetryIntervalSeconds=5)
    public Integer multiply(Integer a, Integer b);

}
