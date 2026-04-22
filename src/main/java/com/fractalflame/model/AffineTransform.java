package com.fractalflame.model;

import java.awt.Color;
import java.util.Random;

public record AffineTransform(double a, double b, double c,
                              double d, double e, double f,
                              int red, int green, int blue) {

    public Point apply(Point p) {
        double nx = a * p.x() + b * p.y() + c;
        double ny = d * p.x() + e * p.y() + f;
        return new Point(nx, ny);
    }

    public static AffineTransform random(Random rnd) {
        double aa, bb, dd, ee;
        do {
            aa = rnd.nextDouble() * 2 - 1;
            bb = rnd.nextDouble() * 2 - 1;
            dd = rnd.nextDouble() * 2 - 1;
            ee = rnd.nextDouble() * 2 - 1;
        } while (aa * aa + dd * dd >= 1
                || bb * bb + ee * ee >= 1
                || aa * aa + bb * bb + dd * dd + ee * ee
                   >= 1 + (aa * ee - bb * dd) * (aa * ee - bb * dd));
        double cc = rnd.nextDouble() * 2 - 1;
        double ff = rnd.nextDouble() * 2 - 1;
        Color color = Color.getHSBColor(rnd.nextFloat(), 0.9f, 1.0f);
        return new AffineTransform(aa, bb, cc, dd, ee, ff,
                color.getRed(), color.getGreen(), color.getBlue());
    }
}
