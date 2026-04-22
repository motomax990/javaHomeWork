package com.fractalflame.model;

public record Point(double x, double y) {

    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    public double distance() {
        return Math.sqrt(x * x + y * y);
    }

    public double theta() {
        return Math.atan2(x, y);
    }

    public double phi() {
        return Math.atan2(y, x);
    }
}
