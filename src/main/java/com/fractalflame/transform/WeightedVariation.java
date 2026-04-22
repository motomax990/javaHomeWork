package com.fractalflame.transform;

public record WeightedVariation(String name, Variation variation, double weight) {

    public WeightedVariation {
        if (weight < 0) {
            throw new IllegalArgumentException("Вес вариации должен быть >= 0, получено " + weight);
        }
    }

    public static WeightedVariation of(String name, double weight) {
        return new WeightedVariation(name, Variations.byName(name), weight);
    }
}
