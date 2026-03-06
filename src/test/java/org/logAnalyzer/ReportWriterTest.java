package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.logAnalyzer.exception.InvalidArgumentException;
import org.logAnalyzer.writer.ReportWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesContentToFile() throws IOException {
        Path output = tempDir.resolve("report.json");

        ReportWriter.write("{ \"test\": true }", output.toString());

        assertThat(output).exists();
        assertThat(Files.readString(output)).isEqualTo("{ \"test\": true }");
    }

    @Test
    void throwsWhenFileAlreadyExists() throws IOException {
        Path output = tempDir.resolve("report.json");
        Files.createFile(output);

        assertThatThrownBy(() -> ReportWriter.write("content", output.toString()))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void createsParentDirectoriesIfNeeded() throws IOException {
        Path output = tempDir.resolve("subdir/nested/report.json");

        ReportWriter.write("data", output.toString());

        assertThat(output).exists();
        assertThat(Files.readString(output)).isEqualTo("data");
    }

    @Test
    void writesEmptyContent() throws IOException {
        Path output = tempDir.resolve("empty.json");

        ReportWriter.write("", output.toString());

        assertThat(output).exists();
        assertThat(Files.readString(output)).isEmpty();
    }
}
