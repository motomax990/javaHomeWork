package com.fractalflame.render;

import com.fractalflame.image.ImageWriter;
import com.fractalflame.model.AffineTransform;
import com.fractalflame.model.Rect;
import com.fractalflame.transform.WeightedVariation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RendererTest {

    private RenderParams sampleParams(int w, int h, int iters) {
        List<AffineTransform> affines = List.of(
                new AffineTransform(0.5, 0.0, 0.0, 0.0, 0.5, 0.0, 255, 0, 0),
                new AffineTransform(0.5, 0.0, 0.5, 0.0, 0.5, 0.0, 0, 255, 0),
                new AffineTransform(0.5, 0.0, 0.25, 0.0, 0.5, 0.5, 0, 0, 255)
        );
        List<WeightedVariation> vars = List.of(
                WeightedVariation.of("linear", 1.0),
                WeightedVariation.of("sinusoidal", 0.5)
        );
        return new RenderParams(w, h, iters, affines, vars,
                new Rect(-1.5, -1.5, 3.0, 3.0), 1, 42L);
    }

    @Test
    void singleThreadProducesHits() {
        FractalImage img = new SingleThreadedRenderer().render(sampleParams(64, 64, 20_000));
        int totalHits = 0;
        for (int y = 0; y < img.height(); y++)
            for (int x = 0; x < img.width(); x++)
                totalHits += img.hitsAt(x, y);
        assertThat(totalHits).isGreaterThan(0);
    }

    @Test
    void multiThreadedMatchesBufferSize() {
        FractalImage img = new MultiThreadedRenderer(4).render(sampleParams(32, 32, 40_000));
        assertThat(img.width()).isEqualTo(32);
        assertThat(img.height()).isEqualTo(32);
        int totalHits = 0;
        for (int y = 0; y < img.height(); y++)
            for (int x = 0; x < img.width(); x++)
                totalHits += img.hitsAt(x, y);
        assertThat(totalHits).isGreaterThan(0);
    }

    @Test
    void writesPngToDisk(@TempDir Path tmp) throws IOException {
        FractalImage img = new SingleThreadedRenderer().render(sampleParams(48, 48, 5_000));
        Path out = tmp.resolve("out.png");
        ImageWriter.writePng(img, out);
        assertThat(out.toFile()).exists();
        BufferedImage loaded = javax.imageio.ImageIO.read(out.toFile());
        assertThat(loaded.getWidth()).isEqualTo(48);
        assertThat(loaded.getHeight()).isEqualTo(48);
    }
}
