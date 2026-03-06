package org.logAnalyzer.path;

import org.logAnalyzer.exception.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public final class PathResolver {

    private static final Logger log = Logger.getLogger(PathResolver.class.getName());
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".log", ".txt");

    private PathResolver() {}

    public static List<Path> resolve(List<String> rawPaths) {
        List<Path> result = new ArrayList<>();
        for (String rawPath : rawPaths) {
            result.addAll(resolveSingle(rawPath));
        }
        if (result.isEmpty()) {
            throw new InvalidArgumentException("No log files found for the given paths");
        }
        return result;
    }

    private static List<Path> resolveSingle(String rawPath) {
        if (isGlob(rawPath)) return resolveGlob(rawPath);
        return resolveExact(rawPath);
    }

    private static boolean isGlob(String path) {
        return path.contains("*") || path.contains("?")
            || path.contains("{") || path.contains("[");
    }

    private static List<Path> resolveGlob(String pattern) {
        Path parent = resolveGlobParent(pattern);
        String globPart = Path.of(pattern).getFileName().toString();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPart);

        List<Path> found = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) {
            for (Path entry : stream) {
                if (matcher.matches(entry.getFileName())) {
                    validateExtension(entry);
                    found.add(entry);
                    log.info("Resolved glob entry: " + entry);
                }
            }
        } catch (IOException e) {
            throw new InvalidArgumentException("Cannot read directory for glob pattern: " + pattern, e);
        }

        if (found.isEmpty()) {
            throw new InvalidArgumentException("No files matched glob pattern: " + pattern);
        }
        return found;
    }

    private static Path resolveGlobParent(String pattern) {
        Path path = Path.of(pattern);
        Path parent = path.getParent();
        return parent != null ? parent : Path.of(".");
    }

    private static List<Path> resolveExact(String rawPath) {
        Path path = Path.of(rawPath);
        if (!Files.exists(path)) {
            throw new InvalidArgumentException("File not found: " + rawPath);
        }
        if (!Files.isRegularFile(path)) {
            throw new InvalidArgumentException("Path is not a regular file: " + rawPath);
        }
        validateExtension(path);
        log.info("Resolved file: " + path);
        return List.of(path);
    }

    private static void validateExtension(Path path) {
        String fileName = path.getFileName().toString();
        boolean valid = SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
        if (!valid) {
            throw new InvalidArgumentException(
                "Unsupported file format: " + fileName + ". Supported: " + SUPPORTED_EXTENSIONS
            );
        }
    }
}
