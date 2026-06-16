package com.spartaclub.mini.testtyil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// NOTE : 동시성 테스트용 유틸리티 클래스
// BUG : @UtilityClass 어노테이션 미작동으로 수동으로 작성
public class ConcurrencyTestingUtil {
    public static void run(int threadCount, Runnable task) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(
                    () -> {
                        try {
                            ready.countDown();
                            start.await();

                            task.run();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            done.countDown();
                        }
                    });
        }

        ready.await();
        start.countDown();
        done.await();

        executorService.shutdown();
    }
}
