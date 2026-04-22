package com.fractalflame.render;

import com.fractalflame.model.AffineTransform;
import com.fractalflame.model.Rect;
import com.fractalflame.transform.WeightedVariation;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Сравнение времени однопоточного и многопоточного рендера.
 * Проверяет НФТ: многопоточный вариант быстрее однопоточного.
 */
class BenchmarkTest {

    private static final Logger LOG = LoggerFactory.getLogger(BenchmarkTest.class);

    @Test
    void multiThreadedIsFasterThanSingle() {
        List<AffineTransform> affines = List.of(
                new AffineTransform(0.5, 0.0, 0.0, 0.0, 0.5, 0.0, 200, 50, 50),
                new AffineTransform(0.5, 0.0, 0.5, 0.0, 0.5, 0.0, 50, 200, 50),
                new AffineTransform(0.5, 0.0, 0.25, 0.0, 0.5, 0.5, 50, 50, 200)
        );
        List<WeightedVariation> vars = List.of(
                WeightedVariation.of("swirl", 1.0),
                WeightedVariation.of("horseshoe", 0.8),
                WeightedVariation.of("spherical", 0.6),
                WeightedVariation.of("sinusoidal", 0.4)
        );
        int iters = 10_000_000;
        RenderParams p = new RenderParams(512, 512, iters, affines, vars,
                new Rect(-1.5, -1.5, 3.0, 3.0), 1, 42L);

        // JVM warm-up: прогреваем JIT перед замерами
        RenderParams warmup = new RenderParams(64, 64, 500_000, affines, vars,
                new Rect(-1.5, -1.5, 3.0, 3.0), 1, 1L);
        new SingleThreadedRenderer().render(warmup);
        new MultiThreadedRenderer(2).render(warmup);

        long t1 = System.currentTimeMillis();
        new SingleThreadedRenderer().render(p);
        long single = System.currentTimeMillis() - t1;

        int cores = Math.max(2, Runtime.getRuntime().availableProcessors());
        long t2 = System.currentTimeMillis();
        new MultiThreadedRenderer(cores).render(p);
        long multi = System.currentTimeMillis() - t2;

        LOG.info("Benchmark: single={} ms, multi({} threads)={} ms, speedup={}",
                single, cores, multi, (double) single / Math.max(1, multi));
        // Многопоточный вариант должен быть хотя бы не медленнее
        assertThat(multi).isLessThanOrEqualTo(single);
    }
}
