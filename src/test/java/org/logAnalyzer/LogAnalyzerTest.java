package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.logAnalyzer.analyzer.LogAnalyzer;
import org.logAnalyzer.cli.CliArgs;
import org.logAnalyzer.model.Statistics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogAnalyzerTest {

    @TempDir
    Path tempDir;

    private static final String LOG_LINE_1 =
        "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 100 \"-\" \"Agent/1.0\"";
    private static final String LOG_LINE_2 =
        "93.180.71.4 - - [17/May/2015:09:00:00 +0000] \"GET /downloads/product_2 HTTP/1.1\" 200 500 \"-\" \"Agent/2.0\"";
    private static final String LOG_LINE_3 =
        "93.180.71.5 - - [18/May/2015:10:00:00 +0000] \"POST /api/data HTTP/1.1\" 500 200 \"-\" \"Agent/3.0\"";

    @Test
    void countsAllRequests() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_2, LOG_LINE_3), null, null);
        assertThat(stats.totalRequestsCount()).isEqualTo(3);
    }

    @Test
    void calculatesAverageResponseSize() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_2), null, null);
        assertThat(stats.averageResponseSizeBytes()).isEqualTo(300.0);
    }

    @Test
    void calculatesMaxResponseSize() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_2, LOG_LINE_3), null, null);
        assertThat(stats.maxResponseSizeBytes()).isEqualTo(500L);
    }

    @Test
    void countsTopResources() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_1, LOG_LINE_2), null, null);
        assertThat(stats.topResources()).containsKey("/downloads/product_1");
        assertThat(stats.topResources().get("/downloads/product_1")).isEqualTo(2L);
    }

    @Test
    void countsResponseCodes() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_2, LOG_LINE_3), null, null);
        assertThat(stats.responseCodes()).containsKey(304);
        assertThat(stats.responseCodes()).containsKey(200);
        assertThat(stats.responseCodes()).containsKey(500);
    }

    @Test
    void filtersEntriesByFromDate() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_2, LOG_LINE_3),
            LocalDate.of(2015, 5, 18), null);
        assertThat(stats.totalRequestsCount()).isEqualTo(1);
    }

    @Test
    void filtersEntriesByToDate() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_2, LOG_LINE_3),
            null, LocalDate.of(2015, 5, 17));
        assertThat(stats.totalRequestsCount()).isEqualTo(2);
    }

    @Test
    void skipsInvalidLogLines() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, "this is garbage", LOG_LINE_2), null, null);
        assertThat(stats.totalRequestsCount()).isEqualTo(2);
    }

    @Test
    void collectsRequestsPerDate() throws IOException {
        Statistics stats = analyze(createLogFile(LOG_LINE_1, LOG_LINE_2, LOG_LINE_3), null, null);
        assertThat(stats.requestsPerDate()).containsKey(LocalDate.of(2015, 5, 17));
        assertThat(stats.requestsPerDate()).containsKey(LocalDate.of(2015, 5, 18));
        assertThat(stats.requestsPerDate().get(LocalDate.of(2015, 5, 17))).isEqualTo(2L);
    }

    private Statistics analyze(Path logFile, LocalDate from, LocalDate to) {
        CliArgs args = new CliArgs(List.of(logFile.toString()), "json", "out.json", from, to);
        return new LogAnalyzer(args).analyze();
    }

    private Path createLogFile(String... lines) throws IOException {
        Path file = tempDir.resolve("test.log");
        Files.writeString(file, String.join("\n", lines));
        return file;
    }
}
