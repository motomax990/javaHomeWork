package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.logAnalyzer.cli.CliArgs;
import org.logAnalyzer.cli.CliParser;
import org.logAnalyzer.exception.InvalidArgumentException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CliParserTest {

    @Test
    void parsesAllArguments() {
        CliArgs result = CliParser.parse(new String[]{
            "--path", "access.log", "--format", "json", "--output", "report.json",
            "--from", "2024-01-01", "--to", "2024-12-31"
        });
        assertThat(result.paths()).containsExactly("access.log");
        assertThat(result.format()).isEqualTo("json");
        assertThat(result.output()).isEqualTo("report.json");
        assertThat(result.from()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result.to()).isEqualTo(LocalDate.of(2024, 12, 31));
    }

    @Test
    void parsesMultiplePaths() {
        CliArgs result = CliParser.parse(new String[]{"--path", "a.log", "b.log", "--format", "markdown", "--output", "out.md"});
        assertThat(result.paths()).containsExactly("a.log", "b.log");
    }

    @Test
    void parsesShortFlags() {
        CliArgs result = CliParser.parse(new String[]{"-p", "a.log", "-f", "json", "-o", "out.json"});
        assertThat(result.format()).isEqualTo("json");
    }

    @Test
    void allowsNullDates() {
        CliArgs result = CliParser.parse(new String[]{"--path", "a.log", "--format", "json", "--output", "out.json"});
        assertThat(result.from()).isNull();
        assertThat(result.to()).isNull();
    }

    @Test
    void throwsWhenPathMissing() {
        assertThatThrownBy(() -> CliParser.parse(new String[]{"--format", "json", "--output", "out.json"}))
            .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void throwsForUnsupportedFormat() {
        assertThatThrownBy(() -> CliParser.parse(new String[]{"--path", "a.log", "--format", "xml", "--output", "out.xml"}))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("Unsupported format");
    }

    @Test
    void throwsWhenFromEqualsTo() {
        assertThatThrownBy(() -> CliParser.parse(new String[]{
            "--path", "a.log", "--format", "json", "--output", "out.json",
            "--from", "2024-01-01", "--to", "2024-01-01"
        }))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("--from must be strictly before --to");
    }

    @Test
    void throwsForInvalidDateFormat() {
        assertThatThrownBy(() -> CliParser.parse(new String[]{
            "--path", "a.log", "--format", "json", "--output", "out.json",
            "--from", "01-01-2024"
        }))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("ISO8601");
    }

    @Test
    void throwsForWrongOutputExtension() {
        assertThatThrownBy(() -> CliParser.parse(new String[]{
            "--path", "a.log", "--format", "json", "--output", "out.md"
        }))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining(".json");
    }
}
