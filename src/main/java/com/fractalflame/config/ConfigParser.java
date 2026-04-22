package com.fractalflame.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fractalflame.model.AffineTransform;
import com.fractalflame.transform.Variations;
import com.fractalflame.transform.WeightedVariation;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ConfigParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ConfigParser() {}

    public static AppConfig parse(CliArgs cli) throws IOException {
        JsonConfig json = loadJson(cli.configPath);

        AppConfig.Builder b = AppConfig.builder();
        b.width(firstPositive(cli.width, json != null && json.size != null ? json.size.width : null, 1920));
        b.height(firstPositive(cli.height, json != null && json.size != null ? json.size.height : null, 1080));
        b.iterations(firstPositive(cli.iterations,
                json != null ? json.iteration_count : null, 2500));
        Long seed = cli.seed != null ? cli.seed
                : (json != null && json.seed != null ? json.seed.longValue() : 5L);
        b.seed(seed);
        String out = cli.outputPath != null ? cli.outputPath
                : (json != null && json.output_path != null ? json.output_path : "result.png");
        b.outputPath(Path.of(out));
        b.threads(firstPositive(cli.threads, json != null ? json.threads : null, 1));
        b.symmetryLevel(firstPositive(cli.symmetryLevel,
                json != null ? json.symmetry_level : null, 1));
        List<WeightedVariation> variations = resolveVariations(cli, json);
        b.variations(variations);
        List<AffineTransform> affines = resolveAffines(cli, json, seed);
        b.affines(affines);
        return b.build();
    }

    private static JsonConfig loadJson(String configPath) throws IOException {
        if (configPath == null) return null;
        Path p = Path.of(configPath);
        if (!Files.exists(p)) {
            throw new IOException("Файл конфигурации не найден: " + p.toAbsolutePath());
        }
        return MAPPER.readValue(Files.readAllBytes(p), JsonConfig.class);
    }

    private static int firstPositive(Integer... candidates) {
        for (Integer c : candidates) {
            if (c != null && c > 0) return c;
        }
        throw new IllegalArgumentException("Нет положительного значения среди кандидатов");
    }

    private static List<WeightedVariation> resolveVariations(CliArgs cli, JsonConfig json) {
        if (cli.functions != null && !cli.functions.isBlank()) {
            return parseFunctionsString(cli.functions);
        }
        if (json != null && json.functions != null && !json.functions.isEmpty()) {
            List<WeightedVariation> list = new ArrayList<>();
            for (JsonConfig.FunctionCfg f : json.functions) {
                if (f.name == null) throw new IllegalArgumentException("Не указано имя функции");
                double w = f.weight != null ? f.weight : 1.0;
                list.add(WeightedVariation.of(f.name, w));
            }
            return list;
        }
        return List.of(
                WeightedVariation.of("linear", 1.0),
                WeightedVariation.of("sinusoidal", 1.0),
                WeightedVariation.of("spherical", 1.0),
                WeightedVariation.of("swirl", 1.0)
        );
    }

    static List<WeightedVariation> parseFunctionsString(String s) {
        List<WeightedVariation> list = new ArrayList<>();
        for (String part : s.split(",")) {
            String item = part.trim();
            if (item.isEmpty()) continue;
            String[] kv = item.split(":");
            if (kv.length != 2) {
                throw new IllegalArgumentException(
                        "Некорректная запись функции: '" + item + "'. Ожидается name:weight");
            }
            String name = kv[0].trim();
            double weight;
            try {
                weight = Double.parseDouble(kv[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректный вес в '" + item + "'", e);
            }
            if (!Variations.exists(name)) {
                throw new IllegalArgumentException("Неизвестная функция: " + name);
            }
            list.add(WeightedVariation.of(name, weight));
        }
        if (list.isEmpty()) throw new IllegalArgumentException("Не удалось распарсить ни одной функции");
        return list;
    }

    private static List<AffineTransform> resolveAffines(CliArgs cli, JsonConfig json, long seed) {
        if (cli.affineParams != null && !cli.affineParams.isBlank()) {
            return parseAffineString(cli.affineParams, seed);
        }
        if (json != null && json.affine_params != null && !json.affine_params.isEmpty()) {
            List<AffineTransform> list = new ArrayList<>();
            Random rnd = new Random(seed);
            for (JsonConfig.AffineCfg a : json.affine_params) {
                Color c = Color.getHSBColor(rnd.nextFloat(), 0.9f, 1.0f);
                list.add(new AffineTransform(
                        nvl(a.a), nvl(a.b), nvl(a.c),
                        nvl(a.d), nvl(a.e), nvl(a.f),
                        c.getRed(), c.getGreen(), c.getBlue()));
            }
            return list;
        }
        List<AffineTransform> list = new ArrayList<>();
        Random rnd = new Random(seed);
        for (int i = 0; i < 4; i++) list.add(AffineTransform.random(rnd));
        return list;
    }

    static List<AffineTransform> parseAffineString(String s, long seed) {
        List<AffineTransform> list = new ArrayList<>();
        Random rnd = new Random(seed);
        for (String part : s.split("/")) {
            String item = part.trim();
            if (item.isEmpty()) continue;
            String[] nums = item.split(",");
            if (nums.length != 6) {
                throw new IllegalArgumentException(
                        "Аффинное преобразование должно содержать 6 чисел: " + item);
            }
            try {
                Color c = Color.getHSBColor(rnd.nextFloat(), 0.9f, 1.0f);
                list.add(new AffineTransform(
                        Double.parseDouble(nums[0].trim()),
                        Double.parseDouble(nums[1].trim()),
                        Double.parseDouble(nums[2].trim()),
                        Double.parseDouble(nums[3].trim()),
                        Double.parseDouble(nums[4].trim()),
                        Double.parseDouble(nums[5].trim()),
                        c.getRed(), c.getGreen(), c.getBlue()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректное число в '" + item + "'", e);
            }
        }
        if (list.isEmpty()) throw new IllegalArgumentException("Не удалось распарсить ни одного аффинного преобразования");
        return list;
    }

    private static double nvl(Double d) { return d == null ? 0.0 : d; }
}
