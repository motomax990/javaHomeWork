package com.fractalflame.model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class AffineTransformTest {

    @Test
    void applyUsesAllSixCoefficients() {
        AffineTransform t = new AffineTransform(
                1, 2, 3,
                4, 5, 6,
                255, 0, 0);
        Point r = t.apply(new Point(10, 20));
        // x' = 1*10 + 2*20 + 3 = 53
        // y' = 4*10 + 5*20 + 6 = 146
        assertThat(r.x()).isEqualTo(53.0);
        assertThat(r.y()).isEqualTo(146.0);
    }

    @Test
    void randomProducesContraction() {
        Random rnd = new Random(1);
        for (int i = 0; i < 50; i++) {
            AffineTransform t = AffineTransform.random(rnd);
            assertThat(t.a() * t.a() + t.d() * t.d()).isLessThan(1.0);
            assertThat(t.b() * t.b() + t.e() * t.e()).isLessThan(1.0);
        }
    }
}
