package com.fractalflame.config;

import com.fractalflame.model.AffineTransform;
import com.fractalflame.transform.WeightedVariation;

import java.nio.file.Path;
import java.util.List;

public record AppConfig(int width,
                        int height,
                        int iterations,
                        long seed,
                        Path outputPath,
                        int threads,
                        int symmetryLevel,
                        List<WeightedVariation> variations,
                        List<AffineTransform> affines) {

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private int width = 1920;
        private int height = 1080;
        private int iterations = 2500;
        private long seed = 5L;
        private Path outputPath = Path.of("result.png");
        private int threads = 1;
        private int symmetryLevel = 1;
        private List<WeightedVariation> variations = List.of();
        private List<AffineTransform> affines = List.of();

        public Builder width(int v) { this.width = v; return this; }
        public Builder height(int v) { this.height = v; return this; }
        public Builder iterations(int v) { this.iterations = v; return this; }
        public Builder seed(long v) { this.seed = v; return this; }
        public Builder outputPath(Path v) { this.outputPath = v; return this; }
        public Builder threads(int v) { this.threads = v; return this; }
        public Builder symmetryLevel(int v) { this.symmetryLevel = v; return this; }
        public Builder variations(List<WeightedVariation> v) { this.variations = v; return this; }
        public Builder affines(List<AffineTransform> v) { this.affines = v; return this; }

        public AppConfig build() {
            if (width <= 0) throw new IllegalArgumentException("width должен быть > 0");
            if (height <= 0) throw new IllegalArgumentException("height должен быть > 0");
            if (iterations <= 0) throw new IllegalArgumentException("iterations должен быть > 0");
            if (threads < 1) throw new IllegalArgumentException("threads должен быть >= 1");
            if (symmetryLevel < 1) throw new IllegalArgumentException("symmetry_level должен быть >= 1");
            if (outputPath == null) throw new IllegalArgumentException("Нужно указать путь вывода");
            if (variations == null || variations.isEmpty())
                throw new IllegalArgumentException("Нужна хотя бы одна вариация");
            if (affines == null || affines.isEmpty())
                throw new IllegalArgumentException("Нужно хотя бы одно аффинное преобразование");
            return new AppConfig(width, height, iterations, seed, outputPath,
                    threads, symmetryLevel, variations, affines);
        }
    }
}
