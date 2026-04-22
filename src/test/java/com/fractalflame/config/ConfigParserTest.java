package com.fractalflame.config;

import com.fractalflame.model.AffineTransform;
import com.fractalflame.transform.WeightedVariation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigParserTest {

    @Test
    void defaultsWhenNoCliNoJson() throws IOException {
        AppConfig cfg = ConfigParser.parse(new CliArgs());
        assertThat(cfg.width()).isEqualTo(1920);
        assertThat(cfg.height()).isEqualTo(1080);
        assertThat(cfg.iterations()).isEqualTo(2500);
        assertThat(cfg.threads()).isEqualTo(1);
        assertThat(cfg.seed()).isEqualTo(5L);
        assertThat(cfg.symmetryLevel()).isEqualTo(1);
        assertThat(cfg.variations()).hasSize(4);
        assertThat(cfg.affines()).isNotEmpty();
    }

    @Test
    void cliOverridesDefaults() throws IOException {
        CliArgs a = new CliArgs();
        a.width = 640;
        a.height = 480;
        a.iterations = 100;
        a.threads = 2;
        a.seed = 123L;
        a.functions = "swirl:1.0,horseshoe:0.5";
        AppConfig cfg = ConfigParser.parse(a);
        assertThat(cfg.width()).isEqualTo(640);
        assertThat(cfg.height()).isEqualTo(480);
        assertThat(cfg.threads()).isEqualTo(2);
        assertThat(cfg.seed()).isEqualTo(123L);
        List<String> names = cfg.variations().stream().map(WeightedVariation::name).toList();
        assertThat(names).containsExactly("swirl", "horseshoe");
    }

    @Test
    void jsonConfigLoaded(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("cfg.json");
        Files.writeString(f, """
                {
                  "size": {"width": 320, "height": 200},
                  "iteration_count": 50,
                  "threads": 3,
                  "seed": 7,
                  "symmetry_level": 2,
                  "functions": [{"name":"swirl","weight":1.0}],
                  "affine_params": [{"a":1.0,"b":0.0,"c":0.0,"d":0.0,"e":1.0,"f":0.0}]
                }
                """);
        CliArgs a = new CliArgs();
        a.configPath = f.toString();
        AppConfig cfg = ConfigParser.parse(a);
        assertThat(cfg.width()).isEqualTo(320);
        assertThat(cfg.iterations()).isEqualTo(50);
        assertThat(cfg.threads()).isEqualTo(3);
        assertThat(cfg.symmetryLevel()).isEqualTo(2);
        assertThat(cfg.variations()).hasSize(1);
        assertThat(cfg.affines()).hasSize(1);
    }

    @Test
    void cliWinsOverJson(@TempDir Path tmp) throws IOException {
        Path f = tmp.resolve("cfg.json");
        Files.writeString(f, """
                {"size":{"width":100,"height":100},"iteration_count":10,
                 "functions":[{"name":"swirl","weight":1.0}],
                 "affine_params":[{"a":1,"b":0,"c":0,"d":0,"e":1,"f":0}]}
                """);
        CliArgs a = new CliArgs();
        a.configPath = f.toString();
        a.width = 500;
        AppConfig cfg = ConfigParser.parse(a);
        assertThat(cfg.width()).isEqualTo(500); // CLI победил
        assertThat(cfg.height()).isEqualTo(100); // JSON
    }

    @Test
    void badFunctionStringIsRejected() {
        CliArgs a = new CliArgs();
        a.functions = "swirl";
        assertThatThrownBy(() -> ConfigParser.parse(a))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void affineStringParsed() throws IOException {
        CliArgs a = new CliArgs();
        a.affineParams = "1,0,0,0,1,0/0.5,0,0.1,0,0.5,0.2";
        a.functions = "linear:1.0";
        AppConfig cfg = ConfigParser.parse(a);
        assertThat(cfg.affines()).hasSize(2);
        AffineTransform first = cfg.affines().get(0);
        assertThat(first.a()).isEqualTo(1.0);
        assertThat(first.e()).isEqualTo(1.0);
    }

    @Test
    void missingConfigFileThrows() {
        CliArgs a = new CliArgs();
        a.configPath = "/tmp/definitely-missing-123.json";
        assertThatThrownBy(() -> ConfigParser.parse(a))
                .isInstanceOf(IOException.class);
    }
}
