package com.fractalflame.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class MultiThreadedRenderer implements Renderer {

    private static final Logger LOG = LoggerFactory.getLogger(MultiThreadedRenderer.class);

    private final int threads;

    public MultiThreadedRenderer(int threads) {
        if (threads < 1) throw new IllegalArgumentException("Число потоков должно быть >= 1");
        this.threads = threads;
    }

    @Override
    public FractalImage render(RenderParams params) {
        LOG.info("Многопоточный рендер: {}x{}, итераций={}, потоков={}, seed={}",
                params.width(), params.height(), params.iterations(), threads, params.seed());

        FractalImage image = new FractalImage(params.width(), params.height());
        int perThread = params.iterations() / threads;
        int remainder = params.iterations() % threads;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<FractalImage>> futures = new ArrayList<>();
        AtomicInteger done = new AtomicInteger();
        int logStep = Math.max(1, threads / 4);

        long start = System.currentTimeMillis();
        try {
            for (int i = 0; i < threads; i++) {
                int samples = perThread + (i < remainder ? 1 : 0);
                long seed = params.seed() + i * 31L;
                int idx = i;
                futures.add(pool.submit(() -> {
                    FractalImage local = new FractalImage(params.width(), params.height());
                    Random rnd = new Random(seed);
                    ChaosGame.run(local, params, samples, rnd);
                    int d = done.incrementAndGet();
                    if (d % logStep == 0 || d == threads) {
                        LOG.info("Прогресс: {}/{} воркеров завершено ({}%)",
                                d, threads, d * 100 / threads);
                    }
                    LOG.debug("Воркер #{} завершён, итераций={}", idx, samples);
                    return local;
                }));
            }
            for (Future<FractalImage> f : futures) {
                image.merge(f.get());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка многопоточного рендера: " + e.getMessage(), e);
        } finally {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) pool.shutdownNow();
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        LOG.info("Многопоточный рендер завершён за {} мс", elapsed);
        return image;
    }
}
