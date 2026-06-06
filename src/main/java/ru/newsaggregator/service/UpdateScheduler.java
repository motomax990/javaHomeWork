package ru.newsaggregator.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class UpdateScheduler {

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> task;
    private int intervalMinutes;

    public UpdateScheduler() {
        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "news-auto-update");
            thread.setDaemon(true);
            return thread;
        });
    }

    public synchronized void start(int minutes, Runnable action) {
        stop();
        this.intervalMinutes = minutes;
        this.task = executor.scheduleAtFixedRate(action, minutes, minutes, TimeUnit.MINUTES);
    }

    public synchronized void stop() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }

    public synchronized boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    public synchronized int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void shutdown() {
        stop();
        executor.shutdownNow();
    }
}
