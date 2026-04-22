package com.fractalflame.transform;

import com.fractalflame.model.Point;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariationsTest {

    private static final double EPS = 1e-9;

    @Test
    void linearIsIdentity() {
        Point p = new Point(0.3, -0.7);
        Point r = Variations.linear(p);
        assertThat(r.x()).isEqualTo(0.3);
        assertThat(r.y()).isEqualTo(-0.7);
    }

    @Test
    void sinusoidalAppliesSin() {
        Point r = Variations.sinusoidal(new Point(Math.PI / 2, 0));
        assertThat(r.x()).isCloseTo(1.0, org.assertj.core.data.Offset.offset(EPS));
        assertThat(r.y()).isCloseTo(0.0, org.assertj.core.data.Offset.offset(EPS));
    }

    @Test
    void sphericalInvertsByRadiusSquared() {
        Point r = Variations.spherical(new Point(1.0, 0.0));
        assertThat(r.x()).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-6));
        assertThat(r.y()).isCloseTo(0.0, org.assertj.core.data.Offset.offset(1e-6));
        Point r2 = Variations.spherical(new Point(2.0, 0.0));
        assertThat(r2.x()).isCloseTo(0.5, org.assertj.core.data.Offset.offset(1e-6));
    }

    @Test
    void swirlPreservesRadius() {
        Point p = new Point(0.5, 0.3);
        Point r = Variations.swirl(p);
        assertThat(Math.hypot(r.x(), r.y()))
                .isCloseTo(Math.hypot(p.x(), p.y()), org.assertj.core.data.Offset.offset(1e-9));
    }

    @Test
    void horseshoeAtUnitX() {
        Point r = Variations.horseshoe(new Point(1.0, 0.0));
        // (x-y)(x+y)/r = 1, 2xy/r = 0
        assertThat(r.x()).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-6));
        assertThat(r.y()).isCloseTo(0.0, org.assertj.core.data.Offset.offset(1e-6));
    }

    @Test
    void polarMapping() {
        Point r = Variations.polar(new Point(0.0, 1.0));
        // theta = atan2(x,y) = 0, r = 1
        assertThat(r.x()).isCloseTo(0.0, org.assertj.core.data.Offset.offset(EPS));
        assertThat(r.y()).isCloseTo(0.0, org.assertj.core.data.Offset.offset(EPS));
    }

    @Test
    void handkerchiefReturnsFinite() {
        Point r = Variations.handkerchief(new Point(0.4, 0.5));
        assertThat(Double.isFinite(r.x())).isTrue();
        assertThat(Double.isFinite(r.y())).isTrue();
    }

    @Test
    void heartReturnsFinite() {
        Point r = Variations.heart(new Point(0.2, 0.8));
        assertThat(Double.isFinite(r.x())).isTrue();
        assertThat(Double.isFinite(r.y())).isTrue();
    }

    @Test
    void discReturnsFinite() {
        Point r = Variations.disc(new Point(0.1, 0.2));
        assertThat(Double.isFinite(r.x())).isTrue();
        assertThat(Double.isFinite(r.y())).isTrue();
    }

    @Test
    void registryLookup() {
        assertThat(Variations.exists("swirl")).isTrue();
        assertThat(Variations.exists("unknown")).isFalse();
        assertThat(Variations.byName("horseshoe")).isNotNull();
        assertThatThrownBy(() -> Variations.byName("bogus"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
