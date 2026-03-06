package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.logAnalyzer.formatter.MarkdownFormatter;
import org.logAnalyzer.model.Statistics;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownFormatterTest {

    private final MarkdownFormatter formatter = new MarkdownFormatter();

    private Statistics buildStats() {
        LinkedHashMap<String, Long> resources = new LinkedHashMap<>();
        resources.put("/index.html", 5000L);
        resources.put("/about.html", 2000L);

        LinkedHashMap<Integer, Long> codes = new LinkedHashMap<>();
        codes.put(200, 8000L);
        codes.put(404, 1000L);
        codes.put(500, 500L);

        LinkedHashMap<LocalDate, Long> dates = new LinkedHashMap<>();
        dates.put(LocalDate.of(2024, 3, 1), 2981L);

        return new Statistics(
            List.of("access.log"),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 12, 31),
            10000, 500.0, 1000, 950.0,
            resources, codes, dates
        );
    }

    @Test
    void containsGeneralInfoSection() {
        assertThat(formatter.format(buildStats())).contains("#### Общая информация");
    }

    @Test
    void containsResourcesSection() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("#### Запрашиваемые ресурсы");
        assertThat(result).contains("/index.html");
    }

    @Test
    void containsResponseCodesSection() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("#### Коды ответа");
        assertThat(result).contains("OK");
        assertThat(result).contains("Not Found");
        assertThat(result).contains("Internal Server Error");
    }

    @Test
    void containsRequestsPerDateSection() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("#### Распределение запросов по датам");
        assertThat(result).contains("01.03.2024");
    }

    @Test
    void formatsFromAndToDates() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("01.01.2024");
        assertThat(result).contains("31.12.2024");
    }

    @Test
    void showsDashWhenDatesAreNull() {
        Statistics stats = new Statistics(
            List.of("access.log"), null, null, 0, 0, 0, 0,
            new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>()
        );
        String result = formatter.format(stats);
        long dashCount = result.lines().filter(line -> line.contains("| - |")).count();
        assertThat(dashCount).isGreaterThanOrEqualTo(2);
    }

    @Test
    void formatsLargeNumbersWithUnderscore() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("10_000");
        assertThat(result).contains("5_000");
    }

    @Test
    void doesNotIncludeDateSectionWhenEmpty() {
        Statistics stats = new Statistics(
            List.of("access.log"), null, null, 0, 0, 0, 0,
            new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>()
        );
        assertThat(formatter.format(stats)).doesNotContain("Распределение запросов по датам");
    }
}
