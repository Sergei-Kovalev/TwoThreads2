package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private final static int maxEven = 1001;
    private final static int maxOdd = 1000;

    private final static Object lock = new Object();
    private static boolean turnFlag = false;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Runnable evenRunnable = () -> {
            for (int i = 0; i < maxEven; i += 2) {
                synchronized (lock) {
                    while (turnFlag) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ": " + i);
                    turnFlag = true;
                    lock.notifyAll();
                }
            }
        };

        Runnable oddRunnable = () -> {
            for (int i = 1; i < maxOdd; i += 2) {
                synchronized (lock) {
                    while (!turnFlag) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ": " + i);
                    turnFlag = false;
                    lock.notifyAll();
                }
            }
        };

        executorService.execute(evenRunnable);
        executorService.execute(oddRunnable);

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}