package com.fractalflame.render;

import com.fractalflame.model.AffineTransform;
import com.fractalflame.model.Point;
import com.fractalflame.model.Rect;
import com.fractalflame.transform.WeightedVariation;

import java.util.List;
import java.util.Random;

final class ChaosGame {

    static final int WARMUP = 20;

    private ChaosGame() {}

    static void run(FractalImage image, RenderParams params, int samples, Random rnd) {
        List<AffineTransform> affines = params.affines();
        List<WeightedVariation> vars = params.variations();
        Rect world = params.world();
        int symmetry = params.symmetryLevel();

        double totalVarWeight = 0.0;
        for (WeightedVariation v : vars) totalVarWeight += v.weight();

        Point p = new Point(
                rnd.nextDouble() * (world.width()) + world.x(),
                rnd.nextDouble() * (world.height()) + world.y()
        );

        for (int step = -WARMUP; step < samples; step++) {
            AffineTransform affine = affines.get(rnd.nextInt(affines.size()));
            p = affine.apply(p);

            double r = rnd.nextDouble() * totalVarWeight;
            double acc = 0.0;
            Point transformed = p;
            for (WeightedVariation wv : vars) {
                acc += wv.weight();
                if (r <= acc) {
                    transformed = wv.variation().apply(p);
                    break;
                }
            }
            p = transformed;

            if (step < 0) continue;

            double baseAngle = 2 * Math.PI / symmetry;
            for (int s = 0; s < symmetry; s++) {
                double angle = baseAngle * s;
                double rx = p.x() * Math.cos(angle) - p.y() * Math.sin(angle);
                double ry = p.x() * Math.sin(angle) + p.y() * Math.cos(angle);
                plot(image, affine, world, rx, ry);
            }
        }
    }

    private static void plot(FractalImage image, AffineTransform affine,
                             Rect world, double wx, double wy) {
        if (wx < world.x() || wx > world.x() + world.width()
                || wy < world.y() || wy > world.y() + world.height()) {
            return;
        }
        int px = (int) ((wx - world.x()) / world.width() * image.width());
        int py = (int) ((wy - world.y()) / world.height() * image.height());
        if (!image.contains(px, py)) return;
        image.hit(px, py, affine.red(), affine.green(), affine.blue());
    }
}
