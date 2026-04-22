package com.fractalflame.config;

import picocli.CommandLine.Option;

public final class CliArgs {

    @Option(names = {"-w", "--width"}, description = "Ширина изображения (по умолчанию 1920)")
    public Integer width;

    @Option(names = {"-h", "--height"}, description = "Высота изображения (по умолчанию 1080)")
    public Integer height;

    @Option(names = {"--seed"}, description = "Seed генератора случайных чисел (по умолчанию 5)")
    public Long seed;

    @Option(names = {"-i", "--iteration-count"}, description = "Количество итераций (по умолчанию 2500)")
    public Integer iterations;

    @Option(names = {"-o", "--output-path"}, description = "Путь к PNG-файлу (по умолчанию result.png)")
    public String outputPath;

    @Option(names = {"-t", "--threads"}, description = "Количество потоков (по умолчанию 1)")
    public Integer threads;

    @Option(names = {"-ap", "--affine-params"},
            description = "Аффинные преобразования: a,b,c,d,e,f/a,b,c,d,e,f")
    public String affineParams;

    @Option(names = {"-f", "--functions"},
            description = "Функции вариаций: name:weight,name:weight (например, swirl:1.0,horseshoe:0.8)")
    public String functions;

    @Option(names = {"-s", "--symmetry-level"},
            description = "Уровень симметрии — число поворотов вокруг центра (по умолчанию 1)")
    public Integer symmetryLevel;

    @Option(names = {"--config"}, description = "Путь к JSON-файлу конфигурации")
    public String configPath;

    @Option(names = {"--help"}, usageHelp = true, description = "Показать справку")
    public boolean help;
}
