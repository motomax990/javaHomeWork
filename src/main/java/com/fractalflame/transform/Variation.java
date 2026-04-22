package com.fractalflame.transform;

import com.fractalflame.model.Point;

@FunctionalInterface
public interface Variation {
    Point apply(Point p);
}
