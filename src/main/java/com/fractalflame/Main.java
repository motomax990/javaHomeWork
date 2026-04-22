package com.fractalflame;

import com.fractalflame.config.AppConfig;
import com.fractalflame.config.CliArgs;
import com.fractalflame.config.ConfigParser;
import com.fractalflame.image.ImageWriter;
import com.fractalflame.model.Rect;
import com.fractalflame.render.FractalImage;
import com.fractalflame.render.MultiThreadedRenderer;
import com.fractalflame.render.RenderParams;
import com.fractalflame.render.Renderer;
import com.fractalflame.render.SingleThreadedRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CliArgs cli = new CliArgs();
        CommandLine cmd = new CommandLine(cli);
        try {
            CommandLine.ParseResult pr = cmd.parseArgs(args);
            if (pr.isUsageHelpRequested()) {
                cmd.usage(System.out);
                return;
            }
        } catch (CommandLine.ParameterException e) {
            System.err.println("Ошибка CLI: " + e.getMessage());
            cmd.usage(System.err);
            System.exit(2);
            return;
        }

        try {
            AppConfig cfg = ConfigParser.parse(cli);
            LOG.info("Конфигурация: {}x{}, итераций={}, потоков={}, симметрия={}, функции={}",
                    cfg.width(), cfg.height(), cfg.iterations(),
                    cfg.threads(), cfg.symmetryLevel(),
                    cfg.variations().stream().map(v -> v.name()).toList());

            Renderer renderer = cfg.threads() == 1
                    ? new SingleThreadedRenderer()
                    : new MultiThreadedRenderer(cfg.threads());

            RenderParams params = new RenderParams(
                    cfg.width(), cfg.height(), cfg.iterations(),
                    cfg.affines(), cfg.variations(),
                    new Rect(-1.777, -1.0, 3.555, 2.0),
                    cfg.symmetryLevel(), cfg.seed());

            FractalImage image = renderer.render(params);
            ImageWriter.writePng(image, cfg.outputPath());
            LOG.info("Готово.");
        } catch (IllegalArgumentException e) {
            LOG.error("Ошибка конфигурации в {}: {}",
                    e.getStackTrace().length > 0 ? e.getStackTrace()[0] : "неизвестно",
                    e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            LOG.error("Непредвиденная ошибка в {}: {}",
                    e.getStackTrace().length > 0 ? e.getStackTrace()[0] : "неизвестно",
                    e.getMessage(), e);
            System.exit(1);
        }
    }
}
