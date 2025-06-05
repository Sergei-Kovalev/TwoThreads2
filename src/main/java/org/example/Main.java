package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private final static long maxEven = 1001;
    private final static long maxOdd = 1000;

    private final static Object lock = new Object();
    private static boolean turnFlag = false;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Runnable evenRunnable = () -> {
            for (int i = 0; i < maxEven; i += 2) {
                synchronized (lock) {
                    while (turnFlag) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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
    }
}