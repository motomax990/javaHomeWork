package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.logAnalyzer.model.LogEntry;
import org.logAnalyzer.parser.LogParser;

import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LogParserTest {

    private static final String VALID_LINE =
        "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3\"";

    @Test
    void parsesValidLogLine() {
        Optional<LogEntry> result = LogParser.parse(VALID_LINE);
        assertThat(result).isPresent();
        LogEntry entry = result.get();
        assertThat(entry.remoteAddr()).isEqualTo("93.180.71.3");
        assertThat(entry.method()).isEqualTo("GET");
        assertThat(entry.resource()).isEqualTo("/downloads/product_1");
        assertThat(entry.protocol()).isEqualTo("HTTP/1.1");
        assertThat(entry.status()).isEqualTo(304);
        assertThat(entry.bodyBytesSent()).isEqualTo(0L);
    }

    @Test
    void parsesTimestampCorrectly() {
        Optional<LogEntry> result = LogParser.parse(VALID_LINE);
        assertThat(result).isPresent();
        var time = result.get().timeLocal();
        assertThat(time.getYear()).isEqualTo(2015);
        assertThat(time.getMonthValue()).isEqualTo(5);
        assertThat(time.getDayOfMonth()).isEqualTo(17);
        assertThat(time.getOffset()).isEqualTo(ZoneOffset.UTC);
    }

    @Test
    void returnsEmptyForInvalidLine() {
        assertThat(LogParser.parse("not a log line")).isEmpty();
    }

    @Test
    void returnsEmptyForBlankLine() {
        assertThat(LogParser.parse("   ")).isEmpty();
    }

    @Test
    void parsesLargeBodySize() {
        String line = "192.168.1.1 - user [01/Jan/2024:12:00:00 +0000] \"POST /api/upload HTTP/2\" 200 1048576 \"https://example.com\" \"Mozilla/5.0\"";
        Optional<LogEntry> result = LogParser.parse(line);
        assertThat(result).isPresent();
        assertThat(result.get().bodyBytesSent()).isEqualTo(1048576L);
    }
}
