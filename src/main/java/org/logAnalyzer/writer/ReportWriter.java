package org.logAnalyzer.writer;

import org.logAnalyzer.exception.InvalidArgumentException;
import org.logAnalyzer.exception.LogAnalyzerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class ReportWriter {

    private static final Logger log = Logger.getLogger(ReportWriter.class.getName());

    private ReportWriter() {}

    public static void write(String content, String outputPath) {
        Path path = Path.of(outputPath);

        if (Files.exists(path)) {
            throw new InvalidArgumentException("Output file already exists: " + outputPath);
        }

        Path parent = path.getParent();
        if (parent != null && Files.exists(parent) && !Files.isWritable(parent)) {
            throw new InvalidArgumentException("Directory is not writable: " + parent);
        }

        try {
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(path, content);
            log.info("Report written to: " + outputPath);
        } catch (IOException e) {
            throw new LogAnalyzerException("Failed to write report to: " + outputPath, e);
        }
    }
}
