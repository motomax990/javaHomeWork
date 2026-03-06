package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.logAnalyzer.formatter.JsonFormatter;
import org.logAnalyzer.model.Statistics;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonFormatterTest {

    private final JsonFormatter formatter = new JsonFormatter();

    private Statistics buildStats() {
        LinkedHashMap<String, Long> resources = new LinkedHashMap<>();
        resources.put("/index.html", 500L);
        resources.put("/about.html", 200L);

        LinkedHashMap<Integer, Long> codes = new LinkedHashMap<>();
        codes.put(200, 800L);
        codes.put(404, 100L);

        LinkedHashMap<LocalDate, Long> dates = new LinkedHashMap<>();
        dates.put(LocalDate.of(2024, 3, 1), 300L);

        return new Statistics(
            List.of("access.log"), null, null,
            1000, 512.5, 2048, 1900.0,
            resources, codes, dates
        );
    }

    @Test
    void outputIsValidJsonStructure() {
        String result = formatter.format(buildStats());
        assertThat(result).startsWith("{");
        assertThat(result).endsWith("}");
    }

    @Test
    void containsFilesArray() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("\"files\"");
        assertThat(result).contains("\"access.log\"");
    }

    @Test
    void containsTotalRequestsCount() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("\"totalRequestsCount\"");
        assertThat(result).contains("1000");
    }

    @Test
    void containsResponseSizeInBytes() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("\"responseSizeInBytes\"");
        assertThat(result).contains("\"average\"");
        assertThat(result).contains("\"max\"");
        assertThat(result).contains("\"p95\"");
    }

    @Test
    void containsResources() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("\"resources\"");
        assertThat(result).contains("/index.html");
    }

    @Test
    void containsResponseCodes() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("\"responseCodes\"");
        assertThat(result).contains("\"code\"");
    }

    @Test
    void containsRequestsPerDate() {
        String result = formatter.format(buildStats());
        assertThat(result).contains("\"requestsPerDate\"");
        assertThat(result).contains("2024-03-01");
        assertThat(result).contains("\"weekday\"");
    }
}
