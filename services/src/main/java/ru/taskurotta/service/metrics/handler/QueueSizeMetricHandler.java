package ru.taskurotta.service.metrics.handler;

import ru.taskurotta.service.console.retriever.QueueInfoRetriever;
import ru.taskurotta.service.metrics.MetricName;
import ru.taskurotta.service.metrics.MetricsFactory;
import ru.taskurotta.service.metrics.PeriodicMetric;
import ru.taskurotta.service.metrics.PeriodicMetric.DatasetValueExtractor;

import java.util.List;
import java.util.Map;

/**
 * Helper class triggering periodic metric for queues size info
 * Date: 26.12.13 12:31
 */
public class QueueSizeMetricHandler {

    public QueueSizeMetricHandler(MetricsFactory metricsFactory, final QueueInfoRetriever retriever, int queueSizeMetricPeriodSeconds) {

        PeriodicMetric queueSizeMetric = metricsFactory.getPeriodicInstance(MetricName.QUEUE_SIZE.getValue(), queueSizeMetricPeriodSeconds);

        //start data collect
        queueSizeMetric.periodicMark(new DatasetValueExtractor() {

            @Override
            public List<String> getDatasets() {//return list of queue names. Dataset name = queue name
                return retriever.getQueueNames();
            }

            @Override
            public Number getDatasetValue(String dataset) {//returns number of tasks in a queue
                return retriever.getQueueTaskCount(dataset);
            }

            @Override
            public Number getGeneralValue(Map<String, Number> datasetsValues) {//returns sum of queue sizes for this metric
                int result = 0;
                if (datasetsValues != null && !datasetsValues.isEmpty()) {
                    for (Number num: datasetsValues.values()) {
                        if (num != null) {
                            result += num.intValue();
                        }
                    }
                }
                return result;
            }
        });
    }

}