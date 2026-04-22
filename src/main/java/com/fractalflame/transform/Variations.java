package com.fractalflame.transform;

import com.fractalflame.model.Point;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Variations {

    private static final Map<String, Variation> REGISTRY = new LinkedHashMap<>();

    static {
        REGISTRY.put("linear", Variations::linear);
        REGISTRY.put("sinusoidal", Variations::sinusoidal);
        REGISTRY.put("spherical", Variations::spherical);
        REGISTRY.put("swirl", Variations::swirl);
        REGISTRY.put("horseshoe", Variations::horseshoe);
        REGISTRY.put("polar", Variations::polar);
        REGISTRY.put("handkerchief", Variations::handkerchief);
        REGISTRY.put("heart", Variations::heart);
        REGISTRY.put("disc", Variations::disc);
    }

    private Variations() {}

    public static Variation byName(String name) {
        Variation v = REGISTRY.get(name.toLowerCase());
        if (v == null) {
            throw new IllegalArgumentException("Неизвестная вариация: " + name
                    + ". Доступные: " + REGISTRY.keySet());
        }
        return v;
    }

    public static boolean exists(String name) {
        return REGISTRY.containsKey(name.toLowerCase());
    }

    public static Point linear(Point p) {
        return p;
    }

    public static Point sinusoidal(Point p) {
        return new Point(Math.sin(p.x()), Math.sin(p.y()));
    }

    public static Point spherical(Point p) {
        double r2 = p.x() * p.x() + p.y() * p.y() + 1e-12;
        return new Point(p.x() / r2, p.y() / r2);
    }

    public static Point swirl(Point p) {
        double r2 = p.x() * p.x() + p.y() * p.y();
        double sin = Math.sin(r2);
        double cos = Math.cos(r2);
        return new Point(p.x() * sin - p.y() * cos,
                         p.x() * cos + p.y() * sin);
    }

    public static Point horseshoe(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y()) + 1e-12;
        return new Point(((p.x() - p.y()) * (p.x() + p.y())) / r,
                         (2 * p.x() * p.y()) / r);
    }

    public static Point polar(Point p) {
        double theta = p.theta();
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        return new Point(theta / Math.PI, r - 1);
    }

    public static Point handkerchief(Point p) {
        double theta = p.theta();
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        return new Point(r * Math.sin(theta + r),
                         r * Math.cos(theta - r));
    }

    public static Point heart(Point p) {
        double theta = p.theta();
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        return new Point(r * Math.sin(theta * r),
                         -r * Math.cos(theta * r));
    }

    public static Point disc(Point p) {
        double theta = p.theta();
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        return new Point((theta / Math.PI) * Math.sin(Math.PI * r),
                         (theta / Math.PI) * Math.cos(Math.PI * r));
    }
}
