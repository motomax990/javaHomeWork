package com.fractalflame.image;

import com.fractalflame.render.FractalImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class ImageWriter {

    private static final Logger LOG = LoggerFactory.getLogger(ImageWriter.class);

    private static final double GAMMA = 2.2;

    private ImageWriter() {}

    public static void writePng(FractalImage image, Path output) throws IOException {
        BufferedImage bi = toBufferedImage(image);
        File f = output.toFile();
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }
        if (!ImageIO.write(bi, "png", f)) {
            throw new IOException("Не найден PNG writer для " + output);
        }
        LOG.info("Изображение сохранено в {}", output.toAbsolutePath());
    }

    static BufferedImage toBufferedImage(FractalImage image) {
        int w = image.width();
        int h = image.height();

        double maxLog = 0.0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int hits = image.hitsAt(x, y);
                if (hits > 0) {
                    double l = Math.log1p(hits);
                    if (l > maxLog) maxLog = l;
                }
            }
        }
        if (maxLog == 0.0) maxLog = 1.0;

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        double invGamma = 1.0 / GAMMA;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int hits = image.hitsAt(x, y);
                if (hits == 0) {
                    bi.setRGB(x, y, 0);
                    continue;
                }
                double alpha = Math.log1p(hits) / maxLog;
                double corrected = Math.pow(alpha, invGamma);
                int r = clamp((int) (image.avgR(x, y) * corrected));
                int g = clamp((int) (image.avgG(x, y) * corrected));
                int b = clamp((int) (image.avgB(x, y) * corrected));
                bi.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return bi;
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : Math.min(v, 255);
    }
}
