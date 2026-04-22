package com.fractalflame.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public final class SingleThreadedRenderer implements Renderer {

    private static final Logger LOG = LoggerFactory.getLogger(SingleThreadedRenderer.class);

    @Override
    public FractalImage render(RenderParams params) {
        LOG.info("Однопоточный рендер: {}x{}, итераций={}, seed={}",
                params.width(), params.height(), params.iterations(), params.seed());

        FractalImage image = new FractalImage(params.width(), params.height());
        Random rnd = new Random(params.seed());

        long start = System.currentTimeMillis();
        ChaosGame.run(image, params, params.iterations(), rnd);
        long elapsed = System.currentTimeMillis() - start;

        LOG.info("Однопоточный рендер завершён за {} мс", elapsed);
        return image;
    }
}
