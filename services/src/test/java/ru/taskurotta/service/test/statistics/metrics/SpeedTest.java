package ru.taskurotta.service.test.statistics.metrics;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * User: stukushin
 * Date: 05.09.13
 * Time: 16:51
 */
public class SpeedTest {

//    private DataListener dataListener = new MockDataListener();
//    private Random random = new Random();
//
//    private int size = 100000;
//
//    private ExecutorService executorService = Executors.newFixedThreadPool(8);
//
//    class MockDataListener extends AbstractDataListener {
//        public void handle(String name, long count, double mean, long time) {
//            System.out.println("DataListener count = " + count + ", mean = " + mean);
//        }
//    }

//    class MarkTask implements Callable<Long> {
//
//        private CheckPoint checkPoint;
//        private int data;
//
//        MarkTask(CheckPoint checkPoint, int data) {
//            this.checkPoint = checkPoint;
//            this.data = data;
//        }
//
//        @Override
//        public Long call() throws Exception {
//
//            long start = System.nanoTime();
//
//            checkPoint.mark(data);
//
//            return System.nanoTime() - start;
//        }
//    }

    @Ignore
    @Test
    public void testCheckPoint() throws ExecutionException, InterruptedException {
//        List<CheckPoint> checkPoints = new ArrayList<>();
//        checkPoints.add(new CheckPoint("testName", dataListener));
//
//        Collections.shuffle(checkPoints);
//
//        int[] testData = new int[size];
//        long sum = 0;
//        for (int i = 0; i < size; i++) {
//            testData[i] = random.nextInt(100000);
//            sum += testData[i];
//        }
//
//        System.out.println("Real mean mean is " + (double) sum / size);
//
//        for (CheckPoint checkPoint : checkPoints) {
//            System.out.println(" Test for [" + checkPoint.getClass().getSimpleName() + "]...");
//            test(checkPoint, testData);
//            checkPoint.dump();
//        }
    }

//    private void test(CheckPoint checkPoint, int[] testData) throws InterruptedException, ExecutionException {
//
//        List<MarkTask> tasks = new ArrayList<>(size);
//
//        for (int i = 0; i < size; i++) {
//            tasks.add(new MarkTask(checkPoint, testData[i]));
//        }
//
//        List<Future<Long>> futures = executorService.invokeAll(tasks);
//
//        long sum = 0;
//        Iterator<Future<Long>> iterator = futures.iterator();
//        while (!futures.isEmpty()) {
//            Future<Long> future = iterator.next();
//
//            if (future.isDone()) {
//                long result = future.get();
//
//                sum += result;
//
//                iterator.remove();
//            }
//        }
//
//        System.out.println("For [" + checkPoint.getClass().getSimpleName() + "] mean speed = " + sum / size);
//    }
}
