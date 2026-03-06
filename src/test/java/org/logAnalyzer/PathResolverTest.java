package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.logAnalyzer.exception.InvalidArgumentException;
import org.logAnalyzer.path.PathResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathResolverTest {

    @TempDir
    Path tempDir;

    @Test
    void resolvesExistingLogFile() throws IOException {
        Path file = tempDir.resolve("access.log");
        Files.createFile(file);

        List<Path> result = PathResolver.resolve(List.of(file.toString()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(file);
    }

    @Test
    void resolvesExistingTxtFile() throws IOException {
        Path file = tempDir.resolve("access.txt");
        Files.createFile(file);

        List<Path> result = PathResolver.resolve(List.of(file.toString()));

        assertThat(result).hasSize(1);
    }

    @Test
    void throwsWhenFileNotFound() {
        assertThatThrownBy(() -> PathResolver.resolve(List.of("/nonexistent/path/access.log")))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("File not found");
    }

    @Test
    void throwsForUnsupportedExtension() throws IOException {
        Path file = tempDir.resolve("access.csv");
        Files.createFile(file);

        assertThatThrownBy(() -> PathResolver.resolve(List.of(file.toString())))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("Unsupported file format");
    }

    @Test
    void resolvesMultipleFilesWithGlob() throws IOException {
        Files.createFile(tempDir.resolve("server1.log"));
        Files.createFile(tempDir.resolve("server2.log"));
        Files.createFile(tempDir.resolve("other.txt"));

        String glob = tempDir.toString() + "/*.log";
        List<Path> result = PathResolver.resolve(List.of(glob));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getFileName().toString().endsWith(".log"));
    }

    @Test
    void throwsWhenGlobMatchesNothing() {
        String glob = tempDir.toString() + "/*.log";

        assertThatThrownBy(() -> PathResolver.resolve(List.of(glob)))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("No files matched glob pattern");
    }

    @Test
    void resolvesCombinationOfMultiplePaths() throws IOException {
        Path file1 = tempDir.resolve("a.log");
        Path file2 = tempDir.resolve("b.log");
        Files.createFile(file1);
        Files.createFile(file2);

        List<Path> result = PathResolver.resolve(List.of(file1.toString(), file2.toString()));

        assertThat(result).hasSize(2);
    }

    @Test
    void throwsWhenPathIsDirectory() throws IOException {
        assertThatThrownBy(() -> PathResolver.resolve(List.of(tempDir.toString())))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("not a regular file");
    }
}
