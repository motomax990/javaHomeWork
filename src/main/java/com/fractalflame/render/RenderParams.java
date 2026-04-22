package com.fractalflame.render;

import com.fractalflame.model.AffineTransform;
import com.fractalflame.model.Rect;
import com.fractalflame.transform.WeightedVariation;

import java.util.List;

public record RenderParams(int width, int height,
                           int iterations,
                           List<AffineTransform> affines,
                           List<WeightedVariation> variations,
                           Rect world,
                           int symmetryLevel,
                           long seed) {

    public RenderParams {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Размер должен быть > 0");
        if (iterations <= 0) throw new IllegalArgumentException("Число итераций должно быть > 0");
        if (affines == null || affines.isEmpty()) throw new IllegalArgumentException("Нужно хотя бы одно аффинное преобразование");
        if (variations == null || variations.isEmpty()) throw new IllegalArgumentException("Нужна хотя бы одна вариация");
        if (symmetryLevel < 1) throw new IllegalArgumentException("symmetryLevel должен быть >= 1");
    }
}
